package com.amazonaws.samples;

import com.amazonaws.AmazonClientException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.services.transcribe.AmazonTranscribe;
import com.amazonaws.services.transcribe.AmazonTranscribeClientBuilder;
import com.amazonaws.services.transcribe.model.StartTranscriptionJobRequest;
import com.amazonaws.services.transcribe.model.Media;
import com.amazonaws.services.transcribe.model.GetTranscriptionJobRequest;
import com.amazonaws.services.transcribe.model.TranscriptionJob;

public class transcribe {
    public static void main(String[] args) {
        AWSCredentials credentials = null;
        try {
            credentials = new ProfileCredentialsProvider("default").getCredentials();
        } catch (Exception e) {
            throw new AmazonClientException(
                    "Cannot load the credentials from the credential profiles file. " +
                    "Please make sure that your credentials file is at the correct " +
                    "location (C:\\Users\\Administrator\\.aws\\credentials), and is in valid format.",
                    e);
        }
        
        AmazonTranscribe transcribe = AmazonTranscribeClientBuilder.standard()
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
                .withRegion("us-east-1")
                .build();
        StartTranscriptionJobRequest request = new StartTranscriptionJobRequest();
        
        Media media = new Media();
        media.setMediaFileUri("https://s3.amazonaws.com/exampleBucket/examplePath");
        
        request.setLanguageCode("en-US");
        request.setMedia(media);
        request.setMediaFormat("mp3");
        request.setTranscriptionJobName("test");
        request.setMediaSampleRateHertz(8000);
        
        try {
         transcribe.startTranscriptionJob(request);
        } catch (Exception e) {
         throw new AmazonClientException(
                    "Cannot start a new transcribe job now. " +
                    "Please make sure that your request configuration is correct and try later. ",
                    e);
        }
        
        GetTranscriptionJobRequest getRequest = new GetTranscriptionJobRequest();
        getRequest.setTranscriptionJobName("test");
        
        while(true) {
            TranscriptionJob job;
         
            try {
                job = transcribe.getTranscriptionJob(getRequest).getTranscriptionJob();
            } catch (Exception e) {
                throw new AmazonClientException(
                        "Cannot get a transcribe job status now. " +
                        "Please make sure that your request configuration is correct and try later. ",
                        e);
            }
         
            String status = job.getTranscriptionJobStatus();
            if (status == "COMPLETED") {
                System.out.println("Transcribe job has been done completed. ");
                break;
            } else if (status == "FAILED") {
                System.out.println("Transcribe job has been done failed. ");
                break;
            } else {
                System.out.println("Transcribe job is in progressing now, please wait ... ");
          
                try { 
                    Thread.sleep(5000); 
                } catch (InterruptedException e) { 
                    e.printStackTrace(); 
                }
            }
        }
    }
}
