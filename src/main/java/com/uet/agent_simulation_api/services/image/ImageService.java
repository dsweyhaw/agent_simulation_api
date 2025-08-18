package com.uet.agent_simulation_api.services.image;

import com.uet.agent_simulation_api.constant.AppConst;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.stream.StreamSupport;

@Slf4j
@Service
public class ImageService implements IImageService {
    public List<List<String>> listImagesInDirectory(String directoryPath) {
        final List<List<String>> imageFileList = new ArrayList<>();
        final var dirPath = Paths.get(directoryPath);

        try (DirectoryStream<Path> stream = Files.newDirectoryStream(dirPath)) {
            StreamSupport.stream(stream.spliterator(), false)
                .forEach(file -> {
                    final var fileName = file.getFileName().toString();

                    // Get last index of '.' in the file name.
                    final var extensionIndex = fileName.lastIndexOf('.');

                    // Check if the file has an extension.
                    if (extensionIndex > 0) {
                        // Get the extension of the file.
                        final var extension = fileName.substring(extensionIndex + 1);

                        // Only add image files to the list.
                        if (AppConst.IMAGE_EXTENSIONS.contains(extension)) {
                            // Remove the extension part of the file name.
                            final var nameWithoutExtension = fileName.substring(0, extensionIndex);

                            // Split the name by '-' to get parts.
                            final var parts = nameWithoutExtension.split("-");

                            String step = "";
                            String category = "";
                            // Check if there are enough parts to extract category and step.
                            if (parts.length >= 2) {
                                // Get image category.
                                category = String.join("-", Arrays.copyOfRange(parts, 0, parts.length - 1));

                                // Step is the last part.
                                step = parts[parts.length - 1];
                            }

                            // Add the file info to the list (category, step, fileName, extension).
                            imageFileList.add(List.of(fileName, extension, category, step));
                        }
                    }
                });

            log.info("Total images in directory: {}", imageFileList.size());
        } catch (Exception e) {
            log.error("Error while listing images in directory: {}", e.getMessage());

            return null;
        }

        return imageFileList;
    }

    @Override
    public MediaType getImageMediaType(String extension) {
        return switch (extension) {
            case "png" -> MediaType.IMAGE_PNG;
            case "jpg", "jpeg" -> MediaType.IMAGE_JPEG;
            case "gif" -> MediaType.IMAGE_GIF;
            default -> MediaType.APPLICATION_OCTET_STREAM;
        };
    }

    @Override
    public String getImageDataEncoded(String filePath) {
        final var path = Paths.get(filePath);
        if (!Files.exists(path)) {
            return null;
        }

        try {
            return Base64.getEncoder().encodeToString(Files.readAllBytes(path));
        } catch (Exception e) {
            log.error("Error while reading image file: {}", e.getMessage());

            return null;
        }
    }
}
