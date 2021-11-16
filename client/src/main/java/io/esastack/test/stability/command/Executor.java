package io.esastack.test.stability.command;

public interface Executor {

    String type();

    void execute(String command);

    Executor singleton();

}
