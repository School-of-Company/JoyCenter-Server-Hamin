package team.startup.joycenter.domain.attachments.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import team.startup.joycenter.domain.attachments.dto.response.UploadAttachmentsResponse;
import team.startup.joycenter.domain.attachments.entity.constant.AttachmentsType;
import team.startup.joycenter.domain.attachments.service.DeleteAttachmentsService;
import team.startup.joycenter.domain.attachments.service.UploadAttachmentsService;

@Tag(name = "Attachments API", description = "첨부파일 업로드/삭제 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/attachments")
public class AttachmentsController {

    private final DeleteAttachmentsService deleteAttachmentsService;
    private final UploadAttachmentsService uploadAttachmentsService;

    @Operation(
            summary = "첨부파일 추가",
            description = "multipart/form-data로 파일을 업로드합니다. attachmentsType과 imageOrder를 함께 전달해야 합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "업로드 성공",
            content = @Content(schema = @Schema(implementation = UploadAttachmentsResponse.class))),
    @ApiResponse(responseCode = "400", description = "요청 파라미터/파일이 유효하지 않음", content = @Content),
    })
    @PostMapping(consumes = org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<UploadAttachmentsResponse> uploadAttachments(
            @Parameter(
                    description = "업로드할 파일",
                    required = true,
                    content = @Content(mediaType = MediaType.APPLICATION_OCTET_STREAM_VALUE)
            )
            @RequestPart("file") MultipartFile file,

            @Parameter(
                    description = "첨부파일 타입",
                    required = true
            )
            @RequestParam("attachmentsType") AttachmentsType attachmentsType,

            @Parameter(
                    description = "이미지 순서",
                    required = true,
                    example = "1"
            )
            @RequestParam("imageOrder") Integer imageOrder) {
        return ResponseEntity.ok(uploadAttachmentsService.execute(file, attachmentsType, imageOrder));

    }

    @Operation(
            summary = "첨부파일 삭제",
            description = "attachmentsId로 첨부파일을 삭제합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "삭제 성공"),
            @ApiResponse(responseCode = "404", description = "첨부파일을 찾을 수 없음", content = @Content)
    })
    @DeleteMapping("/{attachmentsId}")
    public ResponseEntity<Void> deleteAttachments(
            @Parameter(description = "삭제할 첨부파일 ID", required = true, example = "1")
            @PathVariable("attachmentsId") Long AttachmentsId) {
        deleteAttachmentsService.execute(AttachmentsId);
        return ResponseEntity.noContent().build();
    }
}
