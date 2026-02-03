package team.startup.joycenter.global.s3.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.async.AsyncRequestBody;
import software.amazon.awssdk.services.s3.S3AsyncClient;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import team.startup.joycenter.domain.attachments.dto.UploadResult;
import team.startup.joycenter.domain.attachments.exception.AttachmentsUploadFailedException;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Service
@Slf4j
@RequiredArgsConstructor
public class UploadService {

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    @Value("${cloud.aws.region.static}")
    private String region;

    private final S3AsyncClient s3AsyncClient;

    @Async
    public CompletableFuture<UploadResult> execute(String fileName, byte[] fileBytes) {
        String uploadFileName = UUID.randomUUID() + "/" + fileName;

        try {
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucket)
                    .key(uploadFileName)
                    .contentLength((long) fileBytes.length)
                    .build();

            AsyncRequestBody requestBody = AsyncRequestBody.fromBytes(fileBytes);

            return s3AsyncClient.putObject(putObjectRequest, requestBody)
                    .handle((resp, ex) -> {
                        if (ex != null) {
                            log.error("S3 업로드 실패 bucket={}, key={}", bucket, uploadFileName, ex);
                            throw new AttachmentsUploadFailedException();
                        }

                        String url = String.format(
                                "https://%s.s3.%s.amazonaws.com/%s",
                                bucket, region, uploadFileName
                        );

                        return new UploadResult(uploadFileName, url);
                    });

        } catch (Exception e) {
            log.error("S3 업로드 준비 실패", e);
            throw new AttachmentsUploadFailedException();
        }
    }
}
