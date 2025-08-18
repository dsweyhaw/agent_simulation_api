package com.uet.agent_simulation_api.utils;

import com.uet.agent_simulation_api.exceptions.simulation.CannotClearOldSimulationOutputException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Service
@Slf4j
@RequiredArgsConstructor
public class FileUtil {
    private final ExecutorService virtualThreadExecutor;

    /**
     * Check if a file exists
     *
     * @param path the path to the file
     * @return true if the file exists, false otherwise
     */
    public boolean fileExists(String path) {
        final var file = new File(path);

        return file.exists() && !file.isDirectory();
    }

    /**
     * Check if a directory exists
     *
     * @param path the path to the directory
     * @return true if the directory exists, false otherwise
     */
    public boolean directoryExists(String path) {
        final var file = new File(path);

        return file.exists() && file.isDirectory();
    }

    /**
     * Create a directory
     *
     * @param path the path to the directory
     * @return true if the directory is created, false otherwise
     */
    public boolean findAndWrite(String path, String key, String value) {
        try {
            final var content = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(new FileReader(path))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    if (line.contains(key)) {
                        content.append("  ").append(key).append(": ").append(value).append("\n");
                        continue;
                    }
                    content.append(line).append("\n");
                }
            }

            try (BufferedWriter writer = new BufferedWriter(new FileWriter(path))) {
                writer.write(content.toString());
            }

            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Get the value by key in a file
     *
     * @param path the path to the file
     * @param key the key to get the value
     * @return the value of the key
     */
    public String getValueByKey(String path, String key) {
        try {
            try (BufferedReader reader = new BufferedReader(new FileReader(path));) {
                String line;
                while ((line = reader.readLine()) != null) {
                    if (line.contains(key)) {
                        String[] parts = line.split(":");
                        if (parts.length == 2) {
                            return parts[1].trim();
                        }

                        return "";
                    }
                }
            }

            return "";
        } catch (Exception e) {
            log.error("Cannot get value by key: {}", key, e);

            return null;
        }
    }

    /**
     * Read the last line of a file
     *
     * @param pathToFile the path to the file
     * @param key the key must be in the line
     *
     * @return the last line of the file
     */
    public String readLastLine(String pathToFile, String key) {
        String lastStepLine = null;

        try {
            final var path = Paths.get(pathToFile);
            List<String> lines = Files.readAllLines(path);
            if (lines.isEmpty()) {
                return null;
            }

            for (int i = lines.size() - 1; i >= 0; i--) {
                String line = lines.get(i);
                if (line.contains(key)) {
                    lastStepLine = line;
                    break;
                }
            }

        } catch (Exception e) {
            log.error("Error while reading last line of file: {}", e.getMessage());
        }

        return lastStepLine;
    }

    /**
     * Get the file name by prefix
     *
     * @param folderPath the path to the folder
     * @param prefix the prefix of the file name
     *
     * @return the file name
     */
    public String getFileNameByPrefix(String folderPath, String prefix) {
        try {
            final var path = Paths.get(folderPath);

            try (var files = Files.list(path)) {
                final var file = files.filter(f -> f.getFileName().toString().startsWith(prefix)).findFirst();

                return file.map(Path::getFileName).map(Path::toString).orElse(null);
            }
        } catch (Exception e) {
            log.error("Error while getting file name by prefix: {}", e.getMessage());
        }

        return null;
    }

    /**
     * Zip a folder
     *
     * @param sourceDirPath the path to the folder
     */
    public boolean zipFolder(String sourceDirPath) {
        long startTime = System.nanoTime();
        log.info("Starting zipping folder: {}", sourceDirPath);
        final var sourcePath = Paths.get(sourceDirPath);
        final var zipFilePath = sourcePath.getParent().resolve(sourcePath.getFileName() + ".zip");

        try (final var zos = new ZipOutputStream(Files.newOutputStream(zipFilePath));
             final var paths = Files.walk(sourcePath)) {

            paths.filter(path -> !Files.isDirectory(path))
                    .forEach(path -> {
                        final var zipEntry = new ZipEntry(sourcePath.relativize(path).toString());
                        try (var inputStream = new BufferedInputStream(Files.newInputStream(path))) {
                            zos.putNextEntry(zipEntry);
                            byte[] buffer = new byte[4096];
                            int bytesRead;
                            while ((bytesRead = inputStream.read(buffer)) != -1) {
                                zos.write(buffer, 0, bytesRead);
                            }
                            zos.closeEntry();
                        } catch (Exception e) {
                            System.err.println("Error zipping file " + path + ": " + e.getMessage());
                        }
                    });
            long endTime = System.nanoTime();
            long durationInMillis = (endTime - startTime) / 1_000_000;
            log.info("Zipping process finished for folder: {}. Total time: {} ms", sourceDirPath, durationInMillis);

            return true;
        } catch (Exception e) {
            log.error("Error while zipping folder: {}", e.getMessage());

            return false;
        }
    }

    /**
     * Copy folder using OS command
     * @param sourceFolder Path of the source folder
     * @param destinationFolder Path of the destination folder
     * @return true if copy is successful, false otherwise
     */
    public boolean makeACopyFolder(String sourceFolder, String destinationFolder) {
        ProcessBuilder processBuilder;
        String command;

        if (System.getProperty("os.name").toLowerCase().contains("win")) {
            // Windows
            command = String.format("xcopy \"%s\" \"%s\" /E /I", sourceFolder, destinationFolder);
            processBuilder = new ProcessBuilder("cmd.exe", "/c", command);
        } else {
            // Unix/Linux/Mac
            command = String.format("cp -r \"%s\" \"%s\"", sourceFolder, destinationFolder);
            processBuilder = new ProcessBuilder("bash", "-c", command);
        }

        try {
            Process process = processBuilder.start();
            int exitCode = process.waitFor();

            if (exitCode == 0) {
                return true;
            } else {
                log.error("Failed to copy folder. Exit code: {}", exitCode);
                return false;
            }
        } catch (Exception e) {
            log.error("Error while copying folder: {}", e.getMessage());

            return false;
        }
    }

    /**
     * This method is used to delete a file or a folder.
     *
     */
    public void delete(String path) {
        // Clear old output directory
        final var processBuilder = new ProcessBuilder();

        try {
            final var process = processBuilder.command("bash", "-c", "rm -rf " + path).start();
        } catch (Exception e) {
            log.error("Error while clearing old output directory: {}", e.getMessage());
        }
    }

    public void rename(String oldPath, String newPath) {
        final var processBuilder = new ProcessBuilder();

        try {
            final var process = processBuilder.command("bash", "-c", "mv " + oldPath + " " + newPath).start();
        } catch (Exception e) {
            log.error("Error while renaming file: {}", e.getMessage());
        }
    }

    /**
     * Zip a folder asynchronously.
     *
     * @param sourceDirPath the path to the folder
     */
    public void zipFolderAsync(String sourceDirPath) {
        virtualThreadExecutor.submit(() -> zipFolder(sourceDirPath));
    }
}
