package team.startup.joycenter.domain.attachments.service;

import org.springframework.web.multipart.MultipartFile;
import team.startup.joycenter.domain.attachments.dto.response.UploadAttachmentsResponse;
import team.startup.joycenter.domain.attachments.entity.constant.AttachmentsType;

public interface UploadAttachmentsService {
    UploadAttachmentsResponse execute(MultipartFile file, AttachmentsType attachmentsType, Integer imageOrder);
}
