package team.startup.joycenter.domain.post.dto.request;

import java.util.List;

public record CreatePostRequest(
        String title,
        String content,
        List<Long> attachmentsIds
) {
}
