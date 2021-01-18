package com.stn.storage.service;

import com.ibm.cloud.objectstorage.services.s3.model.*;
import com.stn.storage.service.base.CloudStorageService;
import com.stn.storage.service.misc.ICloudStorage;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;

@Service
public class IBMCloudStorageService extends CloudStorageService implements ICloudStorage {

    @Override
    public List<S3ObjectSummary> getListObjects() {
        ObjectListing objectListing = cosClient.listObjects(new ListObjectsRequest().withBucketName(bucketName));
        return objectListing.getObjectSummaries();
    }

    @Override
    public PutObjectResult uploadObject(byte[] bytes, String objectName) {
        try {
            InputStream stream = new ByteArrayInputStream(bytes);
            ObjectMetadata objectMetadata = new ObjectMetadata();
            objectMetadata.setContentType("application/x-java-serialized-object");
            objectMetadata.setContentLength(bytes.length);
            return cosClient.putObject(bucketName, objectName, stream, objectMetadata);
        } catch (Exception exception) {
            exception.printStackTrace();
            return null;
        }
    }

    @Override
    public S3ObjectInputStream getObject(String objectName) {
        GetObjectRequest request = new GetObjectRequest(bucketName, objectName);
        S3Object response = cosClient.getObject(request);
        return response.getObjectContent();
    }

    @Override
    public void deleteObject(String objectName) {
        cosClient.deleteObject(bucketName, objectName);
    }
}
