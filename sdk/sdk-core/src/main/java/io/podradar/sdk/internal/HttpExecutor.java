package io.podradar.sdk.internal;

import io.podradar.sdk.error.PodRadarException;
import io.podradar.sdk.error.PodRadarNetworkException;
import io.podradar.sdk.error.PodRadarServerException;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

/**
 * Thin wrapper around {@link java.net.http.HttpClient}. Adds X-API-Key + User-Agent,
 * unwraps non-2xx responses via {@link HttpErrorMapper}, and optionally retries on
 * {@link PodRadarServerException} with exponential backoff.
 */
public final class HttpExecutor {
    private final SdkConfig cfg;
    private final HttpClient client;

    public HttpExecutor(SdkConfig cfg) {
        this.cfg = cfg;
        this.client = HttpClient.newBuilder()
                .connectTimeout(cfg.connectTimeout())
                .followRedirects(HttpClient.Redirect.NEVER)
                .build();
    }

    public String getJson(String path) {
        return execute(buildJsonRequest(path, "GET", null));
    }

    public String postJson(String path, String body) {
        return execute(buildJsonRequest(path, "POST", body));
    }

    public String putJson(String path, String body) {
        return execute(buildJsonRequest(path, "PUT", body));
    }

    public String deleteJson(String path) {
        return execute(buildJsonRequest(path, "DELETE", null));
    }

    public String postMultipart(String path, Multipart form) {
        HttpRequest req = baseRequest(path)
                .header("Content-Type", form.contentType())
                .POST(HttpRequest.BodyPublishers.ofByteArray(form.body()))
                .build();
        return execute(req);
    }

    public CompletableFuture<String> getJsonAsync(String path) {
        return executeAsync(buildJsonRequest(path, "GET", null));
    }

    public CompletableFuture<String> postJsonAsync(String path, String body) {
        return executeAsync(buildJsonRequest(path, "POST", body));
    }

    public CompletableFuture<String> putJsonAsync(String path, String body) {
        return executeAsync(buildJsonRequest(path, "PUT", body));
    }

    public CompletableFuture<String> deleteJsonAsync(String path) {
        return executeAsync(buildJsonRequest(path, "DELETE", null));
    }

    public CompletableFuture<String> postMultipartAsync(String path, Multipart form) {
        HttpRequest req = baseRequest(path)
                .header("Content-Type", form.contentType())
                .POST(HttpRequest.BodyPublishers.ofByteArray(form.body()))
                .build();
        return executeAsync(req);
    }

    private HttpRequest buildJsonRequest(String path, String method, String body) {
        HttpRequest.Builder b = baseRequest(path)
                .header("Accept", "application/json");
        if (body != null) {
            b.header("Content-Type", "application/json; charset=utf-8");
        }
        HttpRequest.BodyPublisher publisher = body == null
                ? HttpRequest.BodyPublishers.noBody()
                : HttpRequest.BodyPublishers.ofString(body, StandardCharsets.UTF_8);
        switch (method) {
            case "GET":    return b.GET().build();
            case "POST":   return b.POST(publisher).build();
            case "PUT":    return b.PUT(publisher).build();
            case "DELETE": return b.DELETE().build();
            default: throw new IllegalArgumentException("unsupported method " + method);
        }
    }

    private HttpRequest.Builder baseRequest(String path) {
        URI uri = resolve(cfg.endpoint(), path);
        return HttpRequest.newBuilder(uri)
                .timeout(cfg.requestTimeout())
                .header("X-API-Key", cfg.apiKey())
                .header("User-Agent", cfg.userAgent());
    }

    private static URI resolve(URI base, String path) {
        String b = base.toString();
        if (b.endsWith("/")) b = b.substring(0, b.length() - 1);
        if (path == null || path.isEmpty()) return URI.create(b);
        if (!path.startsWith("/")) path = "/" + path;
        return URI.create(b + path);
    }

    private String execute(HttpRequest req) {
        int attempts = cfg.retryOnServerError() ? cfg.maxRetries() + 1 : 1;
        PodRadarException lastServerException = null;
        for (int i = 0; i < attempts; i++) {
            try {
                HttpResponse<String> resp = client.send(req, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
                int status = resp.statusCode();
                if (status >= 200 && status < 300) {
                    return resp.body();
                }
                PodRadarException ex = HttpErrorMapper.map(status, resp.headers(), resp.body());
                if (ex instanceof PodRadarServerException && i < attempts - 1) {
                    lastServerException = ex;
                    sleepBackoff(i);
                    continue;
                }
                throw ex;
            } catch (IOException e) {
                if (i < attempts - 1) {
                    sleepBackoff(i);
                    continue;
                }
                throw new PodRadarNetworkException(e.getMessage() == null ? e.toString() : e.getMessage(), e);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new PodRadarNetworkException("interrupted", e);
            }
        }
        throw lastServerException == null
                ? new PodRadarNetworkException("exhausted retries", null)
                : lastServerException;
    }

    private CompletableFuture<String> executeAsync(HttpRequest req) {
        return sendWithRetry(req, 0);
    }

    private CompletableFuture<String> sendWithRetry(HttpRequest req, int attempt) {
        int maxAttempts = cfg.retryOnServerError() ? cfg.maxRetries() + 1 : 1;
        return client.sendAsync(req, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8))
                .handle((resp, err) -> new Object[]{resp, err})
                .thenCompose(pair -> {
                    @SuppressWarnings("unchecked")
                    HttpResponse<String> resp = (HttpResponse<String>) pair[0];
                    Throwable err = (Throwable) pair[1];
                    if (err != null) {
                        Throwable cause = err instanceof CompletionException ? err.getCause() : err;
                        if (attempt < maxAttempts - 1) {
                            return delayedRetry(req, attempt);
                        }
                        CompletableFuture<String> failed = new CompletableFuture<>();
                        failed.completeExceptionally(new PodRadarNetworkException(
                                cause.getMessage() == null ? cause.toString() : cause.getMessage(), cause));
                        return failed;
                    }
                    int status = resp.statusCode();
                    if (status >= 200 && status < 300) {
                        return CompletableFuture.completedFuture(resp.body());
                    }
                    PodRadarException ex = HttpErrorMapper.map(status, resp.headers(), resp.body());
                    if (ex instanceof PodRadarServerException && attempt < maxAttempts - 1) {
                        return delayedRetry(req, attempt);
                    }
                    CompletableFuture<String> failed = new CompletableFuture<>();
                    failed.completeExceptionally(ex);
                    return failed;
                });
    }

    private CompletableFuture<String> delayedRetry(HttpRequest req, int attempt) {
        long delayMs = backoffMillis(attempt);
        CompletableFuture<Void> delay = new CompletableFuture<>();
        java.util.concurrent.CompletableFuture.delayedExecutor(delayMs, java.util.concurrent.TimeUnit.MILLISECONDS)
                .execute(() -> delay.complete(null));
        return delay.thenCompose(v -> sendWithRetry(req, attempt + 1));
    }

    private static void sleepBackoff(int attempt) {
        try {
            Thread.sleep(backoffMillis(attempt));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private static long backoffMillis(int attempt) {
        long base = 200L;
        long capped = Math.min(base * (1L << Math.min(attempt, 6)), 5000L);
        long jitter = (long) (Math.random() * 100);
        return capped + jitter;
    }

    /** Exposes the underlying client for callers that need to share it. */
    public HttpClient httpClient() {
        return client;
    }

    /** Returns the effective config (read-only). */
    public SdkConfig config() {
        return cfg;
    }

    static URI _resolveForTest(URI base, String path) {
        return resolve(base, path);
    }
}
