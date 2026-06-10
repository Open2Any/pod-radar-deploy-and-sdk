package io.podradar.sdk.internal;

import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

class MultipartTest {

    @Test
    void contentTypeIncludesBoundary() {
        Multipart m = new Multipart();
        assertTrue(m.contentType().startsWith("multipart/form-data; boundary="));
    }

    @Test
    void bodyContainsFieldAndFile() {
        Multipart m = new Multipart()
                .addField("model", "dashscope_v1")
                .addFile("image", "cat.jpg", "image/jpeg", new byte[]{1, 2, 3});
        String body = new String(m.body(), StandardCharsets.UTF_8);
        assertTrue(body.contains("Content-Disposition: form-data; name=\"model\""));
        assertTrue(body.contains("dashscope_v1"));
        assertTrue(body.contains("Content-Disposition: form-data; name=\"image\"; filename=\"cat.jpg\""));
        assertTrue(body.contains("Content-Type: image/jpeg"));
    }

    @Test
    void bodyEndsWithClosingBoundary() {
        Multipart m = new Multipart()
                .addField("k", "v");
        String body = new String(m.body(), StandardCharsets.UTF_8);
        String boundary = m.contentType().substring("multipart/form-data; boundary=".length());
        assertTrue(body.endsWith("--" + boundary + "--\r\n"), "body: " + body);
    }

    @Test
    void fileWithoutContentTypeDefaultsToOctetStream() {
        Multipart m = new Multipart().addFile("f", "x.bin", null, new byte[]{0});
        String body = new String(m.body(), StandardCharsets.UTF_8);
        assertTrue(body.contains("Content-Type: application/octet-stream"));
    }

    @Test
    void escapesQuoteInFilename() {
        Multipart m = new Multipart().addFile("f", "weird\"name.jpg", "image/jpeg", new byte[]{0});
        String body = new String(m.body(), StandardCharsets.UTF_8);
        assertTrue(body.contains("filename=\"weird\\\"name.jpg\""));
    }
}
