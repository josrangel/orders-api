package com.jrangel.ordersapi.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.BucketAlreadyOwnedByYouException;
import software.amazon.awssdk.services.s3.model.CreateBucketRequest;
import software.amazon.awssdk.services.s3.model.HeadBucketRequest;
import software.amazon.awssdk.services.s3.model.NoSuchBucketException;
import software.amazon.awssdk.services.s3.model.S3Exception;

@Component
public class S3BucketInitializer implements CommandLineRunner {

    private final S3Client s3Client;
    private final String bucketName;

    public S3BucketInitializer(
            S3Client s3Client,
            @Value("${app.aws.s3.bucket}") String bucketName
    ) {
        this.s3Client = s3Client;
        this.bucketName = bucketName;
    }

    @Override
    public void run(String... args) {
        try {
            s3Client.headBucket(
                    HeadBucketRequest.builder()
                            .bucket(bucketName)
                            .build()
            );

            System.out.println("Bucket already exists: " + bucketName);

        } catch (NoSuchBucketException exception) {
            createBucket();

        } catch (S3Exception exception) {
            if (exception.statusCode() == 404) {
                createBucket();
                return;
            }

            throw exception;
        }
    }

    private void createBucket() {
        try {
            s3Client.createBucket(
                    CreateBucketRequest.builder()
                            .bucket(bucketName)
                            .build()
            );

            System.out.println("Bucket created: " + bucketName);

        } catch (BucketAlreadyOwnedByYouException exception) {
            System.out.println("Bucket already owned by you: " + bucketName);
        }
    }
}