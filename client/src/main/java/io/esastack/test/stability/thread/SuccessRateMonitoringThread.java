package io.esastack.test.stability.thread;

import esa.commons.logging.Logger;
import io.esastack.httpclient.core.util.LoggerUtils;

import java.util.function.Function;
import java.util.function.Supplier;

public class SuccessRateMonitoringThread<T> extends Thread {

    private static final Logger logger = LoggerUtils.logger();
    private final Supplier<T> task;
    private final Function<T, Boolean> judgeFunc;
    private final Function<T, String> failedMessageFunc;
    private static final long printCountPoint = 1000;
    private long countForPrint = 0;
    private final long printPeriodMills;
    private long lastPrintTime = System.currentTimeMillis();
    private long failedCount;
    private long count;

    public SuccessRateMonitoringThread(
            Supplier<T> task,
            Function<T, Boolean> judgeFunc,
            Function<T, String> failedMessageFunc) {
        this.judgeFunc = judgeFunc;
        this.task = task;
        this.failedMessageFunc = failedMessageFunc;
        this.printPeriodMills = 10 * 60 * 1000;
        this.setName("SuccessRateMonitoringThread-" + System.currentTimeMillis());
    }

    public SuccessRateMonitoringThread(
            Supplier<T> task,
            Function<T, Boolean> judgeFunc,
            Function<T, String> failedMessageFunc,
            long printPeriodMills) {
        this.judgeFunc = judgeFunc;
        this.task = task;
        this.failedMessageFunc = failedMessageFunc;
        this.printPeriodMills = printPeriodMills;
    }

    @Override
    public void run() {
        while (true) {
            try {
                T result;
                try {
                    result = task.get();
                } catch (Throwable e) {
                    logger.error("Task execute error!", e);
                    failedCount++;
                    continue;
                }

                boolean isSuccess = false;
                try {
                    isSuccess = judgeFunc.apply(result);
                } catch (Throwable e) {
                    logger.error("judge result error!result:{}", result, e);
                }

                if (isSuccess) {
                    continue;
                }

                failedCount++;
                try {
                    String failedMessage = failedMessageFunc.apply(result);
                    logger.error(failedMessage);
                } catch (Throwable e) {
                    logger.error("FailedMessageFunc apply error!result:{}", result, e);
                }

            } catch (Throwable e) {
                logger.error("Error occur!", e);
            } finally {
                countForPrint++;
                count++;
                if (countForPrint == printCountPoint) {
                    countForPrint = 0;
                    long current = System.currentTimeMillis();
                    if (current - lastPrintTime > printPeriodMills) {
                        lastPrintTime = current;
                        logger.info("Count : " + count +
                                ", FailedCount : " + failedCount);
                    }
                }
            }
        }
    }
}
