package team.startup.joycenter.domain.attachments.entity;

import jakarta.persistence.*;
import lombok.*;
import team.startup.joycenter.domain.attachments.entity.constant.AttachmentsType;
import team.startup.joycenter.domain.post.entity.Post;

@Entity
@Table(name = "attachments")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class Attachments {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "attachments_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post post;

    @Column(name = "attachments_url",  nullable = false)
    private String attachmentsUrl;

    @Enumerated(EnumType.STRING)
    @Column(name = "attachments_type", nullable = false)
    private AttachmentsType attachmentsType;

    @Column(name = "image_order")
    private Integer imageOrder;

    @Column(name = "s3key", nullable = false)
    private String s3Key;
}
