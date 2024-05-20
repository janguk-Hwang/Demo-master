package com.example.demo.configuration;

import com.example.demo.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@RequiredArgsConstructor
@EnableWebSecurity
@Configuration
public class SecurityConfiguration{

    private final MemberService memberService;

    @Bean
    public PasswordEncoder getPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    UserAuthenticationFailureHandler getFailureHandler() {
        return new UserAuthenticationFailureHandler();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        HttpSessionRequestCache requestCache = new HttpSessionRequestCache();
        requestCache.setMatchingRequestParameterName(null);

        http.csrf(csrf -> csrf.disable());

        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/",
                                "/member/register",
                                "/member/email-auth",
                                "/member/find/password",
                                "/member/reset/password"
                        )
                        .permitAll()
                        .requestMatchers("/admin/**")
                        .hasAuthority("ROLE_ADMIN")
                        .anyRequest() // 나머지 사이트는 권한 요청
                        .authenticated())
                .formLogin((formLogin) -> formLogin
                        .loginPage("/")
                        .failureHandler(getFailureHandler())
                        .defaultSuccessUrl("/index_result")
                        .permitAll())
                .logout((logout) -> logout
                        .logoutRequestMatcher(new AntPathRequestMatcher("/member/logout"))
                        .logoutSuccessUrl("/")
                        .invalidateHttpSession(true))
                .exceptionHandling((exception) -> exception
                        .accessDeniedPage("/error/denied"));

        return http.build();
    }



    protected AuthenticationManager configure(AuthenticationManagerBuilder auth) throws Exception {

        auth.userDetailsService(memberService)
                .passwordEncoder(getPasswordEncoder());

        return auth.build();
    }

}