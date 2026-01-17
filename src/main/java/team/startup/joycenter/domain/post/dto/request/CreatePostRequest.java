package team.startup.joycenter.domain.post.dto.request;

import software.amazon.awssdk.annotations.NotNull;
import team.startup.joycenter.domain.post.entity.constant.BlockType;

import java.util.List;

public record CreatePostRequest(
        @NotNull String title,
        @NotNull List<BlockRequest> blocks
) {
    public record BlockRequest(
            @NotNull Integer order,
            @NotNull BlockType blockType,
            String text,
            Long attachmentId
    ) {}
}

