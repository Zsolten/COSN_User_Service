package COSN.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.PublishRequest;
import software.amazon.awssdk.services.sns.model.PublishResponse;

@Service
public class SnsLoggingService {

    private final SnsClient snsClient;

    @Value("${aws.sns.topic-arn}")
    private String topicArn;

    public SnsLoggingService(SnsClient snsClient) {
        this.snsClient = snsClient;
    }

    public void publishError(String level, String message) {
        String payload = String.format("""
            {
              "serviceID": "user_service",
              "level": "%s",
              "message": "%s",
            }
            """,
            level,
            message
        );

        PublishRequest request = PublishRequest.builder()
                .topicArn(topicArn)
                .message(payload)
                .build();

        snsClient.publish(request);

        PublishResponse response = snsClient.publish(request);
        System.out.println("Error succesfully published. Message published with ID: " + response.messageId());
    }
}
