package team.startup.joycenter.domain.post.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team.startup.joycenter.domain.attachments.entity.Attachments;
import team.startup.joycenter.domain.attachments.entity.constant.AttachmentsType;
import team.startup.joycenter.domain.attachments.repository.AttachmentsRepository;
import team.startup.joycenter.domain.post.dto.response.FindAllPostResponse;
import team.startup.joycenter.domain.post.entity.Post;
import team.startup.joycenter.domain.post.entity.constant.Sort;
import team.startup.joycenter.domain.post.repostory.PostRepository;
import team.startup.joycenter.domain.post.service.FindAllPostService;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class FindAllPostServiceImpl implements FindAllPostService {

    private final PostRepository postRepository;
    private final AttachmentsRepository attachmentsRepository;

    @Override
    @Transactional(readOnly = true)
    public FindAllPostResponse execute(Sort sort, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, sort.toSpringSort());
        Page<Post> posts = postRepository.findAll(pageable);

        List<FindAllPostResponse.Item> content = createThumbs(posts);

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

    private List<FindAllPostResponse.Item> createThumbs(Page<Post> posts) {
        List<Long> postIds = posts.getContent().stream()
                .map(Post::getId)
                .toList();

        List<Attachments> thumbs = attachmentsRepository.findAllByPostIdInAndAttachmentsTypeOrderByPostIdAscImageOrderAsc(postIds, AttachmentsType.IMAGE);
        Map<Long, Attachments> thumbMap = new java.util.HashMap<>();

        for (Attachments a : thumbs) {
            thumbMap.putIfAbsent(a.getPost().getId(), a);
        }

        List<FindAllPostResponse.Item> content = posts.getContent().stream()
                .map(post -> {
                    Attachments a = thumbMap.get(post.getId());

                    FindAllPostResponse.Thumbnail thumb =
                            (a == null) ? new FindAllPostResponse.Thumbnail(null, null)
                                    : new FindAllPostResponse.Thumbnail(a.getId(), a.getAttachmentsUrl());

                    return new FindAllPostResponse.Item(
                            post.getId(),
                            post.getTitle(),
                            new FindAllPostResponse.Member(post.getAuthor().getId(), post.getAuthorName()),
                            thumb
                    );
                })
                .toList();
        return content;
    }
}
