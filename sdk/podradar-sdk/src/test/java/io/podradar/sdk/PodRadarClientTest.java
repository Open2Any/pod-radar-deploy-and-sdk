package io.podradar.sdk;

import com.github.tomakehurst.wiremock.WireMockServer;
import io.podradar.sdk.error.PodRadarNotFoundException;
import io.podradar.sdk.model.BatchSearchAsyncResponse;
import io.podradar.sdk.model.BatchSearchRequest;
import io.podradar.sdk.model.BatchUploadRequest;
import io.podradar.sdk.model.BatchUploadResponse;
import io.podradar.sdk.model.ImageDto;
import io.podradar.sdk.model.ImagesListResponse;
import io.podradar.sdk.model.PageQuery;
import io.podradar.sdk.model.SearchJobItemsResponse;
import io.podradar.sdk.model.SearchJobStatus;
import io.podradar.sdk.model.SearchRequest;
import io.podradar.sdk.model.SearchResponse;
import io.podradar.sdk.model.UploadRequest;
import io.podradar.sdk.model.UploadResponse;
import io.podradar.sdk.model.WriteJobItemsResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.util.Arrays;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.junit.jupiter.api.Assertions.*;

class PodRadarClientTest {

    private WireMockServer server;
    private PodRadarClient client;

    @BeforeEach
    void setUp() {
        server = new WireMockServer(wireMockConfig().dynamicPort());
        server.start();
        client = PodRadarClient.builder()
                .endpoint("http://localhost:" + server.port())
                .apiKey("test-key")
                .build();
    }

    @AfterEach
    void tearDown() {
        if (client != null) client.close();
        if (server != null) server.stop();
    }

    // ───── search ─────────────────────────────────────────────────────

    @Test
    void searchByUrlSendsJsonBody() {
        server.stubFor(post(urlPathEqualTo("/api/v1/search"))
                .willReturn(aResponse().withStatus(200).withBody(
                        "{\"k\":5,\"min_score\":0.5,\"model\":\"dashscope_v1\",\"results\":[" +
                        "{\"id\":42,\"score\":0.91,\"url\":\"https://x/y.jpg\"}]}")));

        SearchResponse resp = client.search(
                SearchRequest.fromUrl(URI.create("https://example.com/cat.jpg"), 5)
                        .withMinScore(0.5));

        assertEquals(5, resp.k());
        assertEquals(0.5, resp.minScore(), 1e-9);
        assertEquals("dashscope_v1", resp.model());
        assertEquals(1, resp.results().size());
        assertEquals(42L, resp.results().get(0).imageId());
        assertEquals(0.91, resp.results().get(0).score(), 1e-9);

        // k / min_score go on the query string; only image_url is in the body.
        server.verify(postRequestedFor(urlPathEqualTo("/api/v1/search"))
                .withHeader("Content-Type", matching("application/json.*"))
                .withQueryParam("k", equalTo("5"))
                .withQueryParam("min_score", equalTo("0.5"))
                .withRequestBody(matchingJsonPath("$.image_url", equalTo("https://example.com/cat.jpg"))));
    }

    @Test
    void searchByBytesSendsMultipart() {
        server.stubFor(post(urlPathEqualTo("/api/v1/search"))
                .willReturn(aResponse().withStatus(200).withBody(
                        "{\"k\":3,\"min_score\":0.0,\"model\":\"m\",\"results\":[]}")));

        byte[] data = new byte[]{(byte) 0xFF, (byte) 0xD8, (byte) 0xFF, 0x00};
        SearchResponse resp = client.search(SearchRequest.fromBytes(data, "image/jpeg", 3));

        assertEquals(3, resp.k());
        assertTrue(resp.results().isEmpty());
        server.verify(postRequestedFor(urlPathEqualTo("/api/v1/search"))
                .withQueryParam("k", equalTo("3"))
                .withHeader("Content-Type", matching("multipart/form-data; boundary=.*")));
    }

    @Test
    void searchByTextOmitsImageFields() {
        server.stubFor(post(urlPathEqualTo("/api/v1/search"))
                .willReturn(aResponse().withStatus(200).withBody(
                        "{\"k\":10,\"min_score\":0.0,\"model\":\"m\",\"results\":[]}")));

        client.search(SearchRequest.fromText("red sneaker", 10));

        server.verify(postRequestedFor(urlPathEqualTo("/api/v1/search"))
                .withQueryParam("k", equalTo("10"))
                .withRequestBody(matchingJsonPath("$.text", equalTo("red sneaker"))));
    }

    @Test
    void searchBatchPostsUrls() {
        server.stubFor(post(urlEqualTo("/api/v1/search/batch"))
                .willReturn(aResponse().withStatus(202).withBody(
                        "{\"search_job_id\":7,\"total\":2,\"enqueued\":2,\"status\":\"queued\"," +
                        "\"status_url\":\"/api/v1/search/jobs/7\"}")));

        BatchSearchRequest req = BatchSearchRequest.fromUrls(Arrays.asList(
                URI.create("https://x/a.jpg"),
                URI.create("https://x/b.jpg"))).withK(8);
        BatchSearchAsyncResponse resp = client.searchBatch(req);

        assertEquals(7L, resp.searchJobId());
        assertEquals(2, resp.total());
        assertEquals("queued", resp.status());
    }

    @Test
    void getSearchJobReturnsStatus() {
        server.stubFor(get(urlEqualTo("/api/v1/search/jobs/9"))
                .willReturn(aResponse().withStatus(200).withBody(
                        "{\"search_job_id\":9,\"status\":\"running\"," +
                        "\"counts\":{\"total\":10,\"completed\":4,\"failed\":1}}")));

        SearchJobStatus s = client.getSearchJob(9);
        assertEquals(9L, s.searchJobId());
        assertEquals("running", s.status());
        assertEquals(10, s.total());
        assertEquals(4, s.completed());
        assertEquals(1, s.failed());
    }

    @Test
    void listSearchJobItemsPassesPaging() {
        server.stubFor(get(urlPathEqualTo("/api/v1/search/jobs/9/items"))
                .willReturn(aResponse().withStatus(200).withBody(
                        "{\"total\":1,\"limit\":50,\"offset\":0,\"items\":[" +
                        "{\"item_index\":0,\"image_url\":\"https://x/a.jpg\"," +
                        "\"status\":\"completed\",\"hits\":[]}]}")));

        SearchJobItemsResponse r = client.listSearchJobItems(9, PageQuery.of(50, 0));
        assertEquals(1, r.total());
        assertEquals(50, r.limit());
        server.verify(getRequestedFor(urlPathEqualTo("/api/v1/search/jobs/9/items"))
                .withQueryParam("limit", equalTo("50"))
                .withQueryParam("offset", equalTo("0")));
    }

    // ───── upload ─────────────────────────────────────────────────────

    @Test
    void uploadByUrlSendsJson() {
        server.stubFor(post(urlEqualTo("/api/v1/images"))
                .willReturn(aResponse().withStatus(200).withBody(
                        "{\"job_id\":\"j-1\"," +
                        "\"image\":{\"id\":11,\"url\":\"https://x/a.jpg\",\"status\":\"fetched\"}," +
                        "\"created\":true,\"queued_count\":1,\"queued_models\":[\"dashscope_v1\"]}")));

        UploadResponse resp = client.upload(
                UploadRequest.fromUrl(URI.create("https://example.com/cat.jpg"))
                        .withSource("seed")
                        .withSourceId("c-1")
                        .withTitle("cat"));

        assertEquals("j-1", resp.jobId());
        assertTrue(resp.created());
        assertEquals(11L, resp.image().imageId());
        assertEquals(1, resp.queuedCount());
        server.verify(postRequestedFor(urlEqualTo("/api/v1/images"))
                .withHeader("Content-Type", matching("application/json.*"))
                .withRequestBody(matchingJsonPath("$.source", equalTo("seed"))));
    }

    @Test
    void uploadByBytesSendsMultipart() {
        server.stubFor(post(urlEqualTo("/api/v1/images"))
                .willReturn(aResponse().withStatus(200).withBody(
                        "{\"image\":{\"id\":22,\"url\":\"\",\"status\":\"fetched\"}," +
                        "\"created\":true,\"queued_count\":0,\"queued_models\":[]}")));

        byte[] data = new byte[]{1, 2, 3, 4, 5};
        UploadResponse resp = client.upload(UploadRequest.fromBytes(data, "image/png"));
        assertEquals(22L, resp.image().imageId());
        server.verify(postRequestedFor(urlEqualTo("/api/v1/images"))
                .withHeader("Content-Type", matching("multipart/form-data; boundary=.*")));
    }

    @Test
    void uploadBatchSendsItemsArray() {
        server.stubFor(post(urlEqualTo("/api/v1/images/batch"))
                .willReturn(aResponse().withStatus(200).withBody(
                        "{\"job_id\":\"j-2\",\"total\":2,\"succeeded\":2,\"failed\":0,\"duplicate\":0," +
                        "\"results\":[" +
                        "{\"image_url\":\"https://x/a.jpg\",\"image_id\":1,\"created\":true,\"duplicate\":false}," +
                        "{\"image_url\":\"https://x/b.jpg\",\"image_id\":2,\"created\":true,\"duplicate\":false}]}")));

        BatchUploadRequest req = BatchUploadRequest.fromUrls(Arrays.asList(
                URI.create("https://x/a.jpg"), URI.create("https://x/b.jpg")));
        BatchUploadResponse resp = client.uploadBatch(req);

        assertEquals("j-2", resp.jobId());
        assertEquals(2, resp.total());
        assertEquals(2, resp.results().size());
        server.verify(postRequestedFor(urlEqualTo("/api/v1/images/batch"))
                .withRequestBody(matchingJsonPath("$.items[0].image_url", equalTo("https://x/a.jpg"))));
    }

    @Test
    void listJobItemsPagesItems() {
        server.stubFor(get(urlPathEqualTo("/api/v1/images/jobs/j-3/items"))
                .willReturn(aResponse().withStatus(200).withBody(
                        "{\"total\":0,\"limit\":20,\"offset\":0,\"items\":[]}")));

        WriteJobItemsResponse r = client.listJobItems("j-3", PageQuery.of(20, 0));
        assertEquals(0, r.total());
        assertTrue(r.items().isEmpty());
    }

    @Test
    void retryFailedJobPostsEmptyBody() {
        server.stubFor(post(urlEqualTo("/api/v1/images/jobs/j-4/retry-failed"))
                .willReturn(aResponse().withStatus(200).withBody(
                        "{\"job_id\":\"j-4\",\"total\":0,\"succeeded\":0,\"failed\":0,\"duplicate\":0,\"results\":[]}")));

        BatchUploadResponse r = client.retryFailedJob("j-4");
        assertEquals("j-4", r.jobId());
        server.verify(postRequestedFor(urlEqualTo("/api/v1/images/jobs/j-4/retry-failed"))
                .withRequestBody(equalTo("{}")));
    }

    // ───── image reads / list ─────────────────────────────────────────

    @Test
    void getImageReturnsDto() {
        server.stubFor(get(urlEqualTo("/api/v1/image/123"))
                .willReturn(aResponse().withStatus(200).withBody(
                        "{\"id\":123,\"source\":\"seed\",\"url\":\"https://x/y.jpg\"," +
                        "\"width\":800,\"height\":600,\"bytes\":12345,\"mime\":\"image/jpeg\"," +
                        "\"status\":\"embedded\",\"tags\":[\"a\",\"b\"]}")));

        ImageDto img = client.getImage(123);
        assertEquals(123L, img.imageId());
        assertEquals(800, img.width());
        assertEquals(600, img.height());
        assertEquals(12345L, img.bytes());
        assertEquals("embedded", img.status());
        assertEquals(2, img.tags().size());
    }

    @Test
    void getImageThrowsOn404() {
        server.stubFor(get(urlEqualTo("/api/v1/image/999"))
                .willReturn(aResponse().withStatus(404).withBody("{\"error\":\"image not found\"}")));
        PodRadarNotFoundException ex = assertThrows(PodRadarNotFoundException.class,
                () -> client.getImage(999));
        assertEquals(404, ex.statusCode());
    }

    @Test
    void listImagesPaged() {
        server.stubFor(get(urlPathEqualTo("/api/v1/images"))
                .willReturn(aResponse().withStatus(200).withBody(
                        "{\"images\":[" +
                        "{\"id\":1,\"url\":\"https://x/1.jpg\",\"status\":\"embedded\"}]," +
                        "\"total\":1,\"canonical_total\":1,\"duplicate_total\":0," +
                        "\"include_duplicates\":false,\"limit\":10,\"offset\":0}")));

        ImagesListResponse r = client.listImages(PageQuery.of(10, 0));
        assertEquals(1, r.total());
        assertEquals(1, r.images().size());
        assertFalse(r.includeDuplicates());
        server.verify(getRequestedFor(urlPathEqualTo("/api/v1/images"))
                .withQueryParam("limit", equalTo("10"))
                .withQueryParam("offset", equalTo("0")));
    }

    @Test
    void apiKeyHeaderSentOnEveryCall() {
        server.stubFor(get(urlEqualTo("/api/v1/image/1"))
                .willReturn(aResponse().withStatus(200).withBody(
                        "{\"id\":1,\"url\":\"\",\"status\":\"embedded\"}")));
        client.getImage(1);
        server.verify(getRequestedFor(urlEqualTo("/api/v1/image/1"))
                .withHeader("X-API-Key", equalTo("test-key")));
    }
}
