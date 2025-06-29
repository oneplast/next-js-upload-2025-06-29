package com.ll.global.security;

import com.ll.domain.member.member.entity.Member;
import com.ll.domain.member.member.service.MemberService;
import java.util.Locale;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {
    private final MemberService memberService;

    @Transactional
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        String oauthId = oAuth2User.getName();
        String providerTypeCode = userRequest
                .getClientRegistration()
                .getRegistrationId()
                .toUpperCase(Locale.getDefault());

        Map<String, Object> attributes = oAuth2User.getAttributes();
        Map<String, String> attributesProperties = (Map<String, String>) attributes.get("properties");

        String nickname = attributesProperties.get("nickname");
        String profileImgUrl = attributesProperties.get("profile_image");
        String username = providerTypeCode + "__" + oauthId;

        Member member = this.memberService.modifyOrJoin(username, nickname, profileImgUrl);

        return new SecurityUser(
                member.getId(),
                member.getUsername(),
                "",
                member.getNickname(),
                member.getAuthorities()
        );
    }
}
