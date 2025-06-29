package com.ll.domain.post.post.dto;

import com.ll.domain.post.post.entity.Post;
import lombok.Getter;
import lombok.Setter;
import org.springframework.lang.NonNull;

@Getter
public class PostWithContentDto extends PostDto {
    @NonNull
    private final String content;

    @Setter
    private Boolean actorCanModify;

    @Setter
    private Boolean actorCanDelete;

    public PostWithContentDto(Post post) {
        super(post);
        this.content = post.getContent();
    }
}
