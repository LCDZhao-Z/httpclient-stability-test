package io.esastack;

import io.esastack.test.stability.command.ExecutorManager;
import io.esastack.test.stability.command.ExecutorManagerImpl;
import io.esastack.test.stability.command.http.HttpClientExecutor;
import io.esastack.test.stability.command.rest.RestClientExecutor;

public class ClientApplication {

    public static void main(String[] args) throws Exception {
        if (args.length < 3) {
            throw new IllegalStateException("The size of args should not be less than 3!");
        }
        ExecutorManager executorManager = new ExecutorManagerImpl();
        executorManager.registry(new HttpClientExecutor());
        executorManager.registry(new RestClientExecutor());
        executorManager.get(args[0]).execute(args[1], args[2]);
    }

}
