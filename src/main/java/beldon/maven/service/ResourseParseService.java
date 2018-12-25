package beldon.maven.service;

import beldon.maven.bean.RequestResource;

/**
 * @author Beldon
 */
public interface ResourseParseService {
    /**
     * 解析uri
     *
     * @param requestUri
     * @return
     */
    RequestResource parseUri(String requestUri);
}
