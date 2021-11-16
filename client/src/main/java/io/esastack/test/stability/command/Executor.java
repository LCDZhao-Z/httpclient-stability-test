package io.esastack.test.stability.command;

import java.io.IOException;

public interface Executor {

    String type();

    void execute(String command, String url) throws IOException, Exception;

    Executor singleton();

}
