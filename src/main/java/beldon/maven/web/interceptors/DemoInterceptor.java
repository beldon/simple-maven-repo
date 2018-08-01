package beldon.maven.web.interceptors;

import beldon.maven.config.properties.MavenProperties;
import beldon.maven.service.DownloadService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StreamUtils;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * @author Beldon
 * @create 2018-08-01 18:21
 */
@Slf4j
public class DemoInterceptor implements HandlerInterceptor {

    @Autowired
    private MavenProperties mavenProperties;

    private File root;

    @Autowired
    private DownloadService downloadService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String requestURI = request.getRequestURI();
        if (!requestURI.startsWith(mavenProperties.getContextPath())) {
            return true;
        }

        String filePath = requestURI.substring(mavenProperties.getContextPath().length());
        log.info("get file [{}]", filePath);
        File file = new File(getRoot(), filePath);
        if (!file.exists()) {
            String url = mavenProperties.getProxy() + filePath;
            try {
                downloadService.download(url, file);
            } catch (IOException e) {
                log.error("download file error", e);
                return false;
            }
        }
        try {
            downloadFile(request, response, file);
        } catch (IOException e) {
            log.error("download file error", e);
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

    private File getRoot() {
        if (root == null) {
            root = new File("./maven");
        }
        return root;
    }

}
