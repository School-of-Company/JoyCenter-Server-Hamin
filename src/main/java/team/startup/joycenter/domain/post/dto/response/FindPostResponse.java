package team.startup.joycenter.domain.post.dto.response;

import team.startup.joycenter.domain.attachments.entity.constant.AttachmentsType;
import team.startup.joycenter.domain.post.entity.constant.BlockType;

import java.time.LocalDateTime;
import java.util.List;

public record FindPostResponse(
        String title,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        Member member,
        List<Block> blocks
) {
    public record Member(Long memberId, String email) {}

    public record Block(
            Long blockId,
            Integer order,
            BlockType type,
            String text,
            Attachment attachment
    ) {}

    public record Attachment(
            Long attachmentId,
            AttachmentsType attachmentsType,
            String attachmentUrl
    ) {}
}

