package com.ll.domain.post.genFile.entity;

import com.ll.domain.base.genFile.genFile.entity.GenFile;
import com.ll.domain.post.post.entity.Post;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@Getter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class PostGenFile extends GenFile {
    @ManyToOne(fetch = FetchType.LAZY)
    private Post post;
}
