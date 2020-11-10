package com.makerthon.kangwonServer;

import com.amazonaws.services.rekognition.AmazonRekognition;
import com.amazonaws.services.rekognition.AmazonRekognitionClientBuilder;
import com.amazonaws.services.rekognition.model.*;
import com.amazonaws.util.IOUtils;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.List;

@SpringBootTest
class KangwonServerApplicationTests {

    @Test
    void contextLoads() {
    }

    @Test
    public void aws이미지인식() throws IOException {
        Float similarityThreshold = 70F;
        //Replace sourceFile and targetFile with the image files you want to compare.
        String sourceImage = "/Users/LeeChnagSup/Desktop/source.jpeg";
        String targetImage = "/Users/LeeChnagSup/Desktop/target.jpeg";
        ByteBuffer sourceImageBytes=null;
        ByteBuffer targetImageBytes=null;
        MultipartFile file = new MockMultipartFile("images", "image123", "image/jpeg",  getClass().getResourceAsStream("/cat.jpg"));

        AmazonRekognition rekognitionClient = AmazonRekognitionClientBuilder.defaultClient();
        //Load source and target images and create input parameters
        try (InputStream inputStream = new FileInputStream(new File(sourceImage))) {
            sourceImageBytes = ByteBuffer.wrap(IOUtils.toByteArray(inputStream));
        }
        catch(Exception e)
        {
            System.out.println("Failed to load source image " + sourceImage);
        }
        try (InputStream inputStream = file.getInputStream()) {
            targetImageBytes = ByteBuffer.wrap(IOUtils.toByteArray(inputStream));
        }
        catch(Exception e)
        {
            System.out.println("Failed to load target images: " + targetImage);
        }

        Image source=new Image()
                .withBytes(sourceImageBytes);

        S3Object s3Object = new S3Object().withBucket("bomnae-static").withName("source.jpeg");
        Image sourceS3 = new Image().withS3Object(s3Object);
        Image target=new Image()
                .withBytes(targetImageBytes);

        CompareFacesRequest request = new CompareFacesRequest()
                .withSourceImage(sourceS3)
                .withTargetImage(target)
                .withSimilarityThreshold(similarityThreshold);

        // Call operation
        CompareFacesResult compareFacesResult=rekognitionClient.compareFaces(request);


        // Display results
        List<CompareFacesMatch> faceDetails = compareFacesResult.getFaceMatches();
        for (CompareFacesMatch match: faceDetails){
            ComparedFace face= match.getFace();
            BoundingBox position = face.getBoundingBox();
            System.out.println("Face at " + position.getLeft().toString()
                    + " " + position.getTop()
                    + " matches with " + face.getConfidence().toString()
                    + "% confidence.");

        }

    }

}
