# pod-radar crawler Nginx reverse proxy

Crawler is deployed on its own domain. Internally the crawler API and web UI
listen on localhost:

| 外部路径 | 内部服务 |
|---|---|
| `/` | redirect to `/crawler` |
| `/crawler/*` | crawler web `127.0.0.1:5175` |
| `/crawler-api/*` | crawler API `127.0.0.1:3002` |
| `/_next/*` | crawler web Next.js assets |
| `/crawler/_next/*` | compatibility for older crawler images built with `/crawler` asset prefix |

## 系统 Nginx

```bash
sudo cp deploy/nginx/pod-radar.conf /etc/nginx/conf.d/pod-radar.conf
sudo nginx -t
sudo systemctl reload nginx
```

## Docker Nginx

```bash
docker compose -f docker-compose.nginx.yml up -d
```

## 应用重启

Nginx 起好后，重启 crawler services：

```bash
pm2 restart pod-radar-crawler-api pod-radar-crawler-web --update-env
pm2 save
```
