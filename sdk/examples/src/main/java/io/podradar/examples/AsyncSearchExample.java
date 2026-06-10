package io.podradar.examples;

import io.podradar.sdk.PodRadarAsyncClient;
import io.podradar.sdk.PodRadarClient;
import io.podradar.sdk.error.PodRadarException;
import io.podradar.sdk.error.PodRadarRateLimitException;
import io.podradar.sdk.error.PodRadarValidationException;
import io.podradar.sdk.model.ImageDto;
import io.podradar.sdk.model.ImagesListResponse;
import io.podradar.sdk.model.PageQuery;
import io.podradar.sdk.model.SearchRequest;
import io.podradar.sdk.model.SearchResponse;
import io.podradar.sdk.model.UploadRequest;
import io.podradar.sdk.model.UploadResponse;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutionException;

/**
 * pod图像向量化系统 SDK:异步 client 覆盖 search/upload/list/get。
 *
 * <pre>
 * mvn -pl examples exec:java -Dexec.mainClass=io.podradar.examples.AsyncSearchExample \
 *   -Dexec.args="search q1.jpg q2.jpg"
 *
 * mvn -pl examples exec:java -Dexec.mainClass=io.podradar.examples.AsyncSearchExample \
 *   -Dexec.args="upload a.jpg b.jpg"
 *
 * mvn -pl examples exec:java -Dexec.mainClass=io.podradar.examples.AsyncSearchExample \
 *   -Dexec.args="list-and-get"
 * </pre>
 */
public final class AsyncSearchExample {

    public static void main(String[] args) throws Exception {
        if (args.length < 1) {
            usage();
            System.exit(ExampleSupport.ERR_USAGE);
        }

        String cmd = args[0];
        try (PodRadarAsyncClient client = PodRadarAsyncClient.builder()
                .endpoint(ExampleSupport.requiredEnv("POD_RADAR_ENDPOINT"))
                .apiKey(ExampleSupport.requiredEnv("POD_RADAR_API_KEY"))
                .userAgent(ExampleSupport.envOr("POD_RADAR_USER_AGENT", "podradar-async-examples/0.1.0"))
                .build()) {
            switch (cmd) {
                case "search":
                    searchMany(client, args);
                    break;
                case "upload":
                    uploadMany(client, args);
                    break;
                case "list-and-get":
                    listAndGet(client);
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

    private static void searchMany(PodRadarAsyncClient client, String[] args) {
        List<String> pos = ExampleSupport.positional(args);
        List<String> files = pos.subList(1, pos.size());
        if (files.isEmpty()) {
            System.err.println("search needs at least one file");
            System.exit(ExampleSupport.ERR_USAGE);
        }
        List<CompletableFuture<SearchResponse>> futures = new ArrayList<>();
        for (String file : files) {
            File f = ExampleSupport.requiredFile(file);
            futures.add(client.search(SearchRequest.fromFile(f, 24).withMinScore(0.85)));
        }
        waitAll(futures);
        for (int i = 0; i < files.size(); i++) {
            try {
                System.out.println("[" + files.get(i) + "]");
                ExampleSupport.printSearchResponse(futures.get(i).get());
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException(e);
            } catch (ExecutionException e) {
                printAsyncFailure(files.get(i), e.getCause());
            }
        }
    }

    private static void uploadMany(PodRadarAsyncClient client, String[] args) {
        List<String> pos = ExampleSupport.positional(args);
        List<String> files = pos.subList(1, pos.size());
        if (files.isEmpty()) {
            System.err.println("upload needs at least one file");
            System.exit(ExampleSupport.ERR_USAGE);
        }
        List<CompletableFuture<UploadResponse>> futures = new ArrayList<>();
        for (String file : files) {
            File f = ExampleSupport.requiredFile(file);
            futures.add(client.upload(UploadRequest.fromFile(f)
                    .withSource("examples-async")
                    .withTitle(f.getName())));
        }
        waitAll(futures);
        for (int i = 0; i < files.size(); i++) {
            try {
                UploadResponse r = futures.get(i).get();
                System.out.printf("[%s] job=%s created=%s queued=%d%n",
                        files.get(i), r.jobId(), r.created(), r.queuedCount());
                ExampleSupport.printImage(r.image());
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException(e);
            } catch (ExecutionException e) {
                printAsyncFailure(files.get(i), e.getCause());
            }
        }
    }

    private static void listAndGet(PodRadarAsyncClient client) {
        try {
            ImagesListResponse list = client.listImages(PageQuery.of(5, 0)).get();
            System.out.printf("images total=%d returned=%d%n", list.total(), list.images().size());
            for (ImageDto img : list.images()) {
                ExampleSupport.printImage(img);
            }
            if (!list.images().isEmpty()) {
                ImageDto first = client.getImage(list.images().get(0).imageId()).get();
                System.out.println("first image detail:");
                ExampleSupport.printImage(first);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        } catch (ExecutionException e) {
            printAsyncFailure("list-and-get", e.getCause());
        }
    }

    private static void wrapExistingSyncClient() throws Exception {
        try (PodRadarClient sync = ExampleSupport.podRadarBuilder().build();
             PodRadarAsyncClient async = PodRadarAsyncClient.wrap(sync)) {
            ImagesListResponse list = async.listImages(PageQuery.of(1, 0)).get();
            System.out.println("wrapped sync client, images returned=" + list.images().size());
        }
    }

    private static void waitAll(List<? extends CompletableFuture<?>> futures) {
        try {
            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
        } catch (CompletionException ignored) {
            // Individual futures are inspected below so one failure does not hide other results.
        }
    }

    private static void printAsyncFailure(String label, Throwable cause) {
        if (cause instanceof PodRadarRateLimitException) {
            PodRadarRateLimitException rl = (PodRadarRateLimitException) cause;
            System.err.printf("[%s] rate limited; retry after %ds%n", label, rl.retryAfterSeconds());
            return;
        }
        if (cause instanceof PodRadarValidationException) {
            PodRadarValidationException ve = (PodRadarValidationException) cause;
            System.err.printf("[%s] bad request: %s request_id=%s details=%s%n",
                    label, ve.error(), ve.requestId(), ve.details());
            return;
        }
        if (cause instanceof PodRadarException) {
            System.err.printf("[%s] podradar call failed: %s%n", label, cause.getMessage());
            return;
        }
        throw new RuntimeException(cause);
    }

    private static void usage() {
        System.err.println("usage:");
        System.err.println("  AsyncSearchExample search <image-file> [<image-file>...]");
        System.err.println("  AsyncSearchExample upload <image-file> [<image-file>...]");
        System.err.println("  AsyncSearchExample list-and-get");
        System.err.println("  AsyncSearchExample wrap");
    }

    private AsyncSearchExample() {}
}
