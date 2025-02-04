package org.myapp.groovie.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3ClientBuilder;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

@Configuration
public class S3Config {
    private String S3AccessKey;
    private String S3PrivateKey;
    private String S3Region;

    @Bean
    public S3Client s3Client(){
        return S3Client.builder()
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create(S3AccessKey,S3PrivateKey))
                )
                .region(Region.of(S3Region))
                .build();
    }

    @Bean
    public S3Presigner s3Presigner(){
        return S3Presigner.builder()
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create(S3AccessKey,S3PrivateKey))
                )
                .region(Region.of(S3Region))
                .build();
    }
}
