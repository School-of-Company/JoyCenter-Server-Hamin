package team.startup.joycenter.domain.attachments.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import team.startup.joycenter.domain.attachments.entity.Attachments;
import team.startup.joycenter.domain.attachments.entity.constant.AttachmentsType;

import java.util.List;

public interface AttachmentsRepository extends JpaRepository<Attachments, Long> {
    List<Attachments> findByIdIn(List<Long> AttachmentsId);
    List<Attachments> findAllByPostIdOrderByImageOrderAsc(Long postId);
    List<Attachments> findAllByPostIdInAndAttachmentsTypeOrderByPostIdAscImageOrderAsc(List<Long> postIds, AttachmentsType type);
}
