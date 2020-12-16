package com.makerthon.kangwonServer.service;

import com.amazonaws.services.rekognition.AmazonRekognition;
import com.amazonaws.services.rekognition.AmazonRekognitionClientBuilder;
import com.amazonaws.services.rekognition.model.*;
import com.amazonaws.util.IOUtils;
import com.makerthon.kangwonServer.domain.User;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.List;

@Service
public class RekognitionService {

    private AmazonRekognition rekognitionClient;

    @PostConstruct
    public void setS3Client() {
        AmazonRekognition rekognitionClient = AmazonRekognitionClientBuilder.defaultClient();
    }

    public boolean matchingFace(User user, MultipartFile picture){
        rekognitionClient = AmazonRekognitionClientBuilder.defaultClient();
        ByteBuffer targetImageBytes=null;
        try (InputStream inputStream = picture.getInputStream()) {
            targetImageBytes = ByteBuffer.wrap(IOUtils.toByteArray(inputStream));
        }
        catch(Exception e)
        {
           e.printStackTrace();
        }
        S3Object s3Object = new S3Object().withBucket("bomnae-static").withName(user.getProfilePictureName()    );
        Image sourceS3 = new Image().withS3Object(s3Object);
        Image target=new Image()
                .withBytes(targetImageBytes);

        CompareFacesRequest request = new CompareFacesRequest()
                .withSourceImage(sourceS3)
                .withTargetImage(target)
                .withSimilarityThreshold(90F);

        // Call operation
        CompareFacesResult compareFacesResult=rekognitionClient.compareFaces(request);
        // Display results
        List<CompareFacesMatch> faceDetails = compareFacesResult.getFaceMatches();
        try{
            if(faceDetails.size()==1){
                for (CompareFacesMatch match: faceDetails) {
                    ComparedFace face = match.getFace();
                    if(face.getConfidence()>90F)
                        return true;
                    else{
                        return false;
                    }
                }
            }
            else{
                throw new Exception();
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }
}
