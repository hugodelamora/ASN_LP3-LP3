/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mx.iteso.desi.cloud.hw3;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import java.nio.ByteBuffer;


import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.rekognition.AmazonRekognition;
import com.amazonaws.services.rekognition.AmazonRekognitionClientBuilder;
import com.amazonaws.services.rekognition.model.BoundingBox;
import com.amazonaws.services.rekognition.model.CompareFacesMatch;
import com.amazonaws.services.rekognition.model.CompareFacesRequest;
import com.amazonaws.services.rekognition.model.CompareFacesResult;
import com.amazonaws.services.rekognition.model.ComparedFace;
import com.amazonaws.services.rekognition.model.Image;
import com.amazonaws.services.rekognition.model.S3Object;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.ListObjectsV2Request;
import com.amazonaws.services.s3.model.ListObjectsV2Result;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import java.util.List;

/**
 *
 * @author 
 */
public class AWSFaceCompare {
    Face f=new Face();;
    
    String srcBucket;
    AmazonRekognition arek;
    String accessKey;
    String secretKey;
    Regions region;

    public AWSFaceCompare(String accessKey, String secretKey, Regions region,String srcBucket) {
        this.srcBucket = srcBucket;
        this.accessKey = accessKey;
        this.secretKey = secretKey;
        this.region = region;
        
        AWSCredentialsProvider credProvider = new AWSStaticCredentialsProvider(new BasicAWSCredentials(accessKey, secretKey));
        arek = AmazonRekognitionClientBuilder.standard().withCredentials(credProvider).withRegion(region).build();
    }

    public Face compare(ByteBuffer imageBuffer) {
        // TODO
        //getting image from screen
        imageBuffer.rewind();
        Image source = new Image().withBytes(imageBuffer);
        
        //getting image from s3
        //http://docs.aws.amazon.com/rekognition/latest/dg/get-started-exercise-compare-faces.html
        //Image source = getImageUtil(S3_BUCKET, "p1.jpg");
        //Image target = getImageUtil(S3_BUCKET, "hugo.jpg");
        
        getAllItemsBucket(srcBucket,source);
        
        return f;
    }
   private static CompareFacesResult callCompareFaces(Image sourceImage, Image targetImage,
            Float similarityThreshold, AmazonRekognition amazonRekognition) {

      CompareFacesRequest compareFacesRequest = new CompareFacesRequest()
         .withSourceImage(sourceImage)
         .withTargetImage(targetImage)
         .withSimilarityThreshold(similarityThreshold);
      return amazonRekognition.compareFaces(compareFacesRequest);
   }

    private static Image getImageUtil(String bucket, String key) {
      return new Image()
          .withS3Object(new S3Object()
                  .withBucket(bucket)
                  .withName(key));
    }
    
    private void getAllItemsBucket(String bucketName,Image source){
        
        try {
            AWSCredentialsProvider credentials= new AWSStaticCredentialsProvider(new BasicAWSCredentials(accessKey, secretKey));
            AmazonS3 s3 = AmazonS3ClientBuilder.standard()
                        .withRegion(region)
        		.withCredentials(credentials)
                        .build();
        
            System.out.println("Listing objects");
            ListObjectsV2Request req = new ListObjectsV2Request().withBucketName(bucketName).withMaxKeys(2);
            ListObjectsV2Result result;
            do {               
               result = s3.listObjectsV2(req);
               
               for (S3ObjectSummary objectSummary : 
                   result.getObjectSummaries()) {
                   //System.out.println(" - " + objectSummary.getKey() + "  " +
                   //        "(size = " + objectSummary.getSize() + 
                   //        ")");
                   System.out.print("Target: [" + objectSummary.getKey() + "]  ");
                   
                   Image target = getImageUtil(bucketName, objectSummary.getKey());
                   boolean x=compare2(source,target); 
                   System.out.println("Match [[" + x + "]]  ");
                   if(x==true)
                        f.setName(objectSummary.getKey());   
               }
               //System.out.println("Next Continuation Token : " + result.getNextContinuationToken());
               req.setContinuationToken(result.getNextContinuationToken());
            } while(result.isTruncated() == true ); 
            
         } catch (AmazonServiceException ase) {
            System.out.println("Caught an AmazonServiceException, " +
            		"which means your request made it " +
                    "to Amazon S3, but was rejected with an error response " +
                    "for some reason.");
            System.out.println("Error Message:    " + ase.getMessage());
            System.out.println("HTTP Status Code: " + ase.getStatusCode());
            System.out.println("AWS Error Code:   " + ase.getErrorCode());
            System.out.println("Error Type:       " + ase.getErrorType());
            System.out.println("Request ID:       " + ase.getRequestId());
        } catch (AmazonClientException ace) {
            System.out.println("Caught an AmazonClientException, " +
            		"which means the client encountered " +
                    "an internal error while trying to communicate" +
                    " with S3, " +
                    "such as not being able to access the network.");
            System.out.println("Error Message: " + ace.getMessage());
        }
    }
    private boolean compare2(Image source,Image target){
     
        AWSCredentialsProvider credentials = new AWSStaticCredentialsProvider(new BasicAWSCredentials(accessKey, secretKey));

        AmazonRekognition rekognitionClient = AmazonRekognitionClientBuilder
        		.standard()
        		.withRegion(region)
        		.withCredentials(credentials)
        		.build();
        Float similarityThreshold = 70F;
        CompareFacesResult compareFacesResult = callCompareFaces(source,
        		target,
        		similarityThreshold,
        		rekognitionClient);
        List <CompareFacesMatch> faceDetails = compareFacesResult.getFaceMatches();
      
        System.out.print("Encuentra similitud: ["+!faceDetails.isEmpty()+"] ");
        
        for (CompareFacesMatch match: faceDetails){
        	ComparedFace face= match.getFace();
        	BoundingBox position = face.getBoundingBox();
        	System.out.println("Face at " + position.getLeft().toString()
        			+ " " + position.getTop()
        			+ " matches with " + face.getConfidence().toString()
        			+ "% confidence.");
                
                f.setCofidence(face.getConfidence());              
                return true;
        }        
        return false;
    }
}
