package team.startup.joycenter.domain.post.dto.response;

import lombok.Builder;

import java.util.List;

@Builder
public record FindAllPostResponse(
        List<Item> content,
        PageInfo page
) {
    public record Item(
            Long id,
            String title,
            Member member,
            Thumbnail thumbnail
    ) {}

    public record Member(Long memberId, String email) {}
    public record Thumbnail(Long attachmentsId, String url) {}

    public record PageInfo(
            int number,
            int size,
            long totalElements,
            int totalPages,
            boolean hasNext,
            boolean hasPrevious
    ) {}
}
