package com.uet.agent_simulation_api.services.node;

import com.uet.agent_simulation_api.models.Node;
import com.uet.agent_simulation_api.responses.node.NodeBriefResponse;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

public interface INodeService {
    /**
     * This method is used to get all nodes.
     *
     * @return List<NodeBriefResponse>
     */
    List<NodeBriefResponse> get();

    Node getCurrentNode();

    /**
     * Get current node id
     *
     * @return nodeId Integer
     */
    Integer getCurrentNodeId();

    /**
     * This method is used to get a node by id.
     *
     * @param nodeId Node id
     * @return Node
     */
    Node getNodeById(Integer nodeId);

    /**
     * This method is used to get a web client by node id.
     *
     * @param nodeId Node id
     * @return WebClient
     */
    WebClient getWebClientByNodeId(Integer nodeId);

    /**
     * This method is used to get all node ids.
     *
     * @return List<Integer>
     */
    List<Integer> getAllNodeIds();
}
