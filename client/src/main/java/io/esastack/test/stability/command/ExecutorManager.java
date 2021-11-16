package io.esastack.test.stability.command;

public interface ExecutorManager {

    void execute(String type, String command);

    void registry(Executor executor);

    static ExecutorManager singleton() throws Exception {
        return new ExecutorManagerImpl();
    }

}
