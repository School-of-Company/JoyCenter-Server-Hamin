package team.startup.joycenter.global.s3.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.S3AsyncClient;
import software.amazon.awssdk.services.s3.model.Delete;
import software.amazon.awssdk.services.s3.model.DeleteObjectsRequest;
import software.amazon.awssdk.services.s3.model.ObjectIdentifier;
import team.startup.joycenter.domain.attachments.exception.AttachmentsDeleteFailedException;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class DeleteAllService {

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    private final S3AsyncClient s3AsyncClient;

    @Async
    public CompletableFuture<Void> execute(List<String> s3Keys) {
        if (s3Keys == null || s3Keys.isEmpty()) {
            return CompletableFuture.completedFuture(null);
        }

        List<ObjectIdentifier> objects = s3Keys.stream()
                .map(key -> ObjectIdentifier.builder().key(key).build())
                .collect(Collectors.toList());

        Delete delete = Delete.builder()
                .objects(objects)
                .quiet(true)
                .build();

        DeleteObjectsRequest request = DeleteObjectsRequest.builder()
                .bucket(bucket)
                .delete(delete)
                .build();

        return s3AsyncClient.deleteObjects(request)
                .thenAccept(response -> {
                    List<?> errors = response.errors();
                    if (errors != null && !errors.isEmpty()) {
                        log.error("S3 파일 일부 삭제 실패: {}", errors);
                        throw new AttachmentsDeleteFailedException();
                    }
                    log.debug("S3 파일 전체 삭제 성공: {}", s3Keys);
                })
                .exceptionally(ex -> {
                    log.error("S3 파일 전체 삭제 실패: {}", s3Keys, ex);
                    throw new AttachmentsDeleteFailedException();
                });
    }
}
