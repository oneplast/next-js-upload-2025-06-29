package com.ll.domain.post.genFile.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.handler;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.ll.domain.member.member.service.MemberService;
import com.ll.domain.post.genFile.entity.PostGenFile;
import com.ll.domain.post.post.service.PostService;
import java.util.List;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@Transactional
class ApiV1PostGenFileControllerTest {
    @Autowired
    private PostService postService;

    @Autowired
    private MemberService memberService;

    @Autowired
    private MockMvc mvc;

    @Test
    @DisplayName("다건 조회")
    void t1() throws Exception {
        ResultActions resultActions = mvc
                .perform(
                        get("/api/v1/posts/8/genFiles")
                )
                .andDo(print());

        resultActions
                .andExpect(handler().handlerType(ApiV1PostGenFileController.class))
                .andExpect(handler().methodName("items"))
                .andExpect(status().isOk());

        List<PostGenFile> postGenFiles = postService.findById(8).get().getGenFiles();

        for (int i = 0; i < postGenFiles.size(); i++) {
            PostGenFile postGenFile = postGenFiles.get(i);

            resultActions
                    .andExpect(jsonPath("$[%d].id".formatted(i)).value(postGenFile.getId()))
                    .andExpect(jsonPath("$[%d].createDate".formatted(i)).value(
                            Matchers.startsWith(postGenFile.getCreateDate().toString().substring(0, 20))))
                    .andExpect(jsonPath("$[%d].modifyDate".formatted(i)).value(
                            Matchers.startsWith(postGenFile.getModifyDate().toString().substring(0, 20))))
                    .andExpect(jsonPath("$[%d].postId".formatted(i)).value(postGenFile.getPost().getId()))
                    .andExpect(jsonPath("$[%d].typeCode".formatted(i)).value(postGenFile.getTypeCode()))
                    .andExpect(jsonPath("$[%d].fileExtTypeCode".formatted(i)).value(postGenFile.getFileExtTypeCode()))
                    .andExpect(jsonPath("$[%d].fileExtType2Code".formatted(i)).value(postGenFile.getFileExtType2Code()))
                    .andExpect(jsonPath("$[%d].fileSize".formatted(i)).value(postGenFile.getFileSize()))
                    .andExpect(jsonPath("$[%d].fileNo".formatted(i)).value(postGenFile.getFileNo()))
                    .andExpect(jsonPath("$[%d].fileExt".formatted(i)).value(postGenFile.getFileExt()))
                    .andExpect(jsonPath("$[%d].fileDateDir".formatted(i)).value(postGenFile.getFileDateDir()))
                    .andExpect(
                            jsonPath("$[%d].originalFileName".formatted(i)).value(postGenFile.getOriginalFileName()))
                    .andExpect(jsonPath("$[%d].downloadUrl".formatted(i)).value(postGenFile.getDownloadUrl()))
                    .andExpect(jsonPath("$[%d].publicUrl".formatted(i)).value(postGenFile.getPublicUrl()));
        }
    }
}