package com.stn.storage.service.misc;

import com.ibm.cloud.objectstorage.services.s3.model.PutObjectResult;
import com.ibm.cloud.objectstorage.services.s3.model.S3ObjectInputStream;
import com.ibm.cloud.objectstorage.services.s3.model.S3ObjectSummary;

import java.util.List;

public interface ICloudStorage {
    List<S3ObjectSummary> getListObjects();

    PutObjectResult uploadObject(byte[] bytes, String objectName);

    S3ObjectInputStream getObject(String objectName);

    void deleteObject(String objectName);
}
