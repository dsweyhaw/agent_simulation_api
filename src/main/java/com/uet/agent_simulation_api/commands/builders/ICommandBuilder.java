package com.uet.agent_simulation_api.commands.builders;

import java.util.List;
import java.util.Map;

public interface ICommandBuilder {
    /**
     * This method is used to build command.
     *
     * @param pathToShell - String
     * @param options - Map<String, String>
     * @param args - List<String>
     * @return String
     */
    String build(String pathToShell, Map<String, String> options, List<String> args);
}
