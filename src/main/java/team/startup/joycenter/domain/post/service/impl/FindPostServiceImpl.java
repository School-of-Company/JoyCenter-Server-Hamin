package team.startup.joycenter.domain.post.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team.startup.joycenter.domain.post.dto.response.FindPostResponse;
import team.startup.joycenter.domain.post.entity.Post;
import team.startup.joycenter.domain.post.exception.NotFoundPostException;
import team.startup.joycenter.domain.post.repostory.PostRepository;
import team.startup.joycenter.domain.post.service.FindPostService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FindPostServiceImpl implements FindPostService {

    private final PostRepository postRepository;

    @Override
    @Transactional(readOnly = true)
    public FindPostResponse execute(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(NotFoundPostException::new);

        return new FindPostResponse(
                post.getTitle(),
                post.getContent(),
                post.getCreatedAt(),
                new FindPostResponse.Member(
                        post.getAuthor().getId(),
                        post.getAuthorName()
                ),
                List.of()
        );
    }
}
