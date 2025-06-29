package com.ll.domain.member.member.dto;

import com.ll.domain.member.member.entity.Member;
import lombok.Getter;
import lombok.NonNull;

@Getter
public class MemberWithUsernameDto extends MemberDto {
    @NonNull
    private final String username;

    public MemberWithUsernameDto(Member member) {
        super(member);
        this.username = member.getUsername();
    }
}
