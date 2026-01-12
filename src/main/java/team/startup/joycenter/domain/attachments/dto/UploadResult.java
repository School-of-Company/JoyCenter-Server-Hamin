package team.startup.joycenter.domain.attachments.dto;

public record UploadResult(
        String s3Key,
        String url
) {
}
