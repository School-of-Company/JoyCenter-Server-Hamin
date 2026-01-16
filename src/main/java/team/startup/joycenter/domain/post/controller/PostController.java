package team.startup.joycenter.domain.post.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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

    @Operation(
            summary = "게시글 생성",
            description = "게시글을 생성합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "생성 성공"),
            @ApiResponse(responseCode = "400", description = "요청 값이 유효하지 않음", content = @Content),
    })
    @PostMapping
    public ResponseEntity<Void> createPost(@RequestBody CreatePostRequest createPostRequest) {
        createPostService.execute(createPostRequest);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Operation(
            summary = "게시글 단건 조회",
            description = "postId로 게시글을 조회합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공",
                    content = @Content(schema = @Schema(implementation = FindPostResponse.class))),
            @ApiResponse(responseCode = "404", description = "게시글을 찾을 수 없음", content = @Content)
    })
    @GetMapping("/{postId}")
    public ResponseEntity<FindPostResponse> findPost(
            @Parameter(description = "게시글 ID", required = true, example = "1")
            @PathVariable("postId") Long postId) {
        FindPostResponse response = findPostService.execute(postId);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "게시글 전체 조회",
            description = "sort/page/size로 게시글을 페이징 조회합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공",
                    content = @Content(schema = @Schema(implementation = FindAllPostResponse.class))),
            @ApiResponse(responseCode = "400", description = "파라미터 형식 오류", content = @Content),
    })
    @GetMapping("/all")
    public ResponseEntity<FindAllPostResponse> findAllPost(
            @Parameter(
                    description = "정렬 기준",
                    example = "CREATED_AT_DESC"
            )
            @RequestParam(defaultValue = "CREATED_AT_DESC") Sort sort,

            @Parameter(
                    description = "페이지 번호",
                    example = "0"
            )
            @RequestParam(defaultValue = "0") int page,

            @Parameter(
                    description = "페이지 크기",
                    example = "20"
            )
            @RequestParam(defaultValue = "20") int size
    ) {
        FindAllPostResponse response = findAllPostService.execute(sort, page, size);
        return ResponseEntity.ok(response);
    }
}
