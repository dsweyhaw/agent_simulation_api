package com.uet.agent_simulation_api.utils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ThreadUtil {
    public void showAll() {
        final var setOfThread = Thread.getAllStackTraces().keySet();

        setOfThread.forEach(thread -> log.info("Thread ID: {}, Thread Name: {}", thread.threadId(), thread.getName()));
    }

    public void killThread(long threadId) {
        final var setOfThread = Thread.getAllStackTraces().keySet();

        setOfThread.stream()
                .filter(thread -> thread.threadId() == threadId)
                .findFirst()
                .ifPresent(Thread::interrupt);
    }

    /**
     * Kill process and its children use OS command.
     */
    public void killProcessById(long pid) {
        try {
            String osName = System.getProperty("os.name").toLowerCase();
            ProcessBuilder processBuilder;

            if (osName.contains("win")) {
                // Windows - kill process tree
                processBuilder = new ProcessBuilder("taskkill", "/F", "/T", "/PID", String.valueOf(pid));
            } else {
                // Linux/Unix - kill process group
                // Kill all processes in the process group.
                processBuilder = new ProcessBuilder("bash", "-c",
                        String.format("pkill -P %d; kill -9 %d", pid, pid));
            }

            final var killProcess = processBuilder.start();
            int exitCode = killProcess.waitFor();

            if (exitCode == 0) {
                log.info("Successfully killed process tree {}", pid);
            } else {
                log.error("Failed to kill process tree {}. Exit code: {}", pid, exitCode);
            }
        } catch (Exception e) {
            log.error("Error while killing process tree {}", pid, e);
            if (e instanceof InterruptedException) {
                Thread.currentThread().interrupt();
            }
        }
    }
}
