package io.esastack;

import io.esastack.test.stability.command.ExecutorManager;

public class ClientApplication {

    public static void main(String[] args) throws Exception {
        if (args.length < 2) {
            throw new IllegalStateException("The size of args should not be less than 2!");
        }
        ExecutorManager.singleton().execute(args[0], args[1]);
    }

}
