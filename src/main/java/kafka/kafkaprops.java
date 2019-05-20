package kafka;

import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.jaas.memory.InMemoryConfiguration;

import javax.security.auth.login.AppConfigurationEntry;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Configuration
@Slf4j
public class kafkaprops {


    @Value("${kafka.kerberos.kdc}")
    private String kdc;

    @Value("${kafka.kerberos.realm}")
    private String realm;

    @Value("{$kafka.kerberos.keytab}")
    private String keytab;

    @Value("{$kafka.kerberos.principal}")
    private String principal;


    @Bean
    public InMemoryConfiguration kafkaprops() throws IOException{
        System.setProperty("java.security.krb5.kdc",kdc);
        System.setProperty("java.security.krb5.realm",realm);

        Map<String,Object> option = new HashMap<>();
        option.put("keytab",keytab);
        option.put("principal",principal);
        option.put("useKeytab","true");
        option.put("storeKey","true");
        AppConfigurationEntry clientConfig = new AppConfigurationEntry("com.security.auth.module.Krb5LoginModule", AppConfigurationEntry.LoginModuleControlFlag.REQUIRED,option);
        Map<String,AppConfigurationEntry[]> jaasConfigEntries = new HashMap<String, AppConfigurationEntry[]>();
        jaasConfigEntries.put("kafkaClient",new AppConfigurationEntry[]{clientConfig});
        InMemoryConfiguration jaasconfig = new InMemoryConfiguration(jaasConfigEntries);
        javax.security.auth.login.Configuration.setConfiguration(jaasconfig);
        return jaasconfig;


    }


}
