package com.ll.global.search;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum MemberSearchKeywordTypeV1 {
    all("all"),
    username("username"),
    nickname("nickname");

    private final String value;
}
