package io.podradar.examples;

import io.podradar.sdk.PodRadarClient;
import io.podradar.sdk.error.PodRadarException;
import io.podradar.sdk.model.SearchRequest;
import io.podradar.sdk.model.SearchResponse;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.util.List;

/**
 * pod图像向量化系统 SDK:同步搜索的全部输入模式。
 *
 * <pre>
 * export POD_RADAR_ENDPOINT="https://api.podradar.example.com"
 * export POD_RADAR_API_KEY="pod-radar_..."
 *
 * mvn -pl examples exec:java -Dexec.mainClass=io.podradar.examples.SearchExample \
 *   -Dexec.args="file /path/to/query.jpg --k 24 --min-score 0.85 --text 红色卫衣"
 *
 * mvn -pl examples exec:java -Dexec.mainClass=io.podradar.examples.SearchExample \
 *   -Dexec.args="url https://example.com/query.jpg"
 *
 * mvn -pl examples exec:java -Dexec.mainClass=io.podradar.examples.SearchExample \
 *   -Dexec.args="text 复古黑色棒球帽"
 * </pre>
 */
public final class SearchExample {

    public static void main(String[] args) throws IOException {
        if (args.length < 2) {
            usage();
            System.exit(ExampleSupport.ERR_USAGE);
        }

        int k = ExampleSupport.intOption(args, "--k", 24);
        double minScore = ExampleSupport.doubleOption(args, "--min-score", 0.0);
        String extraText = ExampleSupport.option(args, "--text", null);

        SearchRequest req = buildRequest(args, k);
        if (extraText != null && req.mode() != SearchRequest.Mode.TEXT) {
            req.withText(extraText);
        }
        if (minScore > 0) {
            req.withMinScore(minScore);
        }

        try (PodRadarClient client = ExampleSupport.podRadarBuilder()
                .retryOnServerError(false)
                .build()) {
            SearchResponse resp = client.search(req);
            ExampleSupport.printSearchResponse(resp);
        } catch (PodRadarException e) {
            ExampleSupport.exitPodRadarException("search failed", e);
        }
    }

    private static SearchRequest buildRequest(String[] args, int k) throws IOException {
        String mode = args[0];
        List<String> pos = ExampleSupport.positional(args);
        if (pos.size() < 2) {
            usage();
            System.exit(ExampleSupport.ERR_USAGE);
        }
        String value = pos.get(1);
        switch (mode) {
            case "file":
                return SearchRequest.fromFile(ExampleSupport.requiredFile(value), k);
            case "bytes": {
                File f = ExampleSupport.requiredFile(value);
                String mime = ExampleSupport.option(args, "--mime", guessMime(f.getName()));
                return SearchRequest.fromBytes(Files.readAllBytes(f.toPath()), mime, k);
            }
            case "url":
                return SearchRequest.fromUrl(URI.create(value), k);
            case "text":
                String text = String.join(" ", pos.subList(1, pos.size()));
                return SearchRequest.fromText(text, k);
            default:
                usage();
                System.exit(ExampleSupport.ERR_USAGE);
                return null;
        }
    }

    private static String guessMime(String filename) {
        String n = filename.toLowerCase();
        if (n.endsWith(".png")) return "image/png";
        if (n.endsWith(".webp")) return "image/webp";
        if (n.endsWith(".gif")) return "image/gif";
        return "image/jpeg";
    }

    private static void usage() {
        System.err.println("usage:");
        System.err.println("  SearchExample file <image-file> [--k N] [--min-score M] [--text TEXT]");
        System.err.println("  SearchExample bytes <image-file> [--mime image/jpeg] [--k N] [--min-score M]");
        System.err.println("  SearchExample url <https-url> [--k N] [--min-score M] [--text TEXT]");
        System.err.println("  SearchExample text <query text...> [--k N] [--min-score M]");
    }

    private SearchExample() {}
}
