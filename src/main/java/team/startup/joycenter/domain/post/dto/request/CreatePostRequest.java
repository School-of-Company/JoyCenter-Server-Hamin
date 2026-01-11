package team.startup.joycenter.domain.post.dto.request;

public record CreatePostRequest(
        String title,
        String content
) {
}
