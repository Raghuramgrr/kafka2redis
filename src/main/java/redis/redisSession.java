package redis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
@DependsOn("redisTemplate")
public class redisSession {

    @Autowired
    private RedisTemplate<String,String> redisTemplate;

    public String get(String key) { return  redisTemplate.opsForValue().get(key);}

    public void set(String key, String Value)
    {
        redisTemplate.opsForValue().set(key,Value);
    }
}
