package beldon.maven.service;

import beldon.maven.bean.RequestResource;

/**
 * @author beldon
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
