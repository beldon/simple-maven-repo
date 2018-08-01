package beldon.maven.web.interceptors;

import beldon.maven.bean.RequestResource;
import beldon.maven.config.properties.MavenProperties;
import beldon.maven.service.DownloadService;
import beldon.maven.service.ResourseParseService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StreamUtils;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.annotation.PostConstruct;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * @author Beldon
 * @create 2018-08-01 18:21
 */
@Slf4j
public class MavenInterceptor implements HandlerInterceptor {

    @Autowired
    private MavenProperties mavenProperties;

    @Autowired
    private DownloadService downloadService;

    @Autowired
    private ResourseParseService resourseParseService;

    @PostConstruct
    public void init() {
        System.out.println(mavenProperties);
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String requestURI = request.getRequestURI();
        if (!requestURI.startsWith(mavenProperties.getContextPath())) {
            return true;
        }
        requestURI = requestURI.substring(mavenProperties.getContextPath().length());
        String method = request.getMethod();
        if (method.toLowerCase().equals("get")) {
            //下载
            RequestResource requestResource = resourseParseService.parseUri(requestURI);
//            File targetFile =
        }
        return false;
    }

    private void downloadFile(HttpServletRequest request, HttpServletResponse response, File file) throws IOException {
        response.setContentType(request.getServletContext().getMimeType(file.getName()));
        response.setHeader("Content-Disposition", "attachment;filename=" + file.getName());
        try (
                ServletOutputStream outputStream = response.getOutputStream();
                FileInputStream fis = new FileInputStream(file)
        ) {
            StreamUtils.copy(fis, outputStream);
        }

    }


}
