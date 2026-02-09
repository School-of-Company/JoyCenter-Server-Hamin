package team.startup.joycenter.domain.attachments.repository;

import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import team.startup.joycenter.domain.attachments.entity.Attachments;
import team.startup.joycenter.domain.attachments.entity.constant.AttachmentsType;

import java.time.LocalDateTime;
import java.util.List;

public interface AttachmentsRepository extends JpaRepository<Attachments, Long> {
    List<Attachments> findByPostId(Long postId);
    List<Attachments> findAllByPostIdInAndAttachmentsTypeOrderByPostIdAscImageOrderAsc(List<Long> postIds, AttachmentsType type);
    @Query("select a.s3Key from Attachments a where a.post.id = :postId")
    List<String> findS3KeysByPostId(@Param("postId") Long postId);

    List<Attachments> findByPostIsNullAndCreatedAtBefore(LocalDateTime time);
    void deleteByS3KeyIn(List<String> s3Keys);
}
