package io.esastack.test.stability.command;

public interface ExecutorManager {

    Executor get(String type);

    void registry(Executor executor);
}
