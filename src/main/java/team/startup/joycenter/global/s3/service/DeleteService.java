package team.startup.joycenter.global.s3.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.S3AsyncClient;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import team.startup.joycenter.domain.attachments.exception.AttachmentsDeleteFailedException;

import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
@Slf4j
public class DeleteService {

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    private final S3AsyncClient s3AsyncClient;

    @Async
    public CompletableFuture<Void> execute(String s3Key) {
        DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                .bucket(bucket)
                .key(s3Key)
                .build();
        return s3AsyncClient.deleteObject(deleteObjectRequest)
                .thenAccept(response -> log.debug("파일 삭제 성공: {}", s3Key))
                .exceptionally(throwable -> {
                    log.error("파일 삭제 실패: {}", s3Key, throwable);
                    throw new AttachmentsDeleteFailedException();
                });
    }
}
