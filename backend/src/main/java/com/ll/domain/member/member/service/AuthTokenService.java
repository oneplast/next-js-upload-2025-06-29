package com.ll.domain.member.member.service;

import com.ll.domain.member.member.entity.Member;
import com.ll.util.Ut;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthTokenService {
    @Value("${custom.jwt.secretKey}")
    private String jwtSecretKey;

    @Value("${custom.accessToken.expirationSeconds}")
    private long accessTokenExpirationSeconds;


    String genAccessToken(Member member) {
        long id = member.getId();
        String username = member.getUsername();
        String nickname = member.getNickname();

        return Ut.jwt.toString(
                jwtSecretKey,
                accessTokenExpirationSeconds,
                Map.of("id", id,
                        "username", username,
                        "nickname", nickname,
                        "authorities", member.getAuthoritiesAsStringList())
        );
    }

    Map<String, Object> payload(String accessToken) {
        Map<String, Object> parsedPayload = Ut.jwt.payload(jwtSecretKey, accessToken);

        if (parsedPayload == null) {
            return null;
        }

        long id = (long) (Integer) parsedPayload.get("id");
        String username = (String) parsedPayload.get("username");
        String nickname = (String) parsedPayload.get("nickname");
        List<String> authorities = (List<String>) parsedPayload.get("authorities");

        return Map.of("id", id, "username", username, "nickname", nickname, "authorities", authorities);
    }
}
