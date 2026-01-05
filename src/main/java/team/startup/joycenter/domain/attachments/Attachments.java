package team.startup.joycenter.domain.attachments;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import team.startup.joycenter.domain.attachments.constant.AttachmentsType;
import team.startup.joycenter.domain.post.entity.Post;

@Entity
@Table(name = "attachments")
@NoArgsConstructor
@AllArgsConstructor
@Getter
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
    @Column(name = "attachments_type")
    private AttachmentsType attachmentsType;
}
