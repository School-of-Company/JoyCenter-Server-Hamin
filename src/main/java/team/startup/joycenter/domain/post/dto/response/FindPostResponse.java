package team.startup.joycenter.domain.post.dto.response;

import team.startup.joycenter.domain.attachments.constant.AttachmentsType;

import java.time.LocalDateTime;
import java.util.List;

public record FindPostResponse(
        String title,
        String content,
        LocalDateTime createdAt,
        Member member,
        List<Attachments> attachments
) {
    public record Member(Long memberId, String email) {}
    public record Attachments(Long attachmentId, AttachmentsType type, String attachmentUrl) {}
}
