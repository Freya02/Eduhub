package com.example.testBackend.Service;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.SdkClientException;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.example.testBackend.Config.S3StorageConfig;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.UUID;
@Component
public class AmazonS3Client {

    @Autowired
    private AmazonS3 s3Client;

    @Value("${amazon.s3.endpointUrl}")
    private String endpointUrl;

    @Value("${amazon.s3.bucketName}")
    private String bucketName;

    @Value("${cloud.aws.credentials.access-key}")
    private String accessKey;

    @Value("${cloud.aws.credentials.secret-key}")
    private String secretKey;

    @Value("${amazon.s3.courseImageFolder}")
    private String courseImageFolder;

    @PostConstruct
    public void init () {
        System.out.println ("Endpoint URL: " + endpointUrl);
        System.out.println ("Bucket Name: " + bucketName);
        System.out.println ("Access Key: " + accessKey);
        System.out.println ("Secret Key: " + secretKey);
    }
    
    public String uploadContent (MultipartFile contentFile, String courseId, String chapterId, String subchapterId) {
        return uploadFile (contentFile, courseId, chapterId, subchapterId);
    }

    public String uploadCourseImage (MultipartFile imageFile, String courseId) {
        File fileObj = convertMultiPartFileToFile (imageFile);
        String fileName = generateFileName (imageFile.getOriginalFilename ());
        String folderKey = generateCourseImageFolder (courseId);
        String s3Key = folderKey + "/" + fileName;

        try {
            if (!s3Client.doesObjectExist (bucketName, folderKey + "/")) {
                s3Client.putObject (bucketName, folderKey + "/", "");
            }
            
            s3Client.putObject (new PutObjectRequest (bucketName, s3Key, fileObj));
            fileObj.delete ();
            String absoluteUrl = "https://" + bucketName + ".s3.amazonaws.com/" + s3Key;
            return "File uploaded: " + absoluteUrl;

        } catch (SdkClientException e) {
            throw new IllegalStateException ("Error occurred while uploading file to S3", e);
        }
    }

    private String generateCourseImageFolder (String courseId) {
        return courseImageFolder + "/course_" + courseId;
    }

    public String uploadFile (MultipartFile file, String courseId, String chapterId, String subchapterId) {
        File fileObj = convertMultiPartFileToFile (file);
        String originalFileName = file.getOriginalFilename();
        String sanitizedFileName = originalFileName.replaceAll(" ", "_"); // Replace spaces with underscores
        String fileName = generateFileName (sanitizedFileName);
        String folderKey = generateFolderKey (courseId, chapterId, subchapterId);
        String s3Key = folderKey + "/" + fileName;

        try {
            if (!s3Client.doesObjectExist (bucketName, folderKey + "/")) {
                s3Client.putObject (bucketName, folderKey + "/", "");
            }
                s3Client.putObject (new PutObjectRequest (bucketName, s3Key, fileObj));
            
            fileObj.delete ();

            String absoluteUrl = "https://" + bucketName + ".s3.amazonaws.com/" + s3Key;
            return "File uploadedsssss: " + absoluteUrl;

        } catch (SdkClientException e) {
            throw new IllegalStateException ("Error occurred while uploading file to S3", e);
        }
    }

    private String generateFileName (String originalFileName) {
        return System.currentTimeMillis () + "_" + originalFileName;
    }

    private String generateFolderKey (String courseId, String chapterId, String subchapterId) {
        return "course_" + courseId + "/chapter_" + chapterId + "/subchapter_" + subchapterId;
    }

    private File convertMultiPartFileToFile (MultipartFile file) {

        File convertedFile = new File (file.getOriginalFilename ());
        try (FileOutputStream fos = new FileOutputStream (convertedFile)) {
            fos.write (file.getBytes ());
        } catch (IOException e) {
        }
        return convertedFile;
    }

    private void uploadFileToS3Bucket (String fileName, File file) {
        BasicAWSCredentials credentials = new BasicAWSCredentials (accessKey, secretKey);
        AmazonS3 s3Client = AmazonS3ClientBuilder.standard ()
                .withCredentials (new AWSStaticCredentialsProvider (credentials))
                .withEndpointConfiguration (new AwsClientBuilder.EndpointConfiguration (endpointUrl, "s3.ca-central-1"))
                .build ();
        try {
            PutObjectRequest putObjectRequest = new PutObjectRequest (bucketName, fileName, file);
            s3Client.putObject (putObjectRequest);
        } catch (AmazonServiceException e) {
            throw new IllegalStateException ("Error uploading file to S3", e);
        } catch (SdkClientException e) {
            throw new IllegalStateException ("Error occurred while communicating with S3", e);
        }
    }

    public String deleteFile (String filepath) {
        if(filepath != null) {
            System.out.println ("Deleting file: " + filepath);
            try {
                String uriString = filepath.substring (filepath.indexOf ("https"));
                URI uri = new URI (uriString);
                String bucketName = uri.getHost ().replace (".s3.amazonaws.com", "");
                String objectKey = uri.getPath ().substring (1); // Remove the leading slash
                if (!s3Client.doesBucketExistV2 (bucketName)) {
                    throw new IllegalArgumentException ("Bucket does not exist: " + bucketName);
                }
                s3Client.deleteObject (bucketName, objectKey);
                System.out.println ("Object deleted successfully from bucket: " + bucketName);
            } catch (Exception e) {
                throw new IllegalStateException ("Error occurred while deleting file from S3", e);
            }
        }
        return filepath + " removed ...";
    }

    public void deleteBucket(String s3Url) {
        try {
            URI uri = new URI(s3Url);
            String bucketName = uri.getHost().split("\\.")[0];
            String objectKey = uri.getPath().substring(1); 

            s3Client.deleteObject(bucketName, objectKey);
            System.out.println("Object deleted successfully from bucket: " + bucketName);
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException("Invalid S3 URL: " + s3Url, e);
        } catch (AmazonS3Exception e) {
            throw new IllegalStateException("Error occurred while deleting object from S3: " + e.getMessage(), e);
        }
    }
}

