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
import team.startup.joycenter.global.s3.dto.DeleteAllResult;

import java.util.*;
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
    public CompletableFuture<DeleteAllResult> execute(List<String> s3Keys) {
        if (s3Keys == null || s3Keys.isEmpty()) {
            return CompletableFuture.completedFuture(new DeleteAllResult(List.of(), List.of()));
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
                .thenApply(response -> {
                    var errors = response.errors();
                    Set<String> failedSet = new HashSet<>();

                    if (errors != null && !errors.isEmpty()) {
                        for (var err : errors) {
                            failedSet.add(err.key());
                        }
                    }

                    List<String> failed = new ArrayList<>(failedSet);
                    List<String> success = s3Keys.stream()
                            .filter(k -> !failedSet.contains(k))
                            .toList();

                    return new DeleteAllResult(success, failed);
                });

    }
}
