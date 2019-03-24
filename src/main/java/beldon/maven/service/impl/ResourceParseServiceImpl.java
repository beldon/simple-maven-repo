package beldon.maven.service.impl;

import beldon.maven.bean.RequestResource;
import beldon.maven.service.ResourseParseService;
import org.springframework.stereotype.Service;

/**
 * @author beldon
 */
@Service
public class ResourceParseServiceImpl implements ResourseParseService {
    private static final String SUB_STRING = "/";

    @Override
    public RequestResource parseUri(String requestUri) {
        String path = requestUri;
        if (path.indexOf(SUB_STRING) == 0) {
            path = path.substring(1);
        }
        int i = path.indexOf(SUB_STRING);
        String repo = path.substring(0, i);
        String filePath = path.substring(i);
        RequestResource requestResource = new RequestResource();
        requestResource.setRepo(repo);
        requestResource.setFilePath(filePath);
        return requestResource;
    }

}
