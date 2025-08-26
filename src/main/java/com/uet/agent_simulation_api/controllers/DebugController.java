package com.uet.agent_simulation_api.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

/**
 * Debug controller for troubleshooting file upload issues
 */
@RestController
@RequestMapping("/api/v1/debug")
@RequiredArgsConstructor
@Slf4j
public class DebugController {

    @Value("${gama.path.project}")
    private String GAMA_PROJECT_ROOT_PATH;

    @Value("${gama.path.shell}")
    private String GAMA_SHELL_PATH;

    @GetMapping("/file-info")
    public Map<String, Object> getFileInfo(@RequestParam String projectLocation, @RequestParam String fileName) {
        Map<String, Object> info = new HashMap<>();
        
        try {
            var projectPath = Paths.get(GAMA_PROJECT_ROOT_PATH, projectLocation, "models");
            var filePath = projectPath.resolve(fileName);
            
            info.put("projectPath", projectPath.toString());
            info.put("filePath", filePath.toString());
            info.put("fileExists", Files.exists(filePath));
            info.put("directoryExists", Files.exists(projectPath));
            info.put("isReadable", Files.isReadable(filePath));
            info.put("isWritable", Files.isWritable(projectPath));
            
            if (Files.exists(filePath)) {
                info.put("fileSize", Files.size(filePath));
                info.put("lastModified", Files.getLastModifiedTime(filePath).toString());
            }
            
            // Check GAMA executable
            Path gamaShell = Paths.get(GAMA_SHELL_PATH);
            info.put("gamaShellPath", GAMA_SHELL_PATH);
            info.put("gamaShellExists", Files.exists(gamaShell));
            info.put("gamaShellExecutable", Files.isExecutable(gamaShell));
            
        } catch (IOException e) {
            info.put("error", e.getMessage());
        }
        
        return info;
    }

    @GetMapping("/list-files")
    public Map<String, Object> listFiles(@RequestParam String projectLocation) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            var projectPath = Paths.get(GAMA_PROJECT_ROOT_PATH, projectLocation, "models");
            result.put("projectPath", projectPath.toString());
            result.put("directoryExists", Files.exists(projectPath));
            
            if (Files.exists(projectPath)) {
                var files = Files.list(projectPath)
                    .map(Path::getFileName)
                    .map(Path::toString)
                    .toList();
                result.put("files", files);
            }
            
        } catch (IOException e) {
            result.put("error", e.getMessage());
        }
        
        return result;
    }
}