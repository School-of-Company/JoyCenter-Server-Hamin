package team.startup.joycenter.domain.attachments.dto.response;

import team.startup.joycenter.domain.attachments.entity.constant.AttachmentsType;

public record UploadAttachmentsResponse(
        Long attachmentsId,
        String url,
        AttachmentsType attachmentsType
) {
}
