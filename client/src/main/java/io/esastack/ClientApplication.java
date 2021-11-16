package io.esastack;

import io.esastack.test.stability.command.ExecutorManager;

public class ClientApplication {

    public static void main(String[] args) throws Exception {
        if (args.length < 3) {
            throw new IllegalStateException("The size of args should not be less than 3!");
        }
        ExecutorManager.singleton().get(args[0]).execute(args[1], args[2]);
    }

}
