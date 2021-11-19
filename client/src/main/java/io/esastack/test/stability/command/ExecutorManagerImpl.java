package io.esastack.test.stability.command;

import java.util.HashMap;
import java.util.Map;

public class ExecutorManagerImpl implements ExecutorManager {
    private static final Map<String, Executor> executorMap =
            new HashMap<>();

    public ExecutorManagerImpl() throws Exception {
    }

    @Override
    public Executor get(String type) {
        Executor executor = executorMap.get(type);
        if (executor == null) {
            throw new NullPointerException("The type(" + type + ") of executor is null!");
        }
        return executor;
    }

    @Override
    public void registry(Executor executor) {
        Executor existExecutor = executorMap.get(executor.type());
        if (existExecutor != null) {
            throw new IllegalStateException("The type(" + executor.type() + ") of executor existed before registry!");
        }
        executorMap.put(executor.type(), executor);
    }
}
