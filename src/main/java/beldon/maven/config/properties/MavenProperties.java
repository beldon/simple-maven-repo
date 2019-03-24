package beldon.maven.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author beldon
 */
@Component
@ConfigurationProperties(prefix = "maven")
@Data
public class MavenProperties {

    /**
     * 本地maven仓库
     */
    private String localRepository = "./maven";

    /**
     * context 路径
     */
    private String contextPath = "/maven";

    /**
     * 用户名
     */
    private String user;

    /**
     * 密码
     */
    private String pass;

    /**
     * 仓库
     */
    private List<RepoProperties> repos;

}
