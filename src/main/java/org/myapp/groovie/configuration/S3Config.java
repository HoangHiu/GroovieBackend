package org.myapp.groovie.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3ClientBuilder;
import software.amazon.awssdk.services.s3.S3Configuration;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

import java.net.URI;

@Configuration
public class S3Config {
    @Value("${spring.data.aws.s3.access-key}")
    private String S3AccessKey;

    @Value("${spring.data.aws.s3.private-key}")
    private String S3PrivateKey;

    @Value("${spring.data.aws.s3.region}")
    private String S3Region;

    @Value("${spring.data.aws.s3.endpoint}")
    private String S3Endpoint;

    @Bean
    public S3Client s3Client(){
        return S3Client.builder()
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create(S3AccessKey,S3PrivateKey))
                )
                .endpointOverride(URI.create(S3Endpoint))
                .serviceConfiguration(S3Configuration.builder()
                        .pathStyleAccessEnabled(true)
                        .build())
                .region(Region.of(S3Region))
                .build();
    }

    @Bean
    public S3Presigner s3Presigner(){
        return S3Presigner.builder()
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create(S3AccessKey,S3PrivateKey))
                )
                .endpointOverride(URI.create(S3Endpoint))
                .serviceConfiguration(S3Configuration.builder()
                        .pathStyleAccessEnabled(true)
                        .build())
                .region(Region.of(S3Region))
                .build();
    }
}
