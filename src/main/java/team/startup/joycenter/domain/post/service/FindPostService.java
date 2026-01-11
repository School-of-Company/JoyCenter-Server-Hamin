package team.startup.joycenter.domain.post.service;

import team.startup.joycenter.domain.post.dto.response.FindPostResponse;

public interface FindPostService {
    FindPostResponse execute(Long postId);
}
