package redis;


import lombok.Value;
import lombok.launch.PatchFixesHider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import org.springframework.context.annotation.Scope;
import org.springframework.data.
import org.springframework.data.redis.connection.RedisClusterConfiguration;
import org.springframework.data.redis.connection.RedisNode;
import org.springframework.data.redis.connection.RedisPassword;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;


import java.util.ArrayList;
import java.util.Arrays;

@Configuration
public class redisConfiguration {

    @Value("${redis.host:''}")
    private String redisHost;

    @Value("${redis.port:6379}")
    private String redisPort;

    @Value("${redis.password}")
    private String redisPassword;

    @Value("${redis.cluster.instance:''}")
    private String redisCluster;

    @Autowired
    private ProfileProps profileProps;

    private static ArrayList<String> clusterEnvs = new ArrayList<String>(Arrays.asList("UAT","DEV"));

    @Bean
    JedisConnectionFactory jedicConnectionFactory(){
        if(clusterEnvs.contains(profileProps.getActive())){
            return new JedisConnectionFactory(getRedisClusterConfig());
        }
        else{
            return new JedisConnectionFactory(getRedisSingleConfig());
        }

    }

    private RedisClusterConfiguration getRedisClusterConfig() {
        RedisClusterConfiguration clusterConfiguration = new RedisClusterConfiguration();
        try {
            String  [] redisClusterArray = redisCluster.split(",");
            for(String redisInstance : redisClusterArray){
                RedisNode node = new RedisNode(redisInstance.split(",")[0],
                        Integer.valueOf(redisInstance.split(":")[1]));
                clusterConfiguration.addClusterNode(node);
            }
        }catch (NullPointerException e){
            throw new IllegalStateException("Cluster is wrong");
        }

        return clusterConfiguration;
    }

    private RedisClusterConfiguration getRedisSingleConfig() {
        RedisClusterConfiguration clusterConfiguration = new RedisClusterConfiguration();
        RedisPassword redisPass =  RedisPassword.of(redisPassword);
        clusterConfiguration.setHostName(redisHost);
        clusterConfiguration.setPort(redisPort);
        clusterConfiguration.setPassword(redisPass);
        return clusterConfiguration;
    }
    @Bean
    @Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
    public RedisTemplate redisTemplate(){
        RedisTemplate<String,Object> redisTemplate = new RedisTemplate<String, Object>();
        redisTemplate.setConnectionFactory(jedicConnectionFactory());
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new StringRedisSerializer());
        redisTemplate.setHashKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new StringRedisSerializer());
        return redisTemplate;

    }


}
