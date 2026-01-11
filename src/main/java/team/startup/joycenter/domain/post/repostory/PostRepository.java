package team.startup.joycenter.domain.post.repostory;

import org.springframework.data.jpa.repository.JpaRepository;
import team.startup.joycenter.domain.post.entity.Post;

public interface PostRepository extends JpaRepository<Post, Long> {
}
