package beldon.maven.web.interceptors;

import beldon.maven.bean.RequestResource;
import beldon.maven.config.properties.MavenProperties;
import beldon.maven.exception.RepoFileNotFoundException;
import beldon.maven.exception.RepoNotFoundException;
import beldon.maven.service.DownloadService;
import beldon.maven.service.RepoService;
import beldon.maven.service.ResourseParseService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StreamUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Base64;

import static javax.servlet.http.HttpServletResponse.SC_NOT_FOUND;
import static javax.servlet.http.HttpServletResponse.SC_OK;
import static javax.servlet.http.HttpServletResponse.SC_UNAUTHORIZED;

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

    @Autowired
    private RepoService repoService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String requestURI = request.getRequestURI();
        requestURI = requestURI.substring(mavenProperties.getContextPath().length());
        String method = request.getMethod();
        RequestResource requestResource = resourseParseService.parseUri(requestURI);
        if (method.toLowerCase().equals("get")) {
            log.info("get file [{}]", requestResource.getFilePath());
            //下载
            try {
                File targetFile = repoService.getFile(requestResource.getRepo(), requestResource.getFilePath());
                downloadFile(request, response, targetFile);
            } catch (RepoNotFoundException e) {
                log.info("[{}] repo not found", e.getMessage());
                response404(response);
            } catch (RepoFileNotFoundException e) {
                log.info("[{}] file not found", e.getMessage());
                response404(response);
            } catch (IOException e) {
                log.error("get file error", e);
                response404(response);
            }
        } else if (method.toLowerCase().equals("put")) {
            //upload
            if (!isAuth(request.getHeader("authorization"))) {
                response.setStatus(SC_UNAUTHORIZED);
                log.info("unauthorized");
                return false;
            }
            log.info("upload file [{}]", requestResource.getFilePath());
            repoService.saveFile(requestResource.getRepo(), requestResource.getFilePath(), request.getInputStream());
            response.setStatus(SC_OK);
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

    private void response404(HttpServletResponse response) throws IOException {
        response.setStatus(SC_NOT_FOUND);
        try (
                PrintWriter printWriter = response.getWriter();
        ) {
            printWriter.write("not found");
        }
    }

    private boolean isAuth(String authorization) {
        if (StringUtils.isEmpty(authorization)) {
            return false;
        }
        authorization = authorization.substring(6).trim();
        String[] auth = new String(Base64.getDecoder().decode(authorization)).split(":");
        String user = auth[0];
        String pwd = auth[1];
        if (mavenProperties.getUser().equals(user) && mavenProperties.getPass().equals(pwd)) {
            return true;
        }
        return false;
    }

}
