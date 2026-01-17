package team.startup.joycenter.domain.post.repostory;

import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import team.startup.joycenter.domain.post.entity.PostBlock;

import java.util.List;

public interface PostBlockRepository extends JpaRepository<PostBlock, Long> {
    List<PostBlock> findAllByPostIdOrderByOrderAsc(Long postId);
    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("delete from PostBlock pb where pb.post.id = :postId")
    void deleteAllByPostId(@Param("postId") Long postId);
    List<PostBlock> findByPostId(Long postId);
}
