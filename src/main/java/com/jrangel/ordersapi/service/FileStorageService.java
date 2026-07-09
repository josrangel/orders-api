package com.jrangel.ordersapi.service;

import com.jrangel.ordersapi.dto.FileUploadResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.IOException;
import java.util.UUID;

@Service
public class FileStorageService {

    private final S3Client s3Client;
    private final String bucketName;

    public FileStorageService(
            S3Client s3Client,
            @Value("${app.aws.s3.bucket}") String bucketName
    ) {
        this.s3Client = s3Client;
        this.bucketName = bucketName;
    }

    public FileUploadResponse upload(MultipartFile file) {
        String originalFileName = file.getOriginalFilename() != null
                ? file.getOriginalFilename()
                : "file";

        String fileName = UUID.randomUUID() + "-" + originalFileName;
        String contentType = file.getContentType() != null
                ? file.getContentType()
                : "application/octet-stream";

        try {
            PutObjectRequest request = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(fileName)
                    .contentType(contentType)
                    .contentLength(file.getSize())
                    .build();

            s3Client.putObject(
                    request,
                    RequestBody.fromBytes(file.getBytes())
            );

            return new FileUploadResponse(
                    fileName,
                    bucketName,
                    contentType,
                    file.getSize()
            );

        } catch (IOException exception) {
            throw new RuntimeException("Error leyendo archivo para subirlo", exception);
        } catch (S3Exception exception) {
            throw new RuntimeException("Error subiendo archivo a S3: " + exception.awsErrorDetails().errorMessage(), exception);
        }
    }

    public byte[] download(String fileName) {
        try {
            GetObjectRequest request = GetObjectRequest.builder()
                    .bucket(bucketName)
                    .key(fileName)
                    .build();

            return s3Client.getObjectAsBytes(request).asByteArray();

        } catch (NoSuchKeyException exception) {
            throw new RuntimeException("Archivo no encontrado: " + fileName, exception);
        } catch (S3Exception exception) {
            throw new RuntimeException("Error descargando archivo desde S3: " + exception.awsErrorDetails().errorMessage(), exception);
        }
    }
}