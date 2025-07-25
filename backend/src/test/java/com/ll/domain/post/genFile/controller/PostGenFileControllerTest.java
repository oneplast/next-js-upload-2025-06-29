package com.ll.domain.post.genFile.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.handler;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.ll.domain.member.member.service.MemberService;
import com.ll.domain.post.genFile.entity.PostGenFile;
import com.ll.domain.post.post.entity.Post;
import com.ll.domain.post.post.service.PostService;
import com.ll.util.Ut;
import org.apache.tika.metadata.HttpHeaders;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@Transactional
public class PostGenFileControllerTest {
    @Autowired
    private PostService postService;

    @Autowired
    private MemberService memberService;

    @Autowired
    private MockMvc mvc;

    @Test
    @DisplayName("다운로드 테스트")
    void t1() throws Exception {
        Post post9 = postService.findById(9).get();

        PostGenFile postGenFile1 = post9.getGenFiles().getFirst();

        String downloadUrl = Ut.url.removeDomain(postGenFile1.getDownloadUrl());

        ResultActions resultActions = mvc
                .perform(
                        get(downloadUrl)
                )
                .andDo(print());

        resultActions
                .andExpect(handler().handlerType(PostGenFileController.class))
                .andExpect(handler().methodName("download"))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" +
                                                                            postGenFile1.getOriginalFileName() + "\""))
                .andExpect(content().contentType(MediaType.IMAGE_GIF));
    }
}
