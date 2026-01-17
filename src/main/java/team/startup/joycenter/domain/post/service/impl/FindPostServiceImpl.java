package team.startup.joycenter.domain.post.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team.startup.joycenter.domain.post.dto.response.FindPostResponse;
import team.startup.joycenter.domain.post.entity.Post;
import team.startup.joycenter.domain.post.entity.constant.BlockType;
import team.startup.joycenter.domain.post.exception.NotFoundPostException;
import team.startup.joycenter.domain.post.repostory.PostBlockRepository;
import team.startup.joycenter.domain.post.repostory.PostRepository;
import team.startup.joycenter.domain.post.service.FindPostService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FindPostServiceImpl implements FindPostService {

    private final PostRepository postRepository;
    private final PostBlockRepository postBlockRepository;

    @Override
    @Transactional(readOnly = true)
    public FindPostResponse execute(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(NotFoundPostException::new);

        List<FindPostResponse.Block> blocks = postBlockRepository
                .findAllByPostIdOrderByOrderAsc(post.getId())
                .stream()
                .map(b -> new FindPostResponse.Block(
                        b.getId(),
                        b.getOrder(),
                        b.getBlockType(),
                        b.getBlockType() == BlockType.TEXT ? b.getText() : null,
                        b.getBlockType() == BlockType.ATTACHMENT && b.getAttachments() != null
                                ? new FindPostResponse.Attachment(
                                b.getAttachments().getId(),
                                b.getAttachments().getAttachmentsType(),
                                b.getAttachments().getAttachmentsUrl()
                        )
                                : null
                ))
                .toList();

        return new FindPostResponse(
                post.getTitle(),
                post.getCreatedAt(),
                post.getUpdatedAt(),
                new FindPostResponse.Member(
                        post.getAuthor().getId(),
                        post.getAuthorName()
                ),
                blocks
        );
    }
}
