package team.startup.joycenter.domain.attachments.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import team.startup.joycenter.domain.attachments.dto.UploadResult;
import team.startup.joycenter.domain.attachments.dto.response.UploadAttachmentsResponse;
import team.startup.joycenter.domain.attachments.entity.Attachments;
import team.startup.joycenter.domain.attachments.entity.constant.AttachmentsType;
import team.startup.joycenter.domain.attachments.exception.AttachmentsFileSizeExceededException;
import team.startup.joycenter.domain.attachments.exception.AttachmentsUploadFailedException;
import team.startup.joycenter.domain.attachments.repository.AttachmentsRepository;
import team.startup.joycenter.domain.attachments.service.UploadAttachmentsService;
import team.startup.joycenter.global.s3.service.UploadService;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

@Service
@RequiredArgsConstructor
@Slf4j
public class UploadAttachmentsServiceImpl implements UploadAttachmentsService {

    private final UploadService uploadService;
    private final AttachmentsRepository attachmentsRepository;

    @Value("${spring.servlet.multipart.max-file-size}")
    private long maxFileSize;

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
        if (file.getSize() > maxFileSize) {
            throw new AttachmentsFileSizeExceededException();
        }

        try {
            byte[] fileBytes = file.getBytes();
            return uploadService.execute(file.getOriginalFilename(), fileBytes).get();
        } catch (IOException | InterruptedException | ExecutionException e) {
            log.error("파일 업로드 실패", e);
            throw new AttachmentsUploadFailedException();
        }
    }
}
