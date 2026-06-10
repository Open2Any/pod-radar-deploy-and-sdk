package io.podradar.sdk.internal;

import com.github.tomakehurst.wiremock.WireMockServer;
import io.podradar.sdk.error.PodRadarAuthException;
import io.podradar.sdk.error.PodRadarServerException;
import io.podradar.sdk.error.PodRadarValidationException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.junit.jupiter.api.Assertions.*;

class HttpExecutorTest {

    private WireMockServer server;
    private HttpExecutor exec;

    @BeforeEach
    void setUp() {
        server = new WireMockServer(wireMockConfig().dynamicPort());
        server.start();
        SdkConfig cfg = SdkConfig.builder()
                .endpoint("http://localhost:" + server.port())
                .apiKey("k")
                .connectTimeout(Duration.ofSeconds(2))
                .requestTimeout(Duration.ofSeconds(5))
                .build();
        exec = new HttpExecutor(cfg);
    }

    @AfterEach
    void tearDown() {
        if (server != null) server.stop();
    }

    @Test
    void getJsonReturnsBodyOn200() {
        server.stubFor(get(urlEqualTo("/api/v1/ping"))
                .willReturn(aResponse().withStatus(200).withBody("{\"ok\":true}")));
        String body = exec.getJson("/api/v1/ping");
        assertEquals("{\"ok\":true}", body);
        server.verify(getRequestedFor(urlEqualTo("/api/v1/ping"))
                .withHeader("X-API-Key", equalTo("k"))
                .withHeader("User-Agent", matching(".*podradar.*")));
    }

    @Test
    void postJsonSendsBody() {
        server.stubFor(post(urlEqualTo("/api/v1/echo"))
                .willReturn(aResponse().withStatus(200).withBody("{\"ok\":true}")));
        exec.postJson("/api/v1/echo", "{\"a\":1}");
        server.verify(postRequestedFor(urlEqualTo("/api/v1/echo"))
                .withRequestBody(equalTo("{\"a\":1}"))
                .withHeader("Content-Type", matching("application/json.*")));
    }

    @Test
    void throwsAuthExceptionOn401() {
        server.stubFor(get(urlEqualTo("/api/v1/x"))
                .willReturn(aResponse().withStatus(401)
                        .withHeader("X-Request-Id", "r-1")
                        .withBody("{\"error\":\"no key\",\"request_id\":\"r-1\"}")));
        PodRadarAuthException ex = assertThrows(PodRadarAuthException.class,
                () -> exec.getJson("/api/v1/x"));
        assertEquals(401, ex.statusCode());
        assertEquals("no key", ex.error());
        assertEquals("r-1", ex.requestId());
    }

    @Test
    void throwsValidationOn400() {
        server.stubFor(get(urlEqualTo("/api/v1/x"))
                .willReturn(aResponse().withStatus(400)
                        .withBody("{\"error\":\"bad\",\"details\":{\"f\":\"top_k\"}}")));
        PodRadarValidationException ex = assertThrows(PodRadarValidationException.class,
                () -> exec.getJson("/api/v1/x"));
        assertEquals("top_k", ex.details().get("f"));
    }

    @Test
    void doesNotRetryByDefaultOn5xx() {
        server.stubFor(get(urlEqualTo("/api/v1/x"))
                .willReturn(aResponse().withStatus(503).withBody("{\"error\":\"down\"}")));
        assertThrows(PodRadarServerException.class, () -> exec.getJson("/api/v1/x"));
        server.verify(1, getRequestedFor(urlEqualTo("/api/v1/x")));
    }

    @Test
    void retriesOn5xxWhenEnabled() {
        SdkConfig cfg = SdkConfig.builder()
                .endpoint("http://localhost:" + server.port())
                .apiKey("k")
                .retryOnServerError(true)
                .maxRetries(2)
                .build();
        HttpExecutor retryExec = new HttpExecutor(cfg);

        server.stubFor(get(urlEqualTo("/api/v1/y"))
                .inScenario("flap")
                .whenScenarioStateIs("Started")
                .willReturn(aResponse().withStatus(503).withBody("{\"error\":\"down\"}"))
                .willSetStateTo("once"));
        server.stubFor(get(urlEqualTo("/api/v1/y"))
                .inScenario("flap")
                .whenScenarioStateIs("once")
                .willReturn(aResponse().withStatus(200).withBody("{\"ok\":true}")));

        String body = retryExec.getJson("/api/v1/y");
        assertEquals("{\"ok\":true}", body);
        server.verify(2, getRequestedFor(urlEqualTo("/api/v1/y")));
    }

    @Test
    void postMultipartSendsBoundary() {
        server.stubFor(post(urlEqualTo("/api/v1/upload"))
                .willReturn(aResponse().withStatus(200).withBody("{\"ok\":true}")));
        Multipart m = new Multipart()
                .addField("model", "dashscope_v1")
                .addFile("image", "x.jpg", "image/jpeg", new byte[]{9, 8, 7});
        exec.postMultipart("/api/v1/upload", m);
        server.verify(postRequestedFor(urlEqualTo("/api/v1/upload"))
                .withHeader("Content-Type", matching("multipart/form-data; boundary=.*")));
    }

    @Test
    void getJsonAsyncResolves() throws Exception {
        server.stubFor(get(urlEqualTo("/api/v1/a"))
                .willReturn(aResponse().withStatus(200).withBody("{\"ok\":true}")));
        String body = exec.getJsonAsync("/api/v1/a").get();
        assertEquals("{\"ok\":true}", body);
    }
}
