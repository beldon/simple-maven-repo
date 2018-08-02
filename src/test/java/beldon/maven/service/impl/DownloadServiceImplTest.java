package beldon.maven.service.impl;

import beldon.maven.service.DownloadService;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.*;

public class DownloadServiceImplTest {

    //    private String url = "http://maven.aliyun.com/nexus/content/groups/public/com/alibaba/fastjson/1.2.47/fastjson-1.2.47.pom";
    private String url = "http://repo2.maven.org/maven2/com/alibaba/fastjson/1.2.47/fastjson-1.2.47.pom2";

    @Test
    public void download() throws Exception {
        File path = new File("./maven/fastjson-1.2.47.pom");
        if (!path.getParentFile().exists()) {
            path.getParentFile().mkdirs();
        }
        DownloadService downloadService = new DownloadServiceImpl();
        downloadService.download(url, path);
    }

    public static void main(String[] args) {

    }
}