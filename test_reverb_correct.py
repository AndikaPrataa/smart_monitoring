#!/usr/bin/env python3
"""
Test Reverb WebSocket dengan URL Pusher protocol yang BENAR
"""

import socket
import time

def test_websocket_handshake():
    """
    Test WebSocket handshake dengan URL yang benar untuk Reverb
    """
    
    # ============================================================
    # REVERB_APP_KEY dari .env
    # ============================================================
    REVERB_APP_KEY = "efksfxfxxfrzuetobo5f"
    HOST = "127.0.0.1"
    PORT = 8080
    
    # ============================================================
    # URL YANG BENAR untuk Reverb (Pusher protocol)
    # ============================================================
    # Format: /app/{REVERB_APP_KEY}?protocol=7&client=js&version=8.4.0&flash=false
    PATH = f"/app/{REVERB_APP_KEY}?protocol=7&client=js&version=8.4.0&flash=false"
    
    print("=" * 70)
    print("REVERB WEBSOCKET TEST - Pusher Protocol")
    print("=" * 70)
    print()
    print(f"🔧 Configuration:")
    print(f"   REVERB_APP_KEY: {REVERB_APP_KEY}")
    print(f"   Host: {HOST}")
    print(f"   Port: {PORT}")
    print(f"   Path: {PATH}")
    print()
    print(f"📡 URL: ws://{HOST}:{PORT}{PATH}")
    print()
    print("-" * 70)
    
    try:
        # Create socket
        sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        print(f"📦 Creating socket... ", end="", flush=True)
        print("✅")
        
        # Connect to Reverb server
        print(f"🔌 Connecting to {HOST}:{PORT}... ", end="", flush=True)
        sock.connect((HOST, PORT))
        print("✅")
        
        # ============================================================
        # WebSocket Handshake Request
        # ============================================================
        handshake_request = (
            f"GET {PATH} HTTP/1.1\r\n"
            f"Host: {HOST}:{PORT}\r\n"
            f"Upgrade: websocket\r\n"
            f"Connection: Upgrade\r\n"
            f"Sec-WebSocket-Key: dGhlIHNhbXBsZSBub25jZQ==\r\n"
            f"Sec-WebSocket-Version: 13\r\n"
            f"\r\n"
        )
        
        print(f"🤝 Sending WebSocket handshake... ", end="", flush=True)
        sock.sendall(handshake_request.encode())
        print("✅")
        
        # Receive response
        print(f"📥 Receiving response... ", end="", flush=True)
        response = sock.recv(4096).decode('utf-8', errors='ignore')
        print("✅")
        
        print()
        print("-" * 70)
        print("📋 SERVER RESPONSE:")
        print("-" * 70)
        print(response)
        print("-" * 70)
        print()
        
        # ============================================================
        # Check response
        # ============================================================
        if "101 Switching Protocols" in response:
            print("✅✅✅ SUCCESS! ✅✅✅")
            print()
            print("🎉 WebSocket upgrade BERHASIL!")
            print("   Server merespons dengan: HTTP/1.1 101 Switching Protocols")
            print()
            print("✅ Ini berarti:")
            print("   • Reverb server BERFUNGSI dengan sempurna")
            print("   • URL path /app/{key} BENAR")
            print("   • Reverb siap terima koneksi WebSocket real-time")
            print("   • Pusher protocol sudah compatible")
            print()
            print("🎯 Next step:")
            print("   1. Buka Postman")
            print(f"   2. Buat WebSocket request ke:")
            print(f"      ws://127.0.0.1:8080{PATH}")
            print("   3. Klik 'Connect' - harusnya berhasil ✅")
            
            return True
            
        elif "404" in response:
            print("❌ ERROR: HTTP/1.1 404 Not Found")
            print()
            print("⚠️ Ini berarti:")
            print("   • URL path mungkin salah")
            print("   • Reverb_APP_KEY mungkin tidak match")
            print("   • Path seharusnya: /app/{REVERB_APP_KEY}")
            
            return False
            
        else:
            print("⚠️ Response tidak expected:")
            first_line = response.split('\r\n')[0]
            print(f"   {first_line}")
            
            return False
            
    except ConnectionRefusedError:
        print("❌ ERROR: Connection Refused")
        print(f"   Reverb server tidak running di {HOST}:{PORT}")
        print(f"   Pastikan: php artisan reverb:start --debug sedang berjalan")
        return False
        
    except Exception as e:
        print(f"❌ ERROR: {e}")
        return False
        
    finally:
        sock.close()
        print()
        print("=" * 70)

if __name__ == "__main__":
    success = test_websocket_handshake()
    exit(0 if success else 1)
