package team.startup.joycenter.domain.post.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team.startup.joycenter.domain.attachments.entity.Attachments;
import team.startup.joycenter.domain.attachments.repository.AttachmentsRepository;
import team.startup.joycenter.domain.member.entity.Member;
import team.startup.joycenter.domain.post.dto.request.CreatePostRequest;
import team.startup.joycenter.domain.post.entity.Post;
import team.startup.joycenter.domain.post.repostory.PostRepository;
import team.startup.joycenter.domain.post.service.CreatePostService;
import team.startup.joycenter.global.util.MemberUtil;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CreatePostServiceImpl implements CreatePostService {

    private final PostRepository postRepository;
    private final MemberUtil memberUtil;
    private final AttachmentsRepository attachmentsRepository;

    @Override
    @Transactional
    public void execute(CreatePostRequest request) {
        Member member = memberUtil.getCurrentMember();
        List<Attachments> attachments = attachmentsRepository.findByIdIn(request.attachmentsIds());

        Post post = Post.builder()
                .author(member)
                .authorName(member.getEmail())
                .title(request.title())
                .content(request.content())
                .build();

        postRepository.save(post);

        for (Attachments attachment : attachments) {
            attachment.setPost(post);
        }
    }
}
