package com.uet.agent_simulation_api.services.image;

import org.springframework.http.MediaType;

import java.util.List;

public interface IImageService {
    /**
     * List all images in a directory.
     *
     * @param directoryPath The path to the directory.
     * @return A list of image file names.
     */
    List<List<String>> listImagesInDirectory(String directoryPath);

    /**
     * Get the media type of image.
     *
     * @param extension The extension of the image.
     * @return The media type of the image.
     */
    MediaType getImageMediaType(String extension);

    /**
     * Get the encoded data of an image file.
     *
     * @param filePath The path to the image file.
     * @return The encoded data of the image file.
     */
    String getImageDataEncoded(String filePath);
}
