package com.ll.global.security;

import com.ll.domain.member.member.entity.Member;
import com.ll.domain.member.member.service.MemberService;
import com.ll.global.rq.Rq;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CustomOAuth2AuthenticationSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {
    private final MemberService memberService;
    private final Rq rq;

    @SneakyThrows
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) {
        Member actor = memberService.findById(rq.getActor().getId()).get();

        rq.makeAuthCookies(actor);

        String redirectUrl = request.getParameter("state");

        response.sendRedirect(redirectUrl);
    }
}
