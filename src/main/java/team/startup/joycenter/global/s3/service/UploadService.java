package team.startup.joycenter.global.s3.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.async.AsyncRequestBody;
import software.amazon.awssdk.services.s3.S3AsyncClient;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;
import team.startup.joycenter.domain.attachments.dto.UploadResult;
import team.startup.joycenter.domain.attachments.exception.AttachmentsUploadFailedException;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
public class UploadService {

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    @Value("${cloud.aws.region.static}")
    private String region;

    private final S3AsyncClient s3AsyncClient;

    @Async
    public CompletableFuture<UploadResult> execute(String fileName, InputStream inputStream) {
        String uploadFileName = UUID.randomUUID() + "/" + fileName;

        try {
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucket)
                    .key(uploadFileName)
                    .build();

            AsyncRequestBody requestBody = AsyncRequestBody.fromBytes(inputStream.readAllBytes());
            CompletableFuture<PutObjectResponse> responseFuture =
                    s3AsyncClient.putObject(putObjectRequest, requestBody);

            return responseFuture.thenApply(response -> {
                String url = String.format(
                        "https://%s.s3.%s.amazonaws.com/%s",
                        bucket,
                        region,
                        uploadFileName
                );

                return new UploadResult(uploadFileName, url);
            });

        } catch (IOException e) {
            throw new AttachmentsUploadFailedException();
        }
    }

}
