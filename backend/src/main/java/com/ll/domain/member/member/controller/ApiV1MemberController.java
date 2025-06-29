package com.ll.domain.member.member.controller;

import com.ll.domain.member.member.dto.MemberDto;
import com.ll.domain.member.member.entity.Member;
import com.ll.domain.member.member.service.AuthTokenService;
import com.ll.domain.member.member.service.MemberService;
import com.ll.global.dto.Empty;
import com.ll.global.exceptions.ServiceException;
import com.ll.global.rq.Rq;
import com.ll.global.rsData.RsData;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/members")
@Tag(name = "ApiV1MemberController", description = "API 회원 컨트롤러")
@SecurityRequirement(name = "bearerAuth")
public class ApiV1MemberController {
    private final MemberService memberService;
    private final AuthTokenService authTokenService;
    private final Rq rq;

    record MemberJoinReqBody(
            @NotBlank
            String username,
            @NotBlank
            String password,
            @NotBlank
            String nickname,
            String profileImgUrl
    ) {
    }

    @PostMapping("/join")
    @Transactional
    @Operation(summary = "회원가입")
    public RsData<MemberDto> join(@RequestBody @Valid MemberJoinReqBody reqBody) {
        Member member = memberService.join(reqBody.username, reqBody.password, reqBody.nickname, "");

        return new RsData<>("201-1", "%s님 환영합니다. 회원가입이 완료되었습니다.".formatted(member.getName()),
                new MemberDto(member));
    }

    record MemberLoginReqBody(
            @NotBlank
            String username,
            @NotBlank
            String password
    ) {
    }

    record MemberLoginResBody(
            @NonNull
            MemberDto item,
            @NonNull
            String apiKey,
            @NonNull
            String accessToken
    ) {
    }

    @PostMapping("/login")
    @Transactional(readOnly = true)
    @Operation(summary = "로그인", description = "apiKey, accessToken 을 발급합니다. 해당 토큰들은 쿠키(HTTP-ONLY)로도 전달됩니다.")
    public RsData<MemberLoginResBody> login(@RequestBody @Valid MemberLoginReqBody reqBody) {
        Member member = memberService.findByUsername(reqBody.username).orElseThrow(() ->
                new ServiceException("401-1", "존재하지 않는 사용자입니다."));

        if (!member.matchPassword(reqBody.password)) {
            throw new ServiceException("401-2", "비밀번호가 일치하지 않습니다.");
        }

        String accessToken = rq.makeAuthCookies(member);

        return new RsData<>("200-1", "%s님 환영합니다.".formatted(member.getName()),
                new MemberLoginResBody(new MemberDto(member), member.getApiKey(), accessToken));
    }

    @DeleteMapping("/logout")
    @Transactional(readOnly = true)
    @Operation(summary = "로그아웃", description = "apiKey, accessToken 을 제거합니다.")
    public RsData<Empty> logout() {
        rq.deleteCookie("accessToken");
        rq.deleteCookie("apiKey");

        return new RsData<>(
                "200-1",
                "로그아웃 되었습니다."
        );
    }

    @GetMapping("/me")
    @Transactional(readOnly = true)
    @Operation(summary = "내 정보")
    public MemberDto me() {
        Member actor = memberService.findById(rq.getActor().getId()).get();

        return new MemberDto(actor);
    }

    record MemberModifyMeReqBody(
            @NotBlank
            String nickname
    ) {
    }

    @PutMapping("/me")
    @Transactional
    @Operation(summary = "내 정보 수정")
    public RsData<MemberDto> modifyMe(
            @RequestBody @Valid MemberModifyMeReqBody reqBody
    ) {
        Member actor = memberService.findByUsername(rq.getActor().getUsername()).get();

        memberService.modify(actor, reqBody.nickname, "");

        rq.refreshAccessToken(actor);

        return new RsData<>(
                "200-1",
                "회원정보가 수정되었습니다.",
                new MemberDto(actor)
        );
    }
}
