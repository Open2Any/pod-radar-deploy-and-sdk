package io.podradar.examples;

import io.podradar.crawler.CrawlerAsyncClient;
import io.podradar.crawler.CrawlerClient;
import io.podradar.crawler.model.CrawlerKey;
import io.podradar.crawler.model.ItemsFilter;
import io.podradar.crawler.model.ItemsListResponse;
import io.podradar.crawler.model.RunsListResponse;
import io.podradar.crawler.model.SettingsResponse;
import io.podradar.sdk.error.PodRadarException;
import io.podradar.sdk.model.PageQuery;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutionException;

/**
 * 爬虫 SDK:异步 client 常用读接口。
 *
 * <pre>
 * mvn -pl examples exec:java -Dexec.mainClass=io.podradar.examples.CrawlerAsyncExample \
 *   -Dexec.args="dashboard"
 *
 * mvn -pl examples exec:java -Dexec.mainClass=io.podradar.examples.CrawlerAsyncExample \
 *   -Dexec.args="wrap"
 * </pre>
 */
public final class CrawlerAsyncExample {

    public static void main(String[] args) throws Exception {
        if (args.length < 1) {
            usage();
            System.exit(ExampleSupport.ERR_USAGE);
        }

        try (CrawlerAsyncClient crawler = CrawlerAsyncClient.builder()
                .endpoint(ExampleSupport.requiredEnv("POD_RADAR_CRAWLER_ENDPOINT"))
                .apiKey(ExampleSupport.requiredEnv("POD_RADAR_CRAWLER_KEY"))
                .userAgent(ExampleSupport.envOr("POD_RADAR_CRAWLER_USER_AGENT", "podradar-crawler-async-examples/0.1.0"))
                .build()) {
            switch (args[0]) {
                case "dashboard":
                    dashboard(crawler);
                    break;
                case "items":
                    items(crawler);
                    break;
                case "wrap":
                    wrapExistingSyncClient();
                    break;
                default:
                    usage();
                    System.exit(ExampleSupport.ERR_USAGE);
            }
        }
    }

    private static void dashboard(CrawlerAsyncClient crawler) {
        CompletableFuture<SettingsResponse> settings = crawler.getSettings();
        CompletableFuture<RunsListResponse> runs = crawler.listRuns(PageQuery.of(5, 0));
        CompletableFuture<List<CrawlerKey>> keys = crawler.listKeys();

        try {
            CompletableFuture.allOf(settings, runs, keys).join();
            System.out.println("settings:");
            System.out.println("  syncEnabled=" + settings.get().settings().syncEnabled()
                    + " interval=" + settings.get().settings().syncIntervalMinutes());
            System.out.println("runs:");
            runs.get().runs().forEach(ExampleSupport::printRunSummary);
            System.out.println("keys:");
            for (CrawlerKey k : keys.get()) {
                System.out.printf("  id=%d name=%s prefix=%s revokedAt=%s%n",
                        k.id(), k.name(), k.prefix(), ExampleSupport.nvl(k.revokedAt()));
            }
        } catch (CompletionException e) {
            printAsyncFailure("dashboard", e.getCause());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        } catch (ExecutionException e) {
            printAsyncFailure("dashboard", e.getCause());
        }
    }

    private static void items(CrawlerAsyncClient crawler) {
        try {
            ItemsListResponse items = crawler.listItems(ItemsFilter.empty()
                    .withPage(PageQuery.of(10, 0))).get();
            System.out.printf("items total=%d returned=%d%n", items.total(), items.items().size());
            items.items().forEach(ExampleSupport::printCrawlerItem);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        } catch (ExecutionException e) {
            printAsyncFailure("items", e.getCause());
        }
    }

    private static void wrapExistingSyncClient() throws Exception {
        try (CrawlerClient sync = ExampleSupport.crawlerBuilder().build();
             CrawlerAsyncClient async = CrawlerAsyncClient.wrap(sync)) {
            RunsListResponse runs = async.listRuns(PageQuery.of(1, 0)).get();
            System.out.println("wrapped sync client, runs returned=" + runs.runs().size());
        }
    }

    private static void printAsyncFailure(String label, Throwable cause) {
        if (cause instanceof PodRadarException) {
            System.err.printf("[%s] crawler call failed: %s%n", label, cause.getMessage());
            return;
        }
        throw new RuntimeException(cause);
    }

    private static void usage() {
        System.err.println("usage:");
        System.err.println("  CrawlerAsyncExample dashboard");
        System.err.println("  CrawlerAsyncExample items");
        System.err.println("  CrawlerAsyncExample wrap");
    }

    private CrawlerAsyncExample() {}
}
