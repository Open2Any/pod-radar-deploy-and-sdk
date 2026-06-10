# hihumbird 爬虫系统 · 部署 + SDK

独立交付包：部署爬虫系统 + 接入爬虫 Java SDK。与图搜主系统无关（独立库 / 独立桶 / 独立鉴权）。

## 部署（Docker Hub 拉取，无需构建）

爬虫系统需要 browserless（商品图无头浏览器渲染），先起它再起应用：

```bash
cd deploy
cp compose.crawler.env.example .env     # 按需改：CRAWLER_IMAGE、库、S3、hihumbird 账号、CRAWLER_HISTORY_ORDER_DAYS 等
docker compose -f docker-compose.browserless.yml up -d
docker compose -f compose.crawler.yml pull
docker compose -f compose.crawler.yml up -d
```

- 镜像默认 `codedevin/pod-radar-crawler:v1.0.0`（`.env` 里 `CRAWLER_IMAGE` 可覆盖）。
- 完整环境变量表（含 `HIHUMBIRD_FETCHER_REPLICAS`、`CRAWLER_HISTORY_ORDER_DAYS` 等）：见 [`deploy/README.crawler.md`](deploy/README.crawler.md)。
- 反向代理参考：[`deploy/nginx/`](deploy/nginx/)。

## SDK

- 现成 jar + Demo：见 [`sdk-dist/`](sdk-dist/)。爬虫 SDK **需两个 jar**（`crawler-sdk-0.1.0.jar` + `sdk-core-0.1.0.jar`）一起放 classpath，详见该目录 `README.md`。
- 源码 / 自行构建：见仓库根 [`../sdk/`](../sdk/)。

## 历史订单门（>90 天）

订单项创建超过 `CRAWLER_HISTORY_ORDER_DAYS`（默认 90）天的老订单：自动同步只爬生产图+源图、跳过商品图（无头浏览器）与面单；批量「重试失败」也只重试该天数内的订单。详见 `deploy/README.crawler.md`。
