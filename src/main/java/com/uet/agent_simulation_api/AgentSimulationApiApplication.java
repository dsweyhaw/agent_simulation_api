package com.uet.agent_simulation_api;

import com.uet.agent_simulation_api.models.Node;
import com.uet.agent_simulation_api.repositories.NodeRepository;
import com.uet.agent_simulation_api.services.auth.IAuthService;
import com.uet.agent_simulation_api.utils.FileUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
@RequiredArgsConstructor
@Slf4j
public class AgentSimulationApiApplication implements CommandLineRunner {
	private final FileUtil fileUtil;
	private final NodeRepository nodeRepository;
	private final IAuthService authService;

	@Value("${cluster.config.path}")
	private String clusterConfigPath;

	@Value("${server.port}")
	private Integer serverPort;

	public static void main(String[] args) {
		SpringApplication.run(AgentSimulationApiApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		log.info("CLuster config path: {}", clusterConfigPath);
		if (!existsClusterConfig()) {
			// exit program
			log.error(" not found");
			return;
		}

		if (!checkNodeId()) {
			// exit program
			log.error("Error while checking node ID");
			return;
		}

		if (!checkNodeHost()) {
			// exit program
			log.error("Error while checking node host");
			return;
		}

		initNode();
	}

	/**
	 * Check if the application.yml file exists.
	 *
	 * @return true if the file exists, false otherwise.
	 */
	private boolean existsClusterConfig() {
		return fileUtil.fileExists(clusterConfigPath);
	}

	/**
	 * Check if the node ID is valid.
	 *
	 * @return true if the node ID is valid, false otherwise.
	 */
	private boolean checkNodeId() {
		final var nodeId = fileUtil.getValueByKey(clusterConfigPath, "node_id");
		if (nodeId == null) {
			log.error("An error occurred while getting the node ID");

			return false;
		}

		if (nodeId.isEmpty()) {
			log.info("Node ID undefined in cluster config file. Start setting node ID");

			fileUtil.findAndWrite(clusterConfigPath, "node_id", "1");
			fileUtil.findAndWrite(clusterConfigPath, "node_role", "1");
			return true;
		}

		try {
			Integer.parseInt(nodeId);
		} catch (NumberFormatException e) {
			log.error("Invalid node ID in cluster config file. Node ID must be an integer");

			return false;
		}

		return true;
	}

	/**
	 * If cannot access config file to get node host, return false.
	 * If node host is empty, set default value to localhost.
	 * If node port is empty, set default value to server port.
	 *
	 * @return true if the node host is valid, false otherwise.
	 */
	private boolean checkNodeHost() {
		final var nodeHost = fileUtil.getValueByKey(clusterConfigPath, "host");
		final var nodePort = fileUtil.getValueByKey(clusterConfigPath, "port");

		if (nodeHost == null) {
			log.error("An error occurred while getting the node host");

			return false;
		}

		if (nodePort == null) {
			log.error("An error occurred while getting the node port");

			return false;
		}

		if (nodeHost.isEmpty()) {
			log.info("Node host undefined in config file. Start setting node host");

			fileUtil.findAndWrite(clusterConfigPath, "host", "localhost");
		}

		if (nodePort.isEmpty()) {
			log.info("Node port undefined in config file. Start setting node port");

			fileUtil.findAndWrite(clusterConfigPath, "port", serverPort.toString());
		}

		return true;
	}

	/**
	 * Initialize the node.
	 */
	private void initNode() {
		final var host = fileUtil.getValueByKey(clusterConfigPath, "host");
		final var port = fileUtil.getValueByKey(clusterConfigPath, "port");
		final var nodeName = fileUtil.getValueByKey(clusterConfigPath, "node_name");
		final var node = nodeRepository.findById(1);

		if (node.isEmpty()) {
			nodeRepository.save(Node.builder()
				.name(nodeName)
				.role(1)
				.host(host)
				.port(Integer.parseInt(port))
				.createdBy("system")
				.updatedBy("system")
				.build());

			return;
		}

		node.get().setName(nodeName);
		node.get().setRole(1);
		node.get().setHost(host);
		node.get().setPort(Integer.parseInt(port));
		node.get().setCreatedBy("system");
		node.get().setUpdatedBy("system");

		nodeRepository.save(node.get());
	}
}
