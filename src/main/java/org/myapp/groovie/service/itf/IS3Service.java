package org.myapp.groovie.service.itf;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface IS3Service {
    void doesBucketExists(String bucketName);
    void doesObjectExists(String buketName, String objectName);
    String getPresignedUrl(String bucketName, String objectName);
    String createPresignedUrl(String bucketName, String objectName);
    List<String> getBulkPresignedUrl(String bucketName, Set<String> objectNames);
}
