package team.startup.joycenter.domain.post.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team.startup.joycenter.domain.attachments.entity.Attachments;
import team.startup.joycenter.domain.attachments.exception.NotFoundAttachmentException;
import team.startup.joycenter.domain.attachments.repository.AttachmentsRepository;
import team.startup.joycenter.domain.member.entity.Member;
import team.startup.joycenter.domain.post.dto.request.CreatePostRequest;
import team.startup.joycenter.domain.post.entity.Post;
import team.startup.joycenter.domain.post.entity.PostBlock;
import team.startup.joycenter.domain.post.entity.constant.BlockType;
import team.startup.joycenter.domain.post.repostory.PostBlockRepository;
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
    private final PostBlockRepository postBlockRepository;

    @Override
    @Transactional
    public void execute(CreatePostRequest request) {
        Member member = memberUtil.getCurrentMember();

        Post post = Post.builder()
                .author(member)
                .authorName(member.getEmail())
                .title(request.title())
                .build();

        postRepository.save(post);

        List<CreatePostRequest.BlockRequest> blocks = request.blocks();
        if (blocks == null || blocks.isEmpty()) return;

        blocks.forEach(blockRequest -> {
            PostBlock postBlock = createPostBlock(post, blockRequest);
            postBlockRepository.save(postBlock);
        });
    }

    private PostBlock createPostBlock(Post post, CreatePostRequest.BlockRequest blockRequest) {
        PostBlock.PostBlockBuilder builder = PostBlock.builder()
                .post(post)
                .blockType(blockRequest.blockType())
                .order(blockRequest.order());

        if (blockRequest.blockType() == BlockType.TEXT) {
            return builder.text(blockRequest.text()).build();
        }

        if (blockRequest.blockType() == BlockType.ATTACHMENT) {
            Attachments attachment = findAndLinkAttachment(post, blockRequest.attachmentId());
            return builder.attachments(attachment).build();
        }

        return builder.build();
    }

    private Attachments findAndLinkAttachment(Post post, Long attachmentId) {
        Attachments attachment = attachmentsRepository.findById(attachmentId)
                .orElseThrow(NotFoundAttachmentException::new);
        attachment.setPost(post);
        return attachment;
    }
}

