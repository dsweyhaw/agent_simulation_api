package com.uet.agent_simulation_api.repositories;

import com.uet.agent_simulation_api.models.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigInteger;
import java.util.List;

public interface ProjectRepository extends JpaRepository<Project, BigInteger> {
    @Query(
        value = """
            SELECT p FROM Project p
            WHERE p.userId = :user_id
        """
    )
    List<Project> find(@Param("user_id") BigInteger userId);
    
    /**
     * Check if a project with the given name already exists for the specific user
     * @param name The project name to check
     * @param userId The user ID
     * @return true if a project with this name exists for this user, false otherwise
     */
    boolean existsByNameAndUserId(String name, BigInteger userId);
    
    /**
     * Delete a project by ID and user ID (for security)
     * @param projectId The project ID to delete
     * @param userId The user ID (for security check)
     * @return number of deleted records
     */
    @Modifying
    @Query(
        value = """
            DELETE FROM Project p
            WHERE p.id = :project_id AND p.userId = :user_id
        """
    )
    int deleteByIdAndUserId(@Param("project_id") BigInteger projectId, @Param("user_id") BigInteger userId);
}
