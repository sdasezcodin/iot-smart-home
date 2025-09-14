package com.smarthome.command;

/**
 * Represents a generic command in the Command Pattern.
 * Each command must define how it will be executed.
 */
public interface Command {

    /**
     * Executes the action defined by the command.
     */
    void execute();
}
