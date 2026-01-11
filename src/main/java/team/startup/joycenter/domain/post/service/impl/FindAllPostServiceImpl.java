package team.startup.joycenter.domain.post.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team.startup.joycenter.domain.post.dto.response.FindAllPostResponse;
import team.startup.joycenter.domain.post.entity.Post;
import team.startup.joycenter.domain.post.entity.constant.Sort;
import team.startup.joycenter.domain.post.repostory.PostRepository;
import team.startup.joycenter.domain.post.service.FindAllPostService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FindAllPostServiceImpl implements FindAllPostService {

    private final PostRepository postRepository;

    @Override
    @Transactional(readOnly = true)
    public FindAllPostResponse  execute(Sort sort, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, sort.toSpringSort());
        Page<Post> posts = postRepository.findAll(pageable);

        List<FindAllPostResponse.Item> content = posts.getContent().stream()
                .map(post -> new FindAllPostResponse.Item(
                        post.getId(),
                        post.getTitle(),
                        new FindAllPostResponse.Member(
                                post.getAuthor().getId(),
                                post.getAuthorName()
                        ),
                        new FindAllPostResponse.Thumbnail(
                                null,
                                null
                        )
                ))
                .toList();

        FindAllPostResponse.PageInfo pageInfo = new FindAllPostResponse.PageInfo(
                posts.getNumber(),
                posts.getSize(),
                posts.getTotalElements(),
                posts.getTotalPages(),
                posts.hasNext(),
                posts.hasPrevious()
        );

        return new FindAllPostResponse(content, pageInfo);
    }
}
