package team.startup.joycenter.domain.post.service;

import team.startup.joycenter.domain.post.dto.request.CreatePostRequest;

public interface CreatePostService {
    void execute(CreatePostRequest request);
}
