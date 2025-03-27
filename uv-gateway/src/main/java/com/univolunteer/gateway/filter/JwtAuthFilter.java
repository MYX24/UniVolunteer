

package com.univolunteer.gateway.filter;

import com.univolunteer.common.enums.UserRoleEnum;
import com.univolunteer.gateway.utils.JwtUtils;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.util.pattern.PathPatternParser;
import reactor.core.publisher.Mono;
import java.util.List;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthFilter implements GlobalFilter, Ordered {

    private final JwtUtils jwtUtils;

    // 登录、注册等不需要校验 token 的路径
    private static final List<String> WHITE_LIST = List.of(
            "/user/login",
            "/user/register"
    );

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String path = exchange.getRequest().getPath().toString();

        // 白名单路径直接放行
        for (String white : WHITE_LIST) {
            if (path.startsWith(white)) {
                return chain.filter(exchange);
            }
        }

        // 从请求头获取 token
        String token = exchange.getRequest().getHeaders().getFirst(AUTHORIZATION);
        log.info("JWT token: {}", token);
        if (token == null || !token.startsWith("Bearer ")) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        token = token.substring(7); // 去掉 "Bearer " 前缀

        try {
            Claims claims = jwtUtils.parseToken(token);
            Long userId = claims.get("userId", Long.class);
            String username = claims.get("username", String.class);
            String role = claims.get("role", String.class);

            // 构建新的请求并添加用户信息到 header 中
            ServerWebExchange mutatedExchange = exchange.mutate()
                    .request(builder -> builder
                            .header("userId", userId.toString())
                            .header("username", username)
                            .header("role", String.valueOf(role))
                    )
                    .build();

            return chain.filter(mutatedExchange);

        } catch (Exception e) {
            log.error("JWT 解析失败：{}", e.getMessage());
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }
    }

    @Override
    public int getOrder() {
        return -100; // 保证优先执行
    }
}
