package org.myapp.groovie.service.itf;

public interface IS3Service {
    void doesBucketExists(String bucketName);
    void doesObjectExists(String buketName, String objectName);
    String getPresignedUrl(String bucketName, String objectName);
    String createPresignedUrl(String bucketName, String objectName);
}
