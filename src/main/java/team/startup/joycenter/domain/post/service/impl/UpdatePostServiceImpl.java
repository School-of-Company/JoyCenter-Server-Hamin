package team.startup.joycenter.domain.post.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team.startup.joycenter.domain.attachments.entity.Attachments;
import team.startup.joycenter.domain.attachments.exception.NotFoundAttachmentException;
import team.startup.joycenter.domain.attachments.repository.AttachmentsRepository;
import team.startup.joycenter.domain.member.entity.Member;
import team.startup.joycenter.domain.post.dto.request.UpdatePostRequest;
import team.startup.joycenter.domain.post.entity.Post;
import team.startup.joycenter.domain.post.entity.PostBlock;
import team.startup.joycenter.domain.post.entity.constant.BlockType;
import team.startup.joycenter.domain.post.exception.NotFoundPostException;
import team.startup.joycenter.domain.post.exception.NotPostOwnerException;
import team.startup.joycenter.domain.post.repostory.PostBlockRepository;
import team.startup.joycenter.domain.post.repostory.PostRepository;
import team.startup.joycenter.domain.post.service.UpdatePostService;
import team.startup.joycenter.global.util.MemberUtil;

import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UpdatePostServiceImpl implements UpdatePostService {

    private final PostRepository postRepository;
    private final PostBlockRepository postBlockRepository;
    private final AttachmentsRepository attachmentsRepository;
    private final MemberUtil memberUtil;

    @Override
    @Transactional
    public void execute(Long postId, UpdatePostRequest request) {
        Member member = memberUtil.getCurrentMember();
        Post post = postRepository.findById(postId)
                .orElseThrow(NotFoundPostException::new);

        validateOwner(post, member.getId());

        post.update(request.title());
        updatePostBlocks(post, request.blocks());
    }

    private void updatePostBlocks(Post post, List<UpdatePostRequest.BlockRequest> blocks) {
        postBlockRepository.deleteAllByPostId(post.getId());

        if (blocks == null) return;

        List<PostBlock> postBlocks = blocks.stream()
                .sorted(Comparator.comparing(UpdatePostRequest.BlockRequest::order))
                .map(blockReq -> createPostBlock(post, blockReq))
                .toList();

        postBlockRepository.saveAll(postBlocks);
    }

    private PostBlock createPostBlock(Post post, UpdatePostRequest.BlockRequest blockReq) {
        PostBlock.PostBlockBuilder builder = PostBlock.builder()
                .post(post)
                .blockType(blockReq.blockType())
                .order(blockReq.order());

        if (blockReq.blockType() == BlockType.TEXT) {
            return builder.text(blockReq.text()).build();
        }

        if (blockReq.blockType() == BlockType.ATTACHMENT) {
            Attachments attachment = findAndLinkAttachment(post, blockReq.attachmentId());
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

    private void validateOwner(Post post, Long memberId) {
        if (!post.getAuthor().getId().equals(memberId)) {
            throw new NotPostOwnerException();
        }
    }
}