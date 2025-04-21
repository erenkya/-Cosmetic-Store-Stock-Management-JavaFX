#!/bin/bash

# Uygulama adı
APP_NAME="StockManagementABC"
JAR_FILE="target/StockManagement-1.0-SNAPSHOT.jar"

# .app dizin yapısını oluştur
mkdir -p "${APP_NAME}.app/Contents/MacOS"
mkdir -p "${APP_NAME}.app/Contents/Resources"
mkdir -p "${APP_NAME}.app/Contents/Java"

# JAR dosyasını kopyala
cp ${JAR_FILE} "${APP_NAME}.app/Contents/Java/"

# Info.plist dosyasını oluştur
cat > "${APP_NAME}.app/Contents/Info.plist" << EOF
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE plist PUBLIC "-//Apple//DTD PLIST 1.0//EN" "http://www.apple.com/DTDs/PropertyList-1.0.dtd">
<plist version="1.0">
<dict>
    <key>CFBundleExecutable</key>
    <string>launcher</string>
    <key>CFBundleIconFile</key>
    <string>AppIcon</string>
    <key>CFBundleIdentifier</key>
    <string>com.stockmanagement.app</string>
    <key>CFBundleName</key>
    <string>${APP_NAME}</string>
    <key>CFBundlePackageType</key>
    <string>APPL</string>
    <key>CFBundleShortVersionString</key>
    <string>1.0</string>
    <key>CFBundleSignature</key>
    <string>????</string>
    <key>CFBundleVersion</key>
    <string>1</string>
    <key>LSMinimumSystemVersion</key>
    <string>10.10</string>
    <key>NSHighResolutionCapable</key>
    <true/>
</dict>
</plist>
EOF

# Başlatıcı script oluştur
cat > "${APP_NAME}.app/Contents/MacOS/launcher" << EOF
#!/bin/bash
cd "\$(dirname "\$0")"
cd ../Java
java -jar StockManagement-1.0-SNAPSHOT.jar
EOF

# Başlatıcı scripti çalıştırılabilir yap
chmod +x "${APP_NAME}.app/Contents/MacOS/launcher"

echo "Uygulama paketi oluşturuldu: ${APP_NAME}.app"
