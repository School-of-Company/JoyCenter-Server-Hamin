package team.startup.joycenter.global.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import team.startup.joycenter.domain.attachments.entity.Attachments;
import team.startup.joycenter.domain.attachments.repository.AttachmentsRepository;
import team.startup.joycenter.global.s3.dto.DeleteAllResult;
import team.startup.joycenter.global.s3.service.DeleteAllService;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class UnusedAttachmentsCleanupScheduler {

    private final AttachmentsRepository attachmentsRepository;
    private final DeleteAllService deleteAllService;

    private static final int MIN_AGE_DAYS = 3;

    @Scheduled(cron = "0 0 16 * * *", zone = "Asia/Seoul")
    public void unusedAttachmentsCleanup() {
        try {
            List<Attachments> unusedAttachments = getUnusedAttachments();
            if (unusedAttachments.isEmpty()) {
                log.info("There are no unused attachments to delete");
                return;
            }

            List<String> s3Keys = unusedAttachments.stream()
                    .map(Attachments::getS3Key)
                    .toList();

            DeleteAllResult result = deleteAllService.execute(s3Keys).join();

            if (result.successCount() == 0) {
                log.warn("S3에서 삭제 성공한 파일이 없어 DB 삭제를 건너뜁니다. (failed={})", result.failedKeys().size());
                return;
            }

            deleteFromDatabaseByKeys(result.successKeys());

            log.info("정상적으로 삭제 작업 완료 (S3 성공={}, 실패={}, DB삭제={})",
                    result.successKeys().size(),
                    result.failedKeys().size(),
                    result.successKeys().size());

        } catch (Exception e) {
            log.error("고아 첨부파일 정리 중 오류 발생", e);
        }
    }

    @Transactional(readOnly = true)
    protected List<Attachments> getUnusedAttachments() {
        LocalDateTime cutoff = LocalDateTime.now(ZoneId.of("Asia/Seoul")).minusDays(MIN_AGE_DAYS);
        return attachmentsRepository.findByPostIsNullAndCreatedAtBefore(cutoff);
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    protected void deleteFromDatabaseByKeys(List<String> successKeys) {
        if (successKeys == null || successKeys.isEmpty()) {
            log.info("There are no attachments to delete from DB");
            return;
        }
        attachmentsRepository.deleteByS3KeyIn(successKeys);
    }
}

