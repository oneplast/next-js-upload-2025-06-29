package com.ll.domain.post.post.controller;

import com.ll.domain.member.member.entity.Member;
import com.ll.domain.post.post.dto.PostDto;
import com.ll.domain.post.post.dto.PostWithContentDto;
import com.ll.domain.post.post.entity.Post;
import com.ll.domain.post.post.service.PostService;
import com.ll.global.dto.Empty;
import com.ll.global.dto.PageDto;
import com.ll.global.exceptions.ServiceException;
import com.ll.global.rq.Rq;
import com.ll.global.rsData.RsData;
import com.ll.global.search.PostSearchKeywordTypeV1;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/posts")
@RequiredArgsConstructor
@Tag(name = "ApiV1PostController", description = "API 글 컨트롤러")
@SecurityRequirement(name = "bearerAuth")
public class ApiV1PostController {
    private final PostService postService;
    private final Rq rq;

    private PostWithContentDto makePostWithContentDto(Post post) {
        Member actor = rq.getActor();

        PostWithContentDto postWithContentDto = new PostWithContentDto(post);

        if (actor != null) {
            postWithContentDto.setActorCanModify(post.getCheckActorCanModifyRs(actor).isSuccess());
            postWithContentDto.setActorCanDelete(post.getCheckActorCanDeleteRs(actor).isSuccess());
        }

        return postWithContentDto;
    }

    record PostStatisticsResBody(
            @NonNull
            long totalPostCount,
            @NonNull
            long totalPublishedPostCount,
            @NonNull
            long totalListedPostCount
    ) {
    }

    @GetMapping("/statistics")
    @Transactional(readOnly = true)
    @Operation(summary = "통계정보")
    public PostStatisticsResBody statistics() {
        Member actor = rq.getActor();

        return new PostStatisticsResBody(
                postService.count(),
                postService.countByPublished(true),
                postService.countByListed(true)
        );
    }

    @GetMapping("/mine")
    @Transactional(readOnly = true)
    @Operation(summary = "내 글 다건 조회")
    public PageDto<PostDto> mine(
            @RequestParam(defaultValue = "title") PostSearchKeywordTypeV1 searchKeywordType,
            @RequestParam(defaultValue = "") String searchKeyword,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "30") int pageSize) {
        Member actor = rq.getActor();

        return new PageDto<>(
                postService.findByAuthorPaged(actor, searchKeywordType, searchKeyword, page, pageSize)
                        .map(PostDto::new)
        );
    }

    @GetMapping
    @Transactional(readOnly = true)
    @Operation(summary = "공개글 다건 조회")
    public PageDto<PostDto> items(
            @RequestParam(defaultValue = "title") PostSearchKeywordTypeV1 searchKeywordType,
            @RequestParam(defaultValue = "") String searchKeyword,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "30") int pageSize) {
        return new PageDto<>(
                postService.findByListedPaged(true, searchKeywordType, searchKeyword, page, pageSize)
                        .map(PostDto::new)
        );
    }

    @GetMapping("/{id}")
    @Operation(summary = "단건 조회", description = "비밀글은 작성자만 조회 가능")
    @Transactional(readOnly = true)
    public PostWithContentDto item(@PathVariable long id) {
        Post post = postService.findById(id).get();

        if (!post.isPublished()) {
            Member actor = rq.getActor();

            if (actor == null) {
                throw new ServiceException("401-1", "비밀글 입니다. 로그인 후 이용해주세요.");
            }

            post.checkActorCanRead(actor);
        }

        return makePostWithContentDto(post);
    }

    @Transactional
    @PostMapping("/temp")
    @Operation(summary = "임시 글 생성")
    public RsData<PostDto> makeTemp() {
        RsData<Post> findTempOrMakeRsData = postService.findTempOrMake(rq.getActor());

        return findTempOrMakeRsData.newDataOf(
                new PostDto(findTempOrMakeRsData.getData())
        );
    }

    record PostWriteReqBody(
            @NotBlank
            @Size(min = 2, max = 100)
            String title,
            @NotBlank
            @Size(min = 2, max = 10000000)
            String content,
            boolean published,
            boolean listed
    ) {
    }

    @PostMapping
    @Transactional
    @Operation(summary = "작성")
    public RsData<PostDto> write(@RequestBody @Valid PostWriteReqBody reqBody) {
        Member actor = rq.getActor();

//        if (user != null) {
//            Authentication authentication = (Authentication) principal;
//            UserDetails user = (UserDetails) authentication.getPrincipal();
//            actor = rq.getActorByUsername(user.getUsername());
//        }

        Post post = this.postService.write(actor, reqBody.title, reqBody.content, reqBody.published, reqBody.listed);

        return new RsData<>(
                "201-1",
                "%d번 글이 작성되었습니다.".formatted(post.getId()),
                new PostDto(post)
        );
    }

    record PostModifyReqBody(
            @NotBlank
            @Size(min = 2, max = 100)
            String title,
            @NotBlank
            @Size(min = 2, max = 10000000)
            String content,
            boolean published,
            boolean listed
    ) {
    }

    @PutMapping("/{id}")
    @Transactional
    @Operation(summary = "수정")
    public RsData<PostDto> modify(@PathVariable long id, @RequestBody @Valid PostModifyReqBody reqBody) {
        Member actor = this.rq.getActor();

        Post post = this.postService.findById(id).get();

        post.checkActorCanModify(actor);

        postService.modify(post, reqBody.title, reqBody.content, reqBody.published, reqBody.listed);

        postService.flush();

        return new RsData<>(
                "200-1",
                "%d번 글이 수정되었습니다.".formatted(id),
                new PostDto(post)
        );
    }

    @DeleteMapping("/{id}")
    @Transactional
    @Operation(summary = "삭제", description = "작성자 본인 뿐만 아니라 관리자도 삭제 가능")
    public RsData<Empty> delete(@PathVariable long id) {
        Member member = rq.getActor();

        Post post = postService.findById(id).get();

        post.checkActorCanDelete(member);

        postService.delete(post);

        return new RsData<>("200-1", "%d번 글이 삭제되었습니다.".formatted(id));
    }
}
