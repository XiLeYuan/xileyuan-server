#!/bin/bash
# deploy-prod.sh

set -e

# 配置
APP_NAME="marry"
VERSION="0.0.1-SNAPSHOT"
JAR_FILE="$APP_NAME-$VERSION.jar"
PROFILE="prod"
BACKUP_DIR="/opt/backup"
APP_DIR="/opt/app"
CONFIG_DIR="$APP_DIR/config"

echo "=== 生产环境部署 ==="

# 1. 备份当前版本
echo "1. 备份当前版本..."
if [ -f "$APP_DIR/$JAR_FILE" ]; then
    mkdir -p $BACKUP_DIR
    BACKUP_FILE="$BACKUP_DIR/$JAR_FILE.$(date +%Y%m%d%H%M%S)"
    cp $APP_DIR/$JAR_FILE $BACKUP_FILE
    echo "✅ 备份完成: $BACKUP_FILE"
fi

# 2. 停止当前服务
echo "2. 停止当前服务..."
sudo systemctl stop $APP_NAME 2>/dev/null || true
sleep 5

# 3. 确保进程停止
echo "3. 清理残留进程..."
pkill -f "java.*$JAR_FILE" 2>/dev/null || true
sleep 2

# 4. 部署新版本
echo "4. 部署新版本..."
chmod +x $APP_DIR/$JAR_FILE

# 5. 确保配置目录存在
echo "5. 检查配置文件..."
mkdir -p $CONFIG_DIR
if [ ! -f "$CONFIG_DIR/application-$PROFILE.yml" ]; then
    echo "⚠️  未找到外部配置文件，将使用JAR内配置"
fi

# 6. 启动服务
echo "6. 启动服务..."
echo "⚠️  正在启动服务" $JAR_FILE
sudo   nohup java -Xms512m -Xmx1024m -jar $JAR_FILE --spring.profiles.active=prod > app.log 2>&1 &


# 7. 健康检查
echo "8. 开始检查日志"
sleep 30
if curl -s http://localhost:8080/api/test/health | grep -q "UP"; then
    echo "✅ 健康检查通过"
else
    echo "⚠️  健康检查失败"
    curl -s http://localhost:8080/api/test/health
fi

echo "=== 部署完成 ==="