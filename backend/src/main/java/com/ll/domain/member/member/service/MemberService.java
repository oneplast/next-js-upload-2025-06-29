package com.ll.domain.member.member.service;

import com.ll.domain.member.member.entity.Member;
import com.ll.domain.member.member.repository.MemberRepository;
import com.ll.global.exceptions.ServiceException;
import com.ll.global.search.MemberSearchKeywordTypeV1;
import jakarta.validation.constraints.NotBlank;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberService {
    private final AuthTokenService authTokenService;
    private final MemberRepository memberRepository;

    public long count() {
        return memberRepository.count();
    }

    public Member join(String username, String password, String nickname, String profileImgUrl) {
        findByUsername(username)
                .ifPresent(__ -> {
                    throw new IllegalArgumentException(
                            new ServiceException("409-1", "해당 username은 이미 사용중입니다."));
                });

        Member member = Member.builder()
                .username(username)
                .password(password)
                .nickname(nickname)
                .apiKey(UUID.randomUUID().toString())
                .profileImgUrl(profileImgUrl)
                .build();

        return memberRepository.save(member);
    }

    public Optional<Member> findByUsername(String username) {
        return memberRepository.findByUsername(username);
    }

    public Optional<Member> findById(long authorId) {
        return memberRepository.findById(authorId);
    }

    public Optional<Member> findByApiKey(String apiKey) {
        return memberRepository.findByApiKey(apiKey);
    }

    public String genAccessToken(Member member) {
        return authTokenService.genAccessToken(member);
    }

    public String genAuthToken(Member member) {
        return member.getApiKey() + " " + this.genAccessToken(member);
    }

    public Member getMemberFromAccessToken(String accessToken) {
        Map<String, Object> payload = authTokenService.payload(accessToken);

        if (payload == null) {
            return null;
        }

        long id = (long) payload.get("id");
        String username = (String) payload.get("username");
        String nickname = (String) payload.get("nickname");

        return new Member(id, username, nickname);
    }

    public Page<Member> findByPaged(MemberSearchKeywordTypeV1 searchKeywordType, String searchKeyword, int page,
                                    int pageSize) {
        Pageable pageable = PageRequest.of(page - 1, pageSize, Sort.by(Sort.Order.desc("id")));

        return memberRepository.findByKw(searchKeywordType, searchKeyword, pageable);
    }

    public Page<Member> findByPaged(int page, int pageSize) {
        return findByPaged(null, null, page, pageSize);
    }

    public void modify(Member member, @NotBlank String nickname, String profileImgUrl) {
        member.setNickname(nickname);
        member.setProfileImgUrl(profileImgUrl);
    }

    public Member modifyOrJoin(String username, String nickname, String profileImgUrl) {
        Optional<Member> opMember = findByUsername(username);

        if (opMember.isPresent()) {
            Member member = opMember.get();
            modify(member, nickname, profileImgUrl);
            return member;
        }

        return join(username, "", nickname, profileImgUrl);
    }
}
