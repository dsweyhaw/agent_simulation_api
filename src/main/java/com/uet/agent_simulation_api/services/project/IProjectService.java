package com.uet.agent_simulation_api.services.project;

import com.uet.agent_simulation_api.models.Project;

import java.math.BigInteger;
import java.util.List;
import java.util.Optional;

public interface IProjectService {
    /**
     * This method is used to get all projects.
     *
     * @return List<Project>
     */
    List<Project> get();

    /**
     * This method is used to get a project by id.
     *
     * @param id Project id
     * @return Optional<Project>
     */
    Optional<Project> getProject(BigInteger id);
}
