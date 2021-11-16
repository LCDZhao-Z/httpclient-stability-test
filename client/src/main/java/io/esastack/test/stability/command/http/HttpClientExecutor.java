package io.esastack.test.stability.command.http;

import esa.commons.Checks;
import esa.commons.logging.Logger;
import io.esastack.commons.net.buffer.Buffer;
import io.esastack.commons.net.http.HttpVersion;
import io.esastack.httpclient.core.Handler;
import io.esastack.httpclient.core.HttpClient;
import io.esastack.httpclient.core.HttpResponse;
import io.esastack.httpclient.core.SegmentRequest;
import io.esastack.httpclient.core.util.LoggerUtils;
import io.esastack.test.stability.command.AutoRegistryExecutor;
import io.esastack.test.stability.command.Executor;
import io.esastack.test.stability.thread.SuccessRateMonitoringThread;
import io.esastack.test.stability.util.BodyUtil;
import io.esastack.test.stability.util.Constants;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutionException;
import java.util.function.Function;

public class HttpClientExecutor implements AutoRegistryExecutor {

    private static final Logger logger = LoggerUtils.logger();

    public HttpClientExecutor() {
    }

    @Override
    public String type() {
        return Constants.Type.HTTP;
    }

    @Override
    public void execute(String command, String url) throws Exception {
        Checks.checkNotNull(command, "command");
        Checks.checkNotNull(url, "url");
        switch (command) {
            case Constants.Command.H1_POST_1MB:
                h1Post1MB(url);
                break;

            case Constants.Command.H2_POST_1MB:
                h2Post1MB(url);
                break;

            case Constants.Command.H1_GET_NO_BODY:
                h1GetNoBody(url);
                break;

            case Constants.Command.H2_GET_NO_BODY:
                h2GetNoBody(url);
                break;

            case Constants.Command.H1_CHUNK:
                h1Chunk(url);
                break;

            case Constants.Command.H2_CHUNK:
                h2Chunk(url);
                break;

            default:
                throw new UnsupportedOperationException("This command(" + command + ") is not supported.");
        }
    }

    private void h1Post1MB(String url) throws InterruptedException, ExecutionException, IOException {
        System.setProperty("io.esastack.httpclient.ioThreads", "6");
        HttpClient client = HttpClient.create()
                .connectTimeout(1000)
                .readTimeout(3000)
                .connectionPoolSize(2048)
                .connectionPoolWaitingQueueLength(16)
                .version(HttpVersion.HTTP_1_1)
                .build();
        post1MB(client, url, HttpVersion.HTTP_1_1);
    }

    private void h2Post1MB(String url) throws InterruptedException, ExecutionException, IOException {
        System.setProperty("io.esastack.httpclient.ioThreads", "6");
        HttpClient client = HttpClient.create()
                .connectTimeout(1000)
                .readTimeout(3000)
                .connectionPoolSize(2048)
                .connectionPoolWaitingQueueLength(16)
                .version(HttpVersion.HTTP_2)
                .build();
        post1MB(client, url, HttpVersion.HTTP_2);
    }

    private void h1GetNoBody(String url) throws IOException {
        System.setProperty("io.esastack.httpclient.ioThreads", "6");
        HttpClient client = HttpClient.create()
                .connectTimeout(1000)
                .readTimeout(3000)
                .connectionPoolSize(2048)
                .connectionPoolWaitingQueueLength(16)
                .version(HttpVersion.HTTP_1_1)
                .build();
        getNoBody(client, url, HttpVersion.HTTP_1_1);
    }

    private void h2GetNoBody(String url) throws IOException {
        System.setProperty("io.esastack.httpclient.ioThreads", "6");
        HttpClient client = HttpClient.create()
                .connectTimeout(1000)
                .readTimeout(3000)
                .connectionPoolSize(2048)
                .connectionPoolWaitingQueueLength(16)
                .version(HttpVersion.HTTP_2)
                .build();
        getNoBody(client, url, HttpVersion.HTTP_2);
    }

    private void h1Chunk(String url) throws IOException {
        System.setProperty("io.esastack.httpclient.ioThreads", "6");
        HttpClient client = HttpClient.create()
                .connectTimeout(1000)
                .readTimeout(3000)
                .connectionPoolSize(2048)
                .connectionPoolWaitingQueueLength(16)
                .version(HttpVersion.HTTP_1_1)
                .build();
        chunk(client, url, HttpVersion.HTTP_1_1);
    }

    private void h2Chunk(String url) throws IOException {
        System.setProperty("io.esastack.httpclient.ioThreads", "6");
        HttpClient client = HttpClient.create()
                .connectTimeout(1000)
                .readTimeout(3000)
                .connectionPoolSize(2048)
                .connectionPoolWaitingQueueLength(16)
                .version(HttpVersion.HTTP_2)
                .build();
        chunk(client, url, HttpVersion.HTTP_2);
    }

    private void chunk(HttpClient client, String url, HttpVersion version) throws IOException {
        new SuccessRateMonitoringThread<>(
                () -> {
                    try {
                        SegmentRequest request = client.post(url)
                                .handler(new SimpleHandler())
                                .segment();
                        for (int j = 0; j < BodyUtil.K; j++) {
                            request.write(BodyUtil.CHUNK_BODY[j]);
                        }
                        return request.end().get();
                    } catch (InterruptedException | ExecutionException e) {
                        logger.error("Chunk request to url({}) error", url, e);
                        return null;
                    }
                },
                createJudgeFunc(200, version, BodyUtil.DON_T_JUDGE_BODY),
                createFailedMessageFunc()
        ).start();

        logger.info("HttpClient 已启动1条线程，循环以1024次1KB的chunk请求体请求url：" + url);
        System.in.read();
    }

    private void getNoBody(HttpClient client, String url, HttpVersion version)
            throws IOException {
        for (int i = 0; i < 10; i++) {
            new SuccessRateMonitoringThread<>(
                    () -> {
                        try {
                            return client.get(url)
                                    .execute()
                                    .get();
                        } catch (InterruptedException | ExecutionException e) {
                            logger.error("GetNoBody to url({}) error", url, e);
                            return null;
                        }
                    },
                    createJudgeFunc(200, version, BodyUtil.EXPECTED_0MB_BYTE_LENGTH),
                    createFailedMessageFunc()
            ).start();
        }

        logger.info("HttpClient 已启动10条线程，循环以0MB的请求体请求url：" + url);
        System.in.read();
    }

    private void post1MB(HttpClient client, String url, HttpVersion version)
            throws IOException {
        for (int i = 0; i < 10; i++) {
            new SuccessRateMonitoringThread<>(
                    () -> {
                        try {
                            return client.post(url)
                                    .body(BodyUtil.EXPECTED_1MB_BODY)
                                    .execute()
                                    .get();
                        } catch (InterruptedException | ExecutionException e) {
                            logger.error("post1MB to url({}) error", url, e);
                            return null;
                        }
                    },
                    createJudgeFunc(200, version, BodyUtil.EXPECTED_1MB_BYTE_LENGTH),
                    createFailedMessageFunc()
            ).start();
        }

        logger.info("HttpClient 已启动10条线程，循环以1MB的请求体请求url：" + url);
        System.in.read();
    }

    private Function<HttpResponse, Boolean> createJudgeFunc(int expectedStatus, HttpVersion expectedVersion, int expectedBodyLength) {
        return (response) -> {
            if (response == null) {
                logger.error("response is null!");
                return false;
            }

            int responseStatus = response.status();
            if (expectedStatus != responseStatus) {
                logger.error("Unexpected responseStatus: " + responseStatus);
                return false;
            }

            HttpVersion responseVersion = response.version();
            if (!expectedVersion.equals(responseVersion)) {
                logger.error("Unexpected responseVersion: " + responseVersion);
                return false;
            }

            if (expectedBodyLength == BodyUtil.DON_T_JUDGE_BODY) {
                return true;
            }

            int bodyBytes;
            try {
                bodyBytes = response.body().readableBytes();
            } catch (Exception e) {
                logger.error("bodyToEntity error!", e);
                return false;
            }

            if (bodyBytes != expectedBodyLength) {
                logger.error("Unexpected body size: " + bodyBytes);
                return false;
            }
            return true;
        };
    }

    private Function<HttpResponse, String> createFailedMessageFunc() {
        return (response) -> {
            if (response == null) {
                return "Response is null!";
            }

            try {
                return "Response status:" + response.status() + ",Response body: " + response.body().string(StandardCharsets.UTF_8);
            } catch (Exception e) {
                logger.error("Generate failedMessage error!", e);
                return "Generate failedMessage error,cause: bodyToString error:" + e.getMessage();
            }
        };
    }

    private static final class SimpleHandler extends Handler {

        private int count;

        @Override
        public void onData(Buffer content) {
            if (content == null) {
                return;
            }
            count += content.readableBytes();
        }

        @Override
        public void onEnd() {
            if (count != BodyUtil.EXPECTED_1MB_BYTE_LENGTH) {
                logger.error("Unexpected body size: " + count);
            }
        }

        @Override
        public void onError(Throwable cause) {
            cause.printStackTrace();
        }

    }

    @Override
    public Executor singleton() {
        return new HttpClientExecutor();
    }
}
