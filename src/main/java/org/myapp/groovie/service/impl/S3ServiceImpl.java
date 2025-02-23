package org.myapp.groovie.service.impl;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.myapp.groovie.service.itf.IS3Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectAclRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class S3ServiceImpl implements IS3Service {
    private final S3Presigner s3Presigner;
    private final S3Client s3Client;

    @Value("${spring.data.aws.s3.expiry}")
    private int expiry;

    @Override
    public void doesBucketExists(String bucketName) {

    }

    @Override
    public void doesObjectExists(String buketName, String objectName) {

    }

    @Override
    public List<String> getBulkPresignedUrl(String bucketName, Set<String> objectNames){
        List<String> urlList = new ArrayList<>();

        for(String objectName : objectNames){
            urlList.add(this.getPresignedUrl(bucketName, objectName));
        }

        return urlList;
    }

    @Override
    public String getPresignedUrl(String bucketName, String objectName) {
        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(objectName)
                .build();

        GetObjectPresignRequest getObjectPresignRequest = GetObjectPresignRequest.builder()
                .getObjectRequest(getObjectRequest)
                .signatureDuration(Duration.ofMinutes(expiry))
                .build();

        PresignedGetObjectRequest presignedGetObjectRequest = s3Presigner.presignGetObject(getObjectPresignRequest);
        return presignedGetObjectRequest.url().toString();
    }

    @Override
    public String createPresignedUrl(String bucketName, String objectName) {
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(objectName)
                .build();

        PutObjectPresignRequest putObjectPresignRequest = PutObjectPresignRequest.builder()
                .putObjectRequest(putObjectRequest)
                .signatureDuration(Duration.ofMinutes(expiry))
                .build();

        PresignedPutObjectRequest presignedPutObjectRequest = s3Presigner.presignPutObject(putObjectPresignRequest);
        return presignedPutObjectRequest.url().toString();
    }
}
