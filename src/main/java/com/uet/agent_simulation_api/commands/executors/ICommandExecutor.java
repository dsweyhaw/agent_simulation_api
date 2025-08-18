package com.uet.agent_simulation_api.commands.executors;

public interface ICommandExecutor {
    /**
     * This method is used to run command in parallel.
     *
     * @param command - command to execute
     */
    void execute(String command);

    /**
     * This method is used to get the output of the command.
     *
     * @param process - process to get output
     */
    void getCommandOutput(Process process);
}
