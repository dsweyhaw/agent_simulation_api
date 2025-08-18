package com.uet.agent_simulation_api.services.s3;

import software.amazon.awssdk.services.s3.model.ObjectCannedACL;

import java.util.List;

/**
 * S3 service interface.
 */
public interface IS3Service {
    /**
     * This method is used to upload a file to S3
     *
     * @param objectKey String
     * @param localPath String
     */
    void uploadFile(String objectKey, String localPath);

    /**
     * This method is used to upload a file to S3 with a specific ACL
     *
     * @param objectKey String
     * @param localPath String
     * @param acl ObjectCannedACL
     */
    void uploadFile(String objectKey, String localPath, ObjectCannedACL acl);

    /**
     * This method is used to delete a directory in S3
     *
     * @param s3Directory String
     */
    void clearDirectory(String s3Directory);

    /**
     * This method is used to upload a directory to S3
     *
     * @param localPath String
     * @param s3Directory String
     */
    void uploadDirectory(String localPath, String s3Directory);

    /**
     * This method is used to make a folder public in S3
     *
     * @param s3Directory String
     */
    void makeFolderPublic(String s3Directory);

    /**
     * This method is used to clear a directory in S3
     *
     * @param s3Directory String
     */
    void clear(String s3Directory);

    /**
     * This method is used to list all files in a directory in S3
     *
     * @param s3Directory String
     * @return List<String>
     */
    List<String> listFileNamesInDirectory(String s3Directory);

    /**
     * This method is used to get the S3 prefix URL
     *
     * @return String
     */
    String getS3PrefixUrl();
}
