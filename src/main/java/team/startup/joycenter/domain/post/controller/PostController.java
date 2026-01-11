package team.startup.joycenter.domain.post.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import team.startup.joycenter.domain.post.dto.request.CreatePostRequest;
import team.startup.joycenter.domain.post.dto.response.FindAllPostResponse;
import team.startup.joycenter.domain.post.dto.response.FindPostResponse;
import team.startup.joycenter.domain.post.entity.constant.Sort;
import team.startup.joycenter.domain.post.service.CreatePostService;
import team.startup.joycenter.domain.post.service.FindAllPostService;
import team.startup.joycenter.domain.post.service.FindPostService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/post")
public class PostController {

    private final CreatePostService createPostService;
    private final FindAllPostService findAllPostService;
    private final FindPostService findPostService;

    @PostMapping
    public ResponseEntity<Void> createPost(@RequestBody CreatePostRequest createPostRequest) {
        createPostService.execute(createPostRequest);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/{postId}")
    public ResponseEntity<FindPostResponse> findPost(@PathVariable("postId") Long postId) {
        FindPostResponse response = findPostService.execute(postId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/all")
    public ResponseEntity<FindAllPostResponse> findAllPost(
            @RequestParam(defaultValue = "CREATED_AT_DESC") Sort sort,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        FindAllPostResponse response = findAllPostService.execute(sort, page, size);
        return ResponseEntity.ok(response);
    }
}
