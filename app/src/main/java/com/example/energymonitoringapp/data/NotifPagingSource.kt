package com.example.energymonitoringapp.data

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.energymonitoringapp.data.api.ApiService
import com.example.energymonitoringapp.data.local.NotifEntity
import com.example.energymonitoringapp.data.local.SensorDao
import com.example.energymonitoringapp.data.response.NotifAdminItem
import retrofit2.HttpException
import java.io.IOException

class AdminNotifPagingSource(
    private val api: ApiService,
    private val dao: SensorDao,
    private val status: String? = null
) : PagingSource<Int, NotifAdminItem>() {

    override fun getRefreshKey(state: PagingState<Int, NotifAdminItem>): Int? {
        return state.anchorPosition?.let { anchor ->
            state.closestPageToPosition(anchor)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchor)?.nextKey?.minus(1)
        }
    }
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, NotifAdminItem> {
        val page = params.key ?: 1

        return try {
            val response = api.getAdminNotifications(page = page, perPage = 20, status = status)
            if (!response.isSuccessful) {
                return LoadResult.Error(HttpException(response))
            }

            val body     = response.body()
            val items    = body?.data?.filterNotNull() ?: emptyList()
            val lastPage = body?.lastPage ?: 1

            if (items.isNotEmpty()) {
                val entities = items.map { item ->
                    NotifEntity(
                        id         = item.id ?: 0,
                        role       = "admin",
                        kategori   = item.kategori,
                        sensor     = item.sensor,
                        title      = item.title,
                        level      = item.level,
                        value      = item.value,
                        unit       = item.unit,
                        message    = item.message,
                        lokasi     = item.lokasi,
                        status     = item.status,
                        assignedTo = item.assignedTo,
                        assignedAt = item.assignedAt,
                        timestamp  = item.timestamp
                    )
                }
                dao.saveAllNotif(entities)
            }

            LoadResult.Page(
                data    = items,
                prevKey = if (page == 1) null else page - 1,
                nextKey = if (page >= lastPage) null else page + 1
            )
        } catch (e: IOException) {
            if (page == 1) {
                loadFromLocalDb()
            } else {
                LoadResult.Page(data = emptyList(), prevKey = null, nextKey = null)
            }
        } catch (e: HttpException) {
            LoadResult.Error(e)
        }
    }

    private suspend fun loadFromLocalDb(): LoadResult<Int, NotifAdminItem> {
        return try {
            val entities = dao.getNotifByRoleOnce("admin")

            val filtered = if (status != null) {
                entities.filter { it.status == status }
            } else {
                entities
            }

            val items = filtered.map { entity ->
                NotifAdminItem(
                    id         = entity.id,
                    kategori   = entity.kategori,
                    sensor     = entity.sensor,
                    title      = entity.title,
                    level      = entity.level,
                    value      = entity.value,
                    unit       = entity.unit,
                    message    = entity.message,
                    lokasi     = entity.lokasi,
                    status     = entity.status,
                    assignedTo = entity.assignedTo,
                    assignedAt = entity.assignedAt,
                    timestamp  = entity.timestamp
                )
            }

            LoadResult.Page(data = items, prevKey = null, nextKey = null)
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }
}
