package com.uet.agent_simulation_api.repositories;

import com.uet.agent_simulation_api.models.Node;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface NodeRepository extends JpaRepository<Node, Integer> {
    @Query("SELECT MAX(id) FROM Node")
    Integer findMaxId();

    @Query("SELECT n FROM Node n")
    List<Node> find();

    @Query("SELECT n.id FROM Node n")
    List<Integer> getAllNodeIds();
}
