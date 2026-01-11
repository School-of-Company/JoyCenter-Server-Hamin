package team.startup.joycenter.domain.post.service;

import team.startup.joycenter.domain.post.dto.response.FindAllPostResponse;
import team.startup.joycenter.domain.post.entity.constant.Sort;

public interface FindAllPostService {
    FindAllPostResponse execute(Sort sort, int page, int size);
}
