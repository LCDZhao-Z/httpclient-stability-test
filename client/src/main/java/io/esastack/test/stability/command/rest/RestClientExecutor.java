package io.esastack.test.stability.command.rest;

import esa.commons.Checks;
import esa.commons.logging.Logger;
import io.esastack.commons.net.http.HttpVersion;
import io.esastack.httpclient.core.util.LoggerUtils;
import io.esastack.restclient.RestClient;
import io.esastack.restclient.RestResponseBase;
import io.esastack.test.stability.command.Executor;
import io.esastack.test.stability.thread.SuccessRateMonitoringThread;
import io.esastack.test.stability.util.BodyUtil;
import io.esastack.test.stability.util.Constants;

import java.util.concurrent.ExecutionException;
import java.util.function.Function;

public class RestClientExecutor implements Executor {

    private static final Logger logger = LoggerUtils.logger();

    public RestClientExecutor() {

    }

    @Override
    public String type() {
        return Constants.Type.REST;
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

            default:
                throw new UnsupportedOperationException("This command(" + command + ") is not supported.");
        }
    }

    private void h1Post1MB(String url) throws Exception {
        System.setProperty("io.esastack.httpclient.ioThreads", "6");
        RestClient client = RestClient.create()
                .connectTimeout(1000)
                .readTimeout(3000)
                .connectionPoolSize(2048)
                .connectionPoolWaitingQueueLength(16)
                .version(HttpVersion.HTTP_1_1)
                .build();
        post1MB(client, url, HttpVersion.HTTP_1_1);
    }

    private void h2Post1MB(String url) throws Exception {
        System.setProperty("io.esastack.httpclient.ioThreads", "6");
        RestClient client = RestClient.create()
                .connectTimeout(1000)
                .readTimeout(3000)
                .connectionPoolSize(2048)
                .connectionPoolWaitingQueueLength(16)
                .version(HttpVersion.HTTP_2)
                .build();
        post1MB(client, url, HttpVersion.HTTP_2);
    }

    private void h1GetNoBody(String url) throws Exception {
        System.setProperty("io.esastack.httpclient.ioThreads", "6");
        RestClient client = RestClient.create()
                .connectTimeout(1000)
                .readTimeout(3000)
                .connectionPoolSize(2048)
                .connectionPoolWaitingQueueLength(16)
                .version(HttpVersion.HTTP_1_1)
                .build();
        getNoBody(client, url, HttpVersion.HTTP_1_1);
    }

    private void h2GetNoBody(String url) throws Exception {
        System.setProperty("io.esastack.httpclient.ioThreads", "6");
        RestClient client = RestClient.create()
                .connectTimeout(1000)
                .readTimeout(3000)
                .connectionPoolSize(2048)
                .connectionPoolWaitingQueueLength(16)
                .version(HttpVersion.HTTP_2)
                .build();
        getNoBody(client, url, HttpVersion.HTTP_2);
    }

    private void getNoBody(RestClient client, String url, HttpVersion version)
            throws Exception {
        for (int i = 0; i < 10; i++) {
            new SuccessRateMonitoringThread<>(
                    () -> {
                        try {
                            return client.get(url)
                                    .execute()
                                    .toCompletableFuture()
                                    .get();
                        } catch (InterruptedException | ExecutionException e) {
                            logger.error("Error when getNoBody to url:{}", url, e);
                            return null;
                        }
                    },
                    createJudgeFunc(200, version, BodyUtil.EXPECTED_0MB_BYTE_LENGTH),
                    createFailedMessageFunc()
            ).start();
        }

        logger.info("RestClient 已启动10条线程，循环以0MB的请求体请求url：" + url);
        System.in.read();
    }

    private void post1MB(RestClient client, String url, HttpVersion version) throws Exception {

        for (int i = 0; i < 10; i++) {
            new SuccessRateMonitoringThread<>(
                    () -> {
                        try {
                            return client.post(url)
                                    .entity(BodyUtil.EXPECTED_1MB_BODY)
                                    .execute()
                                    .toCompletableFuture()
                                    .get();
                        } catch (Throwable e) {
                            logger.error("Error when post1MB to url:{}", url, e);
                            return null;
                        }
                    },
                    createJudgeFunc(200, version, BodyUtil.EXPECTED_1MB_BYTE_LENGTH),
                    createFailedMessageFunc()
            ).start();
        }

        logger.info("RestClient 已启动10条线程，循环以1MB的请求体请求url：" + url);
        System.in.read();
    }

    private Function<RestResponseBase, Boolean> createJudgeFunc(int expectedStatus, HttpVersion expectedVersion, int expectedBodyLength) {
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

            int bodyBytes;
            try {
                bodyBytes = response.bodyToEntity(byte[].class).length;
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

    private Function<RestResponseBase, String> createFailedMessageFunc() {
        return (response) -> {
            if (response == null) {
                return "Response is null!";
            }

            try {
                return "Response status:" + response.status() + ",Response body: " + response.bodyToEntity(String.class);
            } catch (Exception e) {
                logger.error("bodyToEntity error!", e);
                return "Generate message failed,cause: bodyToString error:" + e.getMessage();
            }
        };
    }
}
