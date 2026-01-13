package team.startup.joycenter.domain.attachments.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import team.startup.joycenter.domain.attachments.dto.UploadResult;
import team.startup.joycenter.domain.attachments.dto.response.UploadAttachmentsResponse;
import team.startup.joycenter.domain.attachments.entity.Attachments;
import team.startup.joycenter.domain.attachments.entity.constant.AttachmentsType;
import team.startup.joycenter.domain.attachments.exception.AttachmentsUploadFailedException;
import team.startup.joycenter.domain.attachments.repository.AttachmentsRepository;
import team.startup.joycenter.domain.attachments.service.UploadAttachmentsService;
import team.startup.joycenter.global.s3.service.UploadService;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

@Service
@RequiredArgsConstructor
public class UploadAttachmentsServiceImpl implements UploadAttachmentsService {

    private final UploadService uploadService;
    private final AttachmentsRepository attachmentsRepository;

    @Override
    @Transactional
    public UploadAttachmentsResponse execute(MultipartFile file, AttachmentsType attachmentsType, Integer imageOrder) {
        UploadResult uploadResult = uploadFile(file);

        Attachments attachments = Attachments.builder()
                .attachmentsUrl(uploadResult.url())
                .attachmentsType(attachmentsType)
                .s3Key(uploadResult.s3Key())
                .imageOrder(imageOrder)
                .build();

        attachmentsRepository.save(attachments);

        return new UploadAttachmentsResponse(
                attachments.getId(),
                attachments.getAttachmentsUrl(),
                attachments.getAttachmentsType()
        );
    }

    private UploadResult uploadFile(MultipartFile file) {
        try {
            return uploadService.execute(file.getOriginalFilename(), file.getInputStream()).get();
        } catch (IOException | InterruptedException | ExecutionException e) {
            throw new AttachmentsUploadFailedException();
        }
    }
}
