package beldon.maven.service.impl;

import beldon.maven.bean.RequestResource;
import beldon.maven.service.ResourseParseService;
import org.springframework.stereotype.Service;

/**
 * @author Beldon
 */
@Service
public class ResourceParseServiceImpl implements ResourseParseService {
    @Override
    public RequestResource parseUri(String requestUri) {
        String path = requestUri;
        if (path.contains("/")) {
            path = path.substring(1);
        }
        int i = path.indexOf("/");
        String repo = path.substring(0, i);
        String filePath = path.substring(i);
        RequestResource requestResource = new RequestResource();
        requestResource.setRepo(repo);
        requestResource.setFilePath(filePath);
        return requestResource;
    }

}
