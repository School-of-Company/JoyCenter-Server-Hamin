package team.startup.joycenter.domain.attachments.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import team.startup.joycenter.domain.attachments.dto.response.UploadAttachmentsResponse;
import team.startup.joycenter.domain.attachments.entity.constant.AttachmentsType;
import team.startup.joycenter.domain.attachments.service.DeleteAttachmentsService;
import team.startup.joycenter.domain.attachments.service.UploadAttachmentsService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/attachments")
public class AttachmentsController {

    private final DeleteAttachmentsService deleteAttachmentsService;
    private final UploadAttachmentsService uploadAttachmentsService;

    @PostMapping(consumes = org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<UploadAttachmentsResponse> uploadAttachments(
            @RequestPart("file") MultipartFile file,
            @RequestParam("attachmentsType") AttachmentsType attachmentsType,
            @RequestParam("imageOrder") Integer imageOrder    ) {
        return ResponseEntity.ok(uploadAttachmentsService.execute(file, attachmentsType, imageOrder));

    }

    @DeleteMapping("/{attachmentsId}")
    public ResponseEntity<Void> deleteAttachments(@PathVariable("attachmentsId") Long AttachmentsId) {
        deleteAttachmentsService.execute(AttachmentsId);
        return ResponseEntity.noContent().build();
    }
}
