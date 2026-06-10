package io.podradar.sdk;

import com.github.tomakehurst.wiremock.WireMockServer;
import io.podradar.sdk.error.PodRadarNotFoundException;
import io.podradar.sdk.model.ImageDto;
import io.podradar.sdk.model.SearchRequest;
import io.podradar.sdk.model.SearchResponse;
import io.podradar.sdk.model.UploadRequest;
import io.podradar.sdk.model.UploadResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.util.concurrent.ExecutionException;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.junit.jupiter.api.Assertions.*;

class PodRadarAsyncClientTest {

    private WireMockServer server;
    private PodRadarAsyncClient client;

    @BeforeEach
    void setUp() {
        server = new WireMockServer(wireMockConfig().dynamicPort());
        server.start();
        client = PodRadarAsyncClient.builder()
                .endpoint("http://localhost:" + server.port())
                .apiKey("test-key")
                .build();
    }

    @AfterEach
    void tearDown() {
        if (client != null) client.close();
        if (server != null) server.stop();
    }

    @Test
    void searchAsyncResolves() throws Exception {
        server.stubFor(post(urlPathEqualTo("/api/v1/search"))
                .willReturn(aResponse().withStatus(200).withBody(
                        "{\"k\":3,\"min_score\":0.0,\"model\":\"m\",\"results\":[" +
                        "{\"id\":1,\"score\":0.9,\"url\":\"https://x/a.jpg\"}]}")));

        SearchResponse resp = client.search(
                SearchRequest.fromUrl(URI.create("https://x/cat.jpg"), 3)).get();

        assertEquals(1, resp.results().size());
        assertEquals(1L, resp.results().get(0).imageId());

        // k goes on the query string, not the JSON body.
        server.verify(postRequestedFor(urlPathEqualTo("/api/v1/search"))
                .withQueryParam("k", equalTo("3")));
    }

    @Test
    void uploadAsyncResolves() throws Exception {
        server.stubFor(post(urlEqualTo("/api/v1/images"))
                .willReturn(aResponse().withStatus(200).withBody(
                        "{\"image\":{\"id\":99,\"url\":\"\",\"status\":\"fetched\"}," +
                        "\"created\":true,\"queued_count\":0,\"queued_models\":[]}")));

        UploadResponse resp = client.upload(
                UploadRequest.fromUrl(URI.create("https://x/y.jpg"))).get();

        assertEquals(99L, resp.image().imageId());
    }

    @Test
    void getImageAsyncResolves() throws Exception {
        server.stubFor(get(urlEqualTo("/api/v1/image/55"))
                .willReturn(aResponse().withStatus(200).withBody(
                        "{\"id\":55,\"url\":\"https://x/y.jpg\",\"status\":\"embedded\"}")));

        ImageDto img = client.getImage(55).get();
        assertEquals(55L, img.imageId());
    }

    @Test
    void getImageAsyncFailsOn404() {
        server.stubFor(get(urlEqualTo("/api/v1/image/404"))
                .willReturn(aResponse().withStatus(404).withBody("{\"error\":\"missing\"}")));

        ExecutionException ex = assertThrows(ExecutionException.class,
                () -> client.getImage(404).get());
        assertInstanceOf(PodRadarNotFoundException.class, ex.getCause());
    }

}
