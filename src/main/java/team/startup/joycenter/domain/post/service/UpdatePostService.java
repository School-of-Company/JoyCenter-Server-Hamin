package team.startup.joycenter.domain.post.service;

import team.startup.joycenter.domain.post.dto.request.UpdatePostRequest;

public interface UpdatePostService {
    void execute(Long postId, UpdatePostRequest request);
}
