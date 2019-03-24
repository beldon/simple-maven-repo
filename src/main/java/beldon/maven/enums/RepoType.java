package beldon.maven.enums;

import lombok.Getter;

/**
 * @author Beldon
 */
public enum RepoType {
    /**
     * 分类
     */
    GROUP("group"),
    HOSTED("hosted"),
    PROXY("proxy"),
    ;

    @Getter
    private final String value;

    RepoType(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }
}
