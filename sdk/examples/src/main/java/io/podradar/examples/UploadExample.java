package io.podradar.examples;

import io.podradar.sdk.PodRadarClient;
import io.podradar.sdk.error.PodRadarException;
import io.podradar.sdk.model.UploadRequest;
import io.podradar.sdk.model.UploadResponse;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * pod图像向量化系统 SDK:单图上传的全部输入模式。
 *
 * <pre>
 * mvn -pl examples exec:java -Dexec.mainClass=io.podradar.examples.UploadExample \
 *   -Dexec.args="file /path/to/a.jpg --source snexdiy --source-id SKU-1 --title 红色帽子 --tags hat,red"
 *
 * mvn -pl examples exec:java -Dexec.mainClass=io.podradar.examples.UploadExample \
 *   -Dexec.args="url https://example.com/a.jpg --source external"
 * </pre>
 */
public final class UploadExample {

    public static void main(String[] args) throws IOException {
        if (args.length < 2) {
            usage();
            System.exit(ExampleSupport.ERR_USAGE);
        }

        UploadRequest req = buildRequest(args);
        applyMetadata(req, args);

        try (PodRadarClient client = ExampleSupport.podRadarBuilder()
                .requestTimeout(java.time.Duration.ofSeconds(60))
                .build()) {
            UploadResponse resp = client.upload(req);
            System.out.printf("job=%s created=%s queuedCount=%d queuedModels=%s%n",
                    resp.jobId(), resp.created(), resp.queuedCount(), resp.queuedModels());
            ExampleSupport.printImage(resp.image());
        } catch (PodRadarException e) {
            ExampleSupport.exitPodRadarException("upload failed", e);
        }
    }

    private static UploadRequest buildRequest(String[] args) throws IOException {
        List<String> pos = ExampleSupport.positional(args);
        String mode = pos.get(0);
        String value = pos.get(1);
        switch (mode) {
            case "file":
                return UploadRequest.fromFile(ExampleSupport.requiredFile(value));
            case "bytes": {
                File f = ExampleSupport.requiredFile(value);
                String mime = ExampleSupport.option(args, "--mime", guessMime(f.getName()));
                return UploadRequest.fromBytes(Files.readAllBytes(f.toPath()), mime);
            }
            case "url":
                return UploadRequest.fromUrl(URI.create(value));
            default:
                usage();
                System.exit(ExampleSupport.ERR_USAGE);
                return null;
        }
    }

    private static void applyMetadata(UploadRequest req, String[] args) {
        String source = ExampleSupport.option(args, "--source", "examples");
        String sourceId = ExampleSupport.option(args, "--source-id", null);
        String title = ExampleSupport.option(args, "--title", null);
        String tags = ExampleSupport.option(args, "--tags", null);

        req.withSource(source);
        if (sourceId != null) req.withSourceId(sourceId);
        if (title != null) req.withTitle(title);
        if (tags != null) {
            req.withTags(Arrays.asList(tags.split(",")));
        }

        Map<String, Object> meta = new LinkedHashMap<>();
        meta.put("example", "UploadExample");
        meta.put("source_cli", source);
        req.withMeta(meta);
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
        System.err.println("  UploadExample file <image-file> [--source S] [--source-id ID] [--title T] [--tags a,b]");
        System.err.println("  UploadExample bytes <image-file> [--mime image/jpeg] [--source S]");
        System.err.println("  UploadExample url <https-url> [--source S] [--source-id ID] [--title T]");
    }

    private UploadExample() {}
}
