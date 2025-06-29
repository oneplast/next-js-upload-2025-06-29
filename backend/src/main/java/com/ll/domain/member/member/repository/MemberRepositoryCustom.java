package com.ll.domain.member.member.repository;

import com.ll.domain.member.member.entity.Member;
import com.ll.global.search.MemberSearchKeywordTypeV1;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface MemberRepositoryCustom {
    Page<Member> findByKw(MemberSearchKeywordTypeV1 kwType, String kw, Pageable pageable);
}
