package beldon.maven.bean;

import lombok.Data;

import java.util.List;

/**
 * @author Beldon
 */
@Data
public class RepositoryData {
    /**
     * id，唯一
     */
    private String id;

    /**
     * 名称
     */
    private String name;
    /**
     * 类型，group（仓库组），hosted（宿主仓库），proxy（代理仓库）
     */
    private String type = "hosted";

    /**
     * 仓库路径，若为空，则与id相同
     */
    private String path;

    /**
     * 代理的url
     */
    private String proxyUrl;

    private List<String> group;
}
