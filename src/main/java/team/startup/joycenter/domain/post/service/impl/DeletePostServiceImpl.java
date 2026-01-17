package team.startup.joycenter.domain.post.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team.startup.joycenter.domain.attachments.entity.Attachments;
import team.startup.joycenter.domain.attachments.repository.AttachmentsRepository;
import team.startup.joycenter.domain.member.entity.Member;
import team.startup.joycenter.domain.post.entity.Post;
import team.startup.joycenter.domain.post.entity.PostBlock;
import team.startup.joycenter.domain.post.exception.NotFoundPostException;
import team.startup.joycenter.domain.post.exception.NotPostOwnerException;
import team.startup.joycenter.domain.post.repostory.PostBlockRepository;
import team.startup.joycenter.domain.post.repostory.PostRepository;
import team.startup.joycenter.domain.post.service.DeletePostService;
import team.startup.joycenter.global.s3.service.DeleteAllService;
import team.startup.joycenter.global.util.MemberUtil;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DeletePostServiceImpl implements DeletePostService {

    private final PostRepository postRepository;
    private final AttachmentsRepository attachmentsRepository;
    private final PostBlockRepository postBlockRepository;
    private final MemberUtil memberUtil;
    private final DeleteAllService deleteAllService;

    @Override
    @Transactional
    public void execute(Long postId) {
        Member member  = memberUtil.getCurrentMember();

        Post post = postRepository.findById(postId)
                .orElseThrow(NotFoundPostException::new);

        validateOwner(post, member.getId());

        List<Attachments> attachments = attachmentsRepository.findByPostId(postId);
        List<String> s3Keys = attachmentsRepository.findS3KeysByPostId(postId);

        deleteAllService.execute(s3Keys);
        attachmentsRepository.deleteAll(attachments);

        List<PostBlock> postBlocks = postBlockRepository.findByPostId(postId);

        postBlockRepository.deleteAll(postBlocks);
        postRepository.delete(post);
    }

    private void validateOwner(Post post, Long memberId) {
        if (!post.getAuthor().getId().equals(memberId)) {
            throw new NotPostOwnerException();
        }
    }
}
