package com.ll.global.app;

import java.util.List;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "custom")
public class CustomConfigProperties {
    public record NotProdMember(String username, String nickname, String profileImgUrl) {
        public String apiKey() {
            return username;
        }
    }

    private List<NotProdMember> notProdMembers;
}
