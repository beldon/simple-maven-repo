package beldon.maven.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "maven")
@Data
public class MavenProperties {
    /**
     * 根路径
     */
    private String root = "./maven";

    /**
     * context 路径
     */
    private String contextPath = "/maven";

    private String proxy = "http://maven.aliyun.com/nexus/content/groups/public/";

}
