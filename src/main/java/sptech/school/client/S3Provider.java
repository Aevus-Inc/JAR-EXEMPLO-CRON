package sptech.school.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.auth.credentials.AwsSessionCredentials;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

public class S3Provider {
    private static final Logger logger = LoggerFactory.getLogger(S3Provider.class);

    public S3Client getS3Client() {
        logger.info("Criando o cliente S3.");
        return S3Client.builder()
                .region(Region.US_EAST_1) // Substitua pela região do seu bucket
                .credentialsProvider(DefaultCredentialsProvider.create())
                .build();
    }

    /*

    //Se quiser testar local
    private final AwsSessionCredentials credentials;

    public S3Client getS3Client() {
        logger.info("Criando o cliente S3.");
        return S3Client.builder()
                .region(Region.US_EAST_1) // Substitua pela região do seu bucket
                .credentialsProvider(StaticCredentialsProvider.create(credentials))
                .build();
    }
    */
}
