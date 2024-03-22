package com.example.demo.security.filter;

import com.example.demo.domain.Account;
import com.example.demo.exception.TokenBlackListedException;
import com.example.demo.infrastructure.jwt.JwtUtil;
import com.example.demo.infrastructure.jwt.RedisToken;
import com.example.demo.repository.RedisTokenRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;




@Slf4j
@RequiredArgsConstructor
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final HandlerExceptionResolver handlerExceptionResolver;
    private final JwtUtil jwtUtil;
    private final RedisTokenRepository redisTokenRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {

        log.info("jwtAuthenticationFilter 호출");
        final String header = request.getHeader(HttpHeaders.AUTHORIZATION);
        if(header == null || !header.startsWith("Bearer ")) {
            log.info("jwt가 헤더에 존재하지 않음");
            chain.doFilter(request, response);
            return;
        }

        final String token = header.split(" ")[1].trim();
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            if(authentication == null) {
                jwtUtil.validate(token);       // 검증 실패 시 예외 발생

                if(redisTokenRepository.findById(token).isPresent()) {
                    throw new TokenBlackListedException("black list에 올라간 accessToken으로부터 요청이 왔습니다. token: " + token);
                }

                final Long userPk = jwtUtil.getUserPk(token);
                UserDetails userDetails = Account.createLoginAccount(userPk);

                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );


                SecurityContextHolder.getContext().setAuthentication(authToken);

            }

            chain.doFilter(request, response);
        } catch (Exception ex) {
            log.info("jwt validate exception 발생");
            handlerExceptionResolver.resolveException(request, response, null, ex);
        }


    }
}
