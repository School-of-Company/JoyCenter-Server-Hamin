package team.startup.joycenter.domain.attachments.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team.startup.joycenter.domain.attachments.exception.NotFoundAttachmentException;
import team.startup.joycenter.domain.attachments.repository.AttachmentsRepository;
import team.startup.joycenter.domain.attachments.service.DeleteAttachmentsService;
import team.startup.joycenter.global.s3.service.DeleteService;
import team.startup.joycenter.domain.attachments.entity.Attachments;

@Service
@RequiredArgsConstructor
public class DeleteAttachmentsServiceImpl implements DeleteAttachmentsService {

    private final AttachmentsRepository  attachmentsRepository;
    private final DeleteService deleteService;

    @Override
    @Transactional
    public void execute(Long attachmentId) {
        Attachments attachment = attachmentsRepository.findById(attachmentId)
                .orElseThrow(NotFoundAttachmentException::new);

        deleteService.execute(attachment.getS3Key());
        attachmentsRepository.delete(attachment);
    }
}