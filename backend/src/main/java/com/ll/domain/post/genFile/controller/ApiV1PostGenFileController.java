package com.ll.domain.post.genFile.controller;

import static org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE;

import com.ll.domain.member.member.entity.Member;
import com.ll.domain.post.genFile.dto.PostGenFileDto;
import com.ll.domain.post.genFile.entity.PostGenFile;
import com.ll.domain.post.post.entity.Post;
import com.ll.domain.post.post.service.PostService;
import com.ll.global.app.AppConfig;
import com.ll.global.exceptions.ServiceException;
import com.ll.global.rq.Rq;
import com.ll.global.rsData.RsData;
import com.ll.util.Ut;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/posts/{postId}/genFiles")
@RequiredArgsConstructor
@Tag(name = "ApiV1PostGenFileController", description = "API 글 파일 컨트롤러")
@SecurityRequirement(name = "bearerAuth")
public class ApiV1PostGenFileController {
    private final PostService postService;
    private final Rq rq;

    @PostMapping(value = "/{typeCode}", consumes = MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "등록")
    public RsData<PostGenFileDto> makeNewFile(
            @PathVariable long postId,
            @PathVariable PostGenFile.TypeCode typeCode,
            @NonNull @RequestParam("file") @Schema(type = "string", format = "binary") MultipartFile file
    ) {
        Member actor = rq.getActor();

        Post post = postService.findById(postId).orElseThrow(
                () -> new ServiceException("404-1", "%d번 글은 존재하지 않습니다.".formatted(postId))
        );

        post.checkActorCanMakeNewGenFile(actor);

        String filePath = Ut.file.toFile(file, AppConfig.getTempDirPath());

        PostGenFile postGenFile = post.addGenFile(typeCode, filePath);

        postService.flush();

        return new RsData<>(
                "201-1",
                "%d번 파일이 생성되었습니다.".formatted(postGenFile.getId()),
                new PostGenFileDto(postGenFile)
        );
    }

    @GetMapping
    @Transactional(readOnly = true)
    @Operation(summary = "다건조회")
    public List<PostGenFileDto> items(@PathVariable long postId) {
        Post post = postService.findById(postId).orElseThrow(
                () -> new ServiceException("404-1", "%d번 글은 존재하지 않습니다.".formatted(postId)));

        return post.getGenFiles().stream()
                .map(PostGenFileDto::new)
                .toList();
    }
}
