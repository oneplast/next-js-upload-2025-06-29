package com.ll.domain.post.genFile.controller;

import com.ll.domain.post.genFile.entity.PostGenFile;
import com.ll.domain.post.post.entity.Post;
import com.ll.domain.post.post.service.PostService;
import com.ll.util.Ut;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/post/genFile")
@RequiredArgsConstructor
@Tag(name = "PostGenFileController", description = "파일 다운로드 등 다양한 기능 제공")
public class PostGenFileController {
    private final PostService postService;

    @GetMapping("/download/{postId}/{fileName}")
    @Operation(summary = "파일 다운로드")
    public ResponseEntity<Resource> download(@PathVariable long postId, @PathVariable String fileName,
                                             HttpServletRequest request) throws FileNotFoundException {
        Post post = postService.findById(postId).get();

        PostGenFile genFile = post.getGenFiles().stream()
                .filter(f -> f.getFileName().equals(fileName))
                .findFirst()
                .get();

        String filePath = genFile.getFilePath();

        Resource resource = new InputStreamResource(new FileInputStream(filePath));

        String contentType = request.getServletContext().getMimeType(filePath);

        if (contentType == null) {
            contentType = "application/octet-stream";
        }

        String downloadFileName = Ut.url.encode(genFile.getOriginalFileName()).replace("+", " ");

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + downloadFileName + "\"")
                .contentType(MediaType.parseMediaType(contentType)).body(resource);
    }
}
