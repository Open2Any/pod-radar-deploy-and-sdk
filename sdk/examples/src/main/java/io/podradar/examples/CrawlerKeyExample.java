package io.podradar.examples;

import io.podradar.crawler.CrawlerClient;
import io.podradar.crawler.model.CrawlerKey;
import io.podradar.crawler.model.CreateKeyResponse;
import io.podradar.crawler.model.MeResponse;
import io.podradar.sdk.error.PodRadarException;

import java.util.List;

/**
 * 爬虫 SDK:当前身份 + API key 管理。
 *
 * <pre>
 * mvn -pl examples exec:java -Dexec.mainClass=io.podradar.examples.CrawlerKeyExample -Dexec.args="me"
 * mvn -pl examples exec:java -Dexec.mainClass=io.podradar.examples.CrawlerKeyExample -Dexec.args="list"
 * mvn -pl examples exec:java -Dexec.mainClass=io.podradar.examples.CrawlerKeyExample -Dexec.args="create ops-bob"
 * mvn -pl examples exec:java -Dexec.mainClass=io.podradar.examples.CrawlerKeyExample -Dexec.args="revoke 12"
 * </pre>
 */
public final class CrawlerKeyExample {

    public static void main(String[] args) {
        if (args.length < 1) {
            usage();
            System.exit(ExampleSupport.ERR_USAGE);
        }

        try (CrawlerClient crawler = ExampleSupport.crawlerBuilder().build()) {
            switch (args[0]) {
                case "me":
                    me(crawler);
                    break;
                case "list":
                    list(crawler);
                    break;
                case "create":
                    create(crawler, args);
                    break;
                case "revoke":
                    revoke(crawler, args);
                    break;
                case "delete":
                    delete(crawler, args);
                    break;
                default:
                    usage();
                    System.exit(ExampleSupport.ERR_USAGE);
            }
        } catch (PodRadarException e) {
            ExampleSupport.exitPodRadarException("crawler key op failed", e);
        }
    }

    private static void me(CrawlerClient crawler) {
        MeResponse me = crawler.me();
        System.out.printf("name=%s scopes=%s%n", me.name(), me.scopes());
    }

    private static void list(CrawlerClient crawler) {
        List<CrawlerKey> keys = crawler.listKeys();
        System.out.println("keys:");
        for (CrawlerKey k : keys) {
            System.out.printf("  id=%d name=%s prefix=%s createdAt=%s lastUsedAt=%s useCount=%d revokedAt=%s%n",
                    k.id(), k.name(), k.prefix(), ExampleSupport.nvl(k.createdAt()),
                    ExampleSupport.nvl(k.lastUsedAt()), k.useCount(), ExampleSupport.nvl(k.revokedAt()));
        }
    }

    private static void create(CrawlerClient crawler, String[] args) {
        List<String> pos = ExampleSupport.positional(args);
        if (pos.size() < 2) {
            usage();
            System.exit(ExampleSupport.ERR_USAGE);
        }
        CreateKeyResponse created = crawler.createKey(pos.get(1));
        System.out.printf("created id=%d name=%s prefix=%s createdAt=%s%n",
                created.id(), created.name(), created.prefix(), ExampleSupport.nvl(created.createdAt()));
        System.out.println("plaintext (save now; server will not return it again): " + created.plaintext());
    }

    private static void revoke(CrawlerClient crawler, String[] args) {
        long id = idArg(args);
        crawler.revokeKey(id);
        System.out.println("revoked id=" + id);
    }

    private static void delete(CrawlerClient crawler, String[] args) {
        long id = idArg(args);
        if (!ExampleSupport.hasFlag(args, "--confirm-delete")) {
            System.err.println("refusing hard delete without --confirm-delete; normal ops should use revoke");
            System.exit(ExampleSupport.ERR_USAGE);
        }
        crawler.deleteKey(id);
        System.out.println("deleted id=" + id);
    }

    private static long idArg(String[] args) {
        List<String> pos = ExampleSupport.positional(args);
        if (pos.size() < 2) {
            usage();
            System.exit(ExampleSupport.ERR_USAGE);
        }
        return ExampleSupport.requiredLong(pos.get(1), "key-id");
    }

    private static void usage() {
        System.err.println("usage:");
        System.err.println("  CrawlerKeyExample me");
        System.err.println("  CrawlerKeyExample list");
        System.err.println("  CrawlerKeyExample create <name>");
        System.err.println("  CrawlerKeyExample revoke <key-id>");
        System.err.println("  CrawlerKeyExample delete <key-id> --confirm-delete");
    }

    private CrawlerKeyExample() {}
}
