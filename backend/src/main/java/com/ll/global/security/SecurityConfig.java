package com.ll.global.security;

import com.ll.global.app.AppConfig;
import com.ll.global.rsData.RsData;
import com.ll.util.Ut;
import java.util.Arrays;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer.FrameOptionsConfig;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {
    private final CustomAuthenticationFilter customAuthenticationFilter;
    private final CustomOAuth2AuthenticationSuccessHandler customOAuth2AuthenticationSuccessHandler;
    private final CustomAuthorizationRequestResolver customAuthorizationRequestResolver;

    @Bean
    SecurityFilterChain baseSecurityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(authorizeRequests ->
                                authorizeRequests
//                                .requestMatchers("/h2-console/login.do")    // 상위 룰 우선이기 때문에 의미 없음
//                                .authenticated()                              // 상위 룰 우선이기 때문에 의미 없음
                                        .requestMatchers(HttpMethod.GET,
                                                "/api/*/posts/{id:\\d+}",
                                                "/api/*/posts",
                                                "/api/*/posts/{postId:\\d+}/comments",
                                                "/api/*/posts/{postId:\\d+}/genFiles",
                                                "/api/*/posts/{postId:\\d+}/genFiles/{id:\\d+}")
                                        .permitAll()
                                        .requestMatchers("/api/*/members/login",
                                                "/api/*/members/logout",
                                                "/api/*/members/join")
                                        .permitAll()
                                        .requestMatchers(HttpMethod.GET,
                                                "/api/*/posts",
                                                "/api/*/posts/{id:\\d+}",
                                                "/api/*/posts/{postId:\\d+}/comments")
                                        .permitAll()
                                        .requestMatchers(HttpMethod.GET, "/api/*/posts/statistics", "/api/*/adm/members/**")
                                        .hasRole("ADMIN")
                                        .requestMatchers("/api/*/**")
                                        .authenticated()
                                        .anyRequest()
                                        .permitAll()
                )
                .headers(headers -> headers.frameOptions(FrameOptionsConfig::sameOrigin))
                .csrf(AbstractHttpConfigurer::disable)
                .oauth2Login(oauth2Login -> oauth2Login
                        .successHandler(customOAuth2AuthenticationSuccessHandler)
                        .authorizationEndpoint(authorizationEndPoint -> authorizationEndPoint
                                .authorizationRequestResolver(customAuthorizationRequestResolver))
                )
                .formLogin(AbstractHttpConfigurer::disable)
                .sessionManagement(sessionManagement -> sessionManagement
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(customAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling(exceptionHandling -> exceptionHandling
                        .authenticationEntryPoint(
                                (request, response, authException) -> {
                                    response.setContentType("application/json;charset=UTF-8");

                                    response.setStatus(401);
                                    response.getWriter().write(
                                            Ut.json.toString(
                                                    new RsData<>("401-1", "사용자 인증정보가 올바르지 않습니다.")
                                            )
                                    );
                                }
                        )
                        .accessDeniedHandler(
                                (request, response, accessDeniedException) -> {
                                    response.setContentType("application/json;charset=UTF-8");

                                    response.setStatus(403);
                                    response.getWriter().write(
                                            Ut.json.toString(
                                                    new RsData<>("403-1", "권한이 없습니다.")
                                            )
                                    );
                                }
                        )
                )
        ;

        return http.build();
    }

    @Bean
    public UrlBasedCorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // 허용할 오리진 설정
        configuration.setAllowedOrigins(Arrays.asList("https://cdpn.io", AppConfig.getSiteFrontUrl()));

        // 허용할 HTTP 메서드 설정
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE"));

        // 자격 증명 허용 설정
        configuration.setAllowCredentials(true);

        // 허용할 헤더 설정
        configuration.setAllowedHeaders(Arrays.asList("*"));

        // CORS 설정을 소스에 등록
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/api/**", configuration);

        return source;
    }
}
