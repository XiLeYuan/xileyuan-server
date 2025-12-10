# 🛠️nginx 服务
1. 配置文件目录
   - /etc/nginx/conf.d
   ````
   server {
    listen       80;
    server_name jiehunba.net.cn www.jiehunba.net.cn.com;
    root         /usr/share/nginx/html;
    index        index.html index.htm;

     # 所有请求都转发到 Spring Boot 应用
    location / {
        proxy_pass http://127.0.0.1:8080;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;

        # 超时设置
        proxy_connect_timeout 30s;
        proxy_send_timeout 30s;
        proxy_read_timeout 30s;
    }
    # 访问日志
    access_log  /var/log/nginx/host.access.log;


    # 错误页面
    error_page  404              /404.html;
    error_page  500 502 503 504  /50x.html;
    
    location = /50x.html {
        root   /usr/share/nginx/html;
    }
}

# maven 打包正式上线版本

mvn clean package -Pprod -DskipTests # 打包生产环境版本（不包含本地数据库配置）


mvn clean package -Pprod -Dmaven.test.skip=true # 或者完全跳过测试


mvn clean package -Pprod -DskipTests -X  # 详细构建信息


# Nginx管理
sudo systemctl start nginx      # 启动
sudo systemctl stop nginx       # 停止
sudo systemctl restart nginx    # 重启
sudo systemctl reload nginx     # 重载配置
sudo systemctl status nginx     # 状态

# 查看日志
sudo tail -f /var/log/nginx/access.log    # Nginx访问日志
sudo tail -f /var/log/nginx/error.log     # Nginx错误日志
sudo tail -f /var/log/mysqld.log          # MySQL日志
sudo tail -f  /usr/local/marrydb/app.log  # 结婚吧日志





# 🛠️ 结婚吧业务
1. **业务目录:**

- cd /opt/app


2. **部署命令:**

- sudo sh deploy-prod.sh





