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
import java.util.stream.Collectors;

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

        List<FindAllPostResponse.Item> items = createItems(posts);
        FindAllPostResponse.PageInfo pageInfo = createPageInfo(posts);

        return new FindAllPostResponse(items, pageInfo);
    }

    private List<FindAllPostResponse.Item> createItems(Page<Post> posts) {
        Map<Long, Attachments> thumbnailMap = getThumbnailMap(posts);

        return posts.getContent().stream()
                .map(post -> {
                    Attachments thumbnail = thumbnailMap.get(post.getId());

                    FindAllPostResponse.Thumbnail thumbnailInfo = (thumbnail == null)
                            ? new FindAllPostResponse.Thumbnail(null, null)
                            : new FindAllPostResponse.Thumbnail(thumbnail.getId(), thumbnail.getAttachmentsUrl());

                    return new FindAllPostResponse.Item(
                            post.getId(),
                            post.getTitle(),
                            new FindAllPostResponse.Member(post.getAuthor().getId(), post.getAuthorName()),
                            thumbnailInfo
                    );
                })
                .toList();
    }

    private Map<Long, Attachments> getThumbnailMap(Page<Post> posts) {
        List<Long> postIds = posts.getContent().stream()
                .map(Post::getId)
                .toList();

        List<Attachments> thumbnails = attachmentsRepository
                .findAllByPostIdInAndAttachmentsTypeOrderByPostIdAscImageOrderAsc(postIds, AttachmentsType.IMAGE);

        return thumbnails.stream()
                .collect(Collectors.toMap(
                        attachment -> attachment.getPost().getId(),
                        attachment -> attachment,
                        (existing, replacement) -> existing
                ));
    }

    private FindAllPostResponse.PageInfo createPageInfo(Page<Post> posts) {
        return new FindAllPostResponse.PageInfo(
                posts.getNumber(),
                posts.getSize(),
                posts.getTotalElements(),
                posts.getTotalPages(),
                posts.hasNext(),
                posts.hasPrevious()
        );
    }
}