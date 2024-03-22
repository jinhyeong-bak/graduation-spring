package com.example.demo.infrastructure.jwt;



import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

@RedisHash("token")
@Getter
public class RedisToken {
    @Id
    private String jwt;
    @TimeToLive
    private Long expiredTime;


    public static RedisToken createTokenInRedis(String jwt, Long expiredTime) {
        RedisToken token = new RedisToken();
        token.jwt = jwt;
        token.expiredTime = expiredTime;
        return token;
    }
}
