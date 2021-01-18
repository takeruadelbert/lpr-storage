package com.stn.storage.service.base;

import com.ibm.cloud.objectstorage.ClientConfiguration;
import com.ibm.cloud.objectstorage.auth.AWSCredentials;
import com.ibm.cloud.objectstorage.auth.AWSStaticCredentialsProvider;
import com.ibm.cloud.objectstorage.client.builder.AwsClientBuilder;
import com.ibm.cloud.objectstorage.oauth.BasicIBMOAuthCredentials;
import com.ibm.cloud.objectstorage.services.s3.AmazonS3;
import com.ibm.cloud.objectstorage.services.s3.AmazonS3ClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Service
public abstract class CloudStorageService {

    @Value("${storage.cloud.ibm.bucket-name}")
    protected String bucketName;

    @Value("${storage.cloud.ibm.api-key}")
    private String apiKey;

    @Value("${storage.cloud.ibm.service-instance-id}")
    private String serviceInstanceId;

    @Value("${storage.cloud.ibm.endpoint-url}")
    private String endpointUrl;

    @Value("${storage.cloud.ibm.location}")
    private String location;

    @Value("${storage.cloud.request-timeout}")
    private Integer requestTimeout;

    protected AmazonS3 cosClient;

    @PostConstruct
    public void setCosClient() {
        this.cosClient = createClient();
    }

    private AmazonS3 createClient() {
        AWSCredentials awsCredentials = new BasicIBMOAuthCredentials(apiKey, serviceInstanceId);
        ClientConfiguration clientConfiguration = new ClientConfiguration()
                .withRequestTimeout(requestTimeout)
                .withTcpKeepAlive(true);
        return AmazonS3ClientBuilder
                .standard()
                .withCredentials(new AWSStaticCredentialsProvider(awsCredentials))
                .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(endpointUrl, location))
                .withPathStyleAccessEnabled(true)
                .withClientConfiguration(clientConfiguration)
                .build();
    }
}
