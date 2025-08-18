package com.uet.agent_simulation_api.configs;

//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
//import software.amazon.awssdk.regions.Region;
//import software.amazon.awssdk.services.s3.S3AsyncClient;
//import software.amazon.awssdk.services.s3.S3Client;
//import software.amazon.awssdk.transfer.s3.S3TransferManager;

///**
// * This class is used to configure S3.
// */
//@Configuration
//public class S3Config {
//    @Value("${aws.access-key}")
//    private String accessKey;
//
//    @Value("${aws.secret-key}")
//    private String secretKey;
//
//    @Value("${aws.s3.bucket-name}")
//    private String bucketName;
//
//    @Value("${aws.region}")
//    private String region;
//
//    /**
//     * This method is used to create an S3Client bean.
//     *
//     * @return S3Client
//     */
//    @Bean
//    public S3Client s3client() {
//        final var awsCredentials = AwsBasicCredentials.create(accessKey, secretKey);
//
//        return S3Client.builder()
//                .region(Region.of(region))
//                .credentialsProvider(() -> awsCredentials)
//                .build();
//    }
//
//    /**
//     * This method is used to create an S3TransferManager bean.
//     *
//     * @return S3TransferManager
//     */
//    @Bean
//    public S3TransferManager s3TransferManager() {
//        final var s3AsyncClient = S3AsyncClient.builder()
//                .region(Region.of(region))
//                .credentialsProvider(() -> AwsBasicCredentials.create(accessKey, secretKey))
//                .build();
//
//        return S3TransferManager.builder()
//                .s3Client(s3AsyncClient)
//                .build();
//    }
//}
