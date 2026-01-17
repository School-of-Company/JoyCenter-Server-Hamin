package team.startup.joycenter.domain.post.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import team.startup.joycenter.domain.attachments.entity.Attachments;
import team.startup.joycenter.domain.post.entity.constant.BlockType;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Table(name = "post_block")
public class PostBlock {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_block_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "post_id")
    private Post post;

    @Column(name = "block_order", nullable = false)
    private Integer order;

    @Column(name = "text", length = 1000)
    private String text;

    @ManyToOne
    @JoinColumn(name = "attachments_id")
    private Attachments attachments;

    @Enumerated(EnumType.STRING)
    @Column(name = "block_type", nullable = false)
    private BlockType blockType;
}
