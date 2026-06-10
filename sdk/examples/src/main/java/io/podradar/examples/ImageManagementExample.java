package io.podradar.examples;

import io.podradar.sdk.PodRadarClient;
import io.podradar.sdk.error.PodRadarException;
import io.podradar.sdk.model.ImageDto;
import io.podradar.sdk.model.ImagesListResponse;
import io.podradar.sdk.model.PageQuery;

import java.util.List;

/**
 * pod图像向量化系统 SDK:图片列表和详情。
 *
 * <pre>
 * mvn -pl examples exec:java -Dexec.mainClass=io.podradar.examples.ImageManagementExample \
 *   -Dexec.args="list --limit 20 --offset 0"
 *
 * mvn -pl examples exec:java -Dexec.mainClass=io.podradar.examples.ImageManagementExample \
 *   -Dexec.args="get 12345"
 * </pre>
 */
public final class ImageManagementExample {

    public static void main(String[] args) {
        if (args.length < 1) {
            usage();
            System.exit(ExampleSupport.ERR_USAGE);
        }
        String cmd = args[0];

        try (PodRadarClient client = ExampleSupport.podRadarBuilder().build()) {
            switch (cmd) {
                case "list":
                    listImages(client, args);
                    break;
                case "get":
                    getImage(client, args);
                    break;
                default:
                    usage();
                    System.exit(ExampleSupport.ERR_USAGE);
            }
        } catch (PodRadarException e) {
            ExampleSupport.exitPodRadarException("image op failed", e);
        }
    }

    private static void listImages(PodRadarClient client, String[] args) {
        int limit = ExampleSupport.intOption(args, "--limit", 24);
        int offset = ExampleSupport.intOption(args, "--offset", 0);
        ImagesListResponse resp = client.listImages(PageQuery.of(limit, offset));
        System.out.printf("total=%d canonicalTotal=%d duplicateTotal=%d includeDuplicates=%s limit=%d offset=%d%n",
                resp.total(), resp.canonicalTotal(), resp.duplicateTotal(),
                resp.includeDuplicates(), resp.limit(), resp.offset());
        for (ImageDto img : resp.images()) {
            ExampleSupport.printImage(img);
        }
    }

    private static void getImage(PodRadarClient client, String[] args) {
        List<String> pos = ExampleSupport.positional(args);
        if (pos.size() < 2) {
            usage();
            System.exit(ExampleSupport.ERR_USAGE);
        }
        ImageDto img = client.getImage(ExampleSupport.requiredLong(pos.get(1), "id"));
        ExampleSupport.printImage(img);
    }

    private static void usage() {
        System.err.println("usage:");
        System.err.println("  ImageManagementExample list [--limit N] [--offset N]");
        System.err.println("  ImageManagementExample get <id>");
    }

    private ImageManagementExample() {}
}
