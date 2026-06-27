package com.example.energymonitoringapp.view.screen.common.splash

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.energymonitoringapp.R
import com.example.energymonitoringapp.view.components.common.EcoUnilaLogo
import com.example.energymonitoringapp.view.theme.DotInactive
import com.example.energymonitoringapp.view.theme.EnergyMonitoringAppTheme
import com.example.energymonitoringapp.view.theme.GreenBg
import com.example.energymonitoringapp.view.theme.GreenDark
import com.example.energymonitoringapp.view.theme.GreenPrimary
import com.example.energymonitoringapp.view.theme.TextPrimary
import com.example.energymonitoringapp.view.theme.TextSecondary
import com.example.energymonitoringapp.view.theme.White
import com.example.energymonitoringapp.view.theme.rememberResponsiveDimensions
import kotlinx.coroutines.delay

data class BannerSlide(
    val image: Int,
    val background: Color,
    val title: String,
    val subtitle: String
)

private val bannerSlides = listOf(
    BannerSlide(R.drawable.electriccar, White, "Mobilitas Hijau", "Dukung kampus ramah lingkungan"),
    BannerSlide(R.drawable.bicycle, White, "Hemat Energi", "Pantau konsumsi listrik secara real-time"),
    BannerSlide(R.drawable.pasture, White, "Lingkungan Sehat", "Monitor kualitas udara & parameter IEQ")
)

@Composable
fun splashScreen(
    onAdminClick: () -> Unit = {},
    onTeknisiClick: () -> Unit = {}
) {
    val r = rememberResponsiveDimensions()
    var currentSlide by remember { mutableIntStateOf(0) }

    LaunchedEffect(Unit) {
        while (true) {
            delay(3000)
            currentSlide = (currentSlide + 1) % bannerSlides.size
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(GreenBg)
            .verticalScroll(rememberScrollState())
    ) {
        BannerSection(
            currentSlide = currentSlide,
            modifier = Modifier
                .fillMaxWidth()
                .height(r.splashBannerHeight)
        )
        BottomSection(
            onAdminClick = onAdminClick,
            onTeknisiClick = onTeknisiClick,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun BannerSection(currentSlide: Int, modifier: Modifier = Modifier) {
    Box(modifier = modifier) {
        AnimatedContent(
            targetState = currentSlide,
            transitionSpec = {
                slideInHorizontally(animationSpec = tween(600), initialOffsetX = { it }) togetherWith
                        slideOutHorizontally(animationSpec = tween(600), targetOffsetX = { -it })
            },
            label = "banner_slide"
        ) { index ->
            val s = bannerSlides[index]
            Box(
                modifier = Modifier.fillMaxSize().background(s.background),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = s.image),
                    contentDescription = null,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.fillMaxWidth().fillMaxHeight(0.85f)
                )
            }
        }

        Row(
            modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 14.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            bannerSlides.indices.forEach { i ->
                val isActive = i == currentSlide
                val width by animateDpAsState(
                    targetValue = if (isActive) 24.dp else 8.dp,
                    animationSpec = tween(300),
                    label = "dot_width"
                )
                Box(
                    modifier = Modifier.height(8.dp).width(width)
                        .clip(CircleShape).background(if (isActive) GreenDark else DotInactive)
                )
            }
        }
    }
}

@Composable
fun BottomSection(
    modifier: Modifier = Modifier,
    onAdminClick: () -> Unit = {},
    onTeknisiClick: () -> Unit = {}
) {
    val r = rememberResponsiveDimensions()
    Column(
        modifier = modifier.padding(horizontal = r.horizontalPadding, vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.logoeco),
            contentDescription = "Logo EcoUNILA",
            contentScale = ContentScale.Fit,
            modifier = Modifier.size(r.splashLogoSize)
        )
        EcoUnilaLogo(
            modifier = Modifier.fillMaxWidth(),
            fontSize = r.splashTitleFontSize
        )
        Text(
            text = "Energi Cerdas untuk Kampus Hijau",
            fontSize = r.fontLabel,
            fontWeight = FontWeight.Medium,
            color = GreenDark,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(20.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier.padding(vertical = 20.dp, horizontal = r.horizontalPadding),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Masuk sebagai",
                    fontSize = r.fontLabel,
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(16.dp))

                // Tombol Admin dan Teknisi — responsif
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Admin
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        val avatarSize = (r.iconSizeLarge.value + 24).dp
                        Box(
                            modifier = Modifier.size(avatarSize).clip(CircleShape)
                                .background(Color(0xFFE3F2FD)),
                            contentAlignment = Alignment.Center
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.bisnisman),
                                contentDescription = null,
                                contentScale = ContentScale.Fit,
                                modifier = Modifier.size(avatarSize * 0.7f)
                            )
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        Button(
                            onClick = onAdminClick,
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary, contentColor = White),
                            modifier = Modifier.widthIn(min = 90.dp, max = 130.dp).height(r.buttonHeight)
                        ) {
                            Text(text = "Admin", fontSize = r.buttonFontSize, fontWeight = FontWeight.SemiBold)
                        }
                    }

                    Text(text = "atau", fontSize = r.fontLabel, color = TextSecondary)

                    // Teknisi
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        val avatarSize = (r.iconSizeLarge.value + 24).dp
                        Box(
                            modifier = Modifier.size(avatarSize).clip(CircleShape)
                                .background(Color(0xFFFFF3E0)),
                            contentAlignment = Alignment.Center
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.soldier),
                                contentDescription = null,
                                contentScale = ContentScale.Fit,
                                modifier = Modifier.size(avatarSize * 0.7f)
                            )
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        Button(
                            onClick = onTeknisiClick,
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary, contentColor = White),
                            modifier = Modifier.widthIn(min = 90.dp, max = 130.dp).height(r.buttonHeight)
                        ) {
                            Text(text = "Teknisi", fontSize = r.buttonFontSize, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Preview(showBackground = true)
@Composable
fun splashScreenPreview() {
    EnergyMonitoringAppTheme {
        splashScreen()
    }
}
