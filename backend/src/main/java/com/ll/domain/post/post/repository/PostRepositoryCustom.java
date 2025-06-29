package com.ll.domain.post.post.repository;

import com.ll.domain.member.member.entity.Member;
import com.ll.domain.post.post.entity.Post;
import com.ll.global.search.PostSearchKeywordTypeV1;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PostRepositoryCustom {
    Page<Post> findByKw(PostSearchKeywordTypeV1 kwType, String kw, Member author, Boolean published, Boolean listed,
                        Pageable pageable);
}
