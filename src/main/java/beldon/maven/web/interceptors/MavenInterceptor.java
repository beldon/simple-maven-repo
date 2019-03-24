package beldon.maven.web.interceptors;

import beldon.maven.bean.RequestResource;
import beldon.maven.config.properties.MavenProperties;
import beldon.maven.exception.RepoFileNotFoundException;
import beldon.maven.exception.RepoNotFoundException;
import beldon.maven.service.RepoService;
import beldon.maven.service.ResourseParseService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
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

import static javax.servlet.http.HttpServletResponse.*;

/**
 * @author Beldon
 */
@Slf4j
public class MavenInterceptor implements HandlerInterceptor {

    @Autowired
    private MavenProperties mavenProperties;

    @Autowired
    private ResourseParseService resourseParseService;

    @Autowired
    private RepoService repoService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String requestURI = request.getRequestURI();
        requestURI = requestURI.substring(mavenProperties.getContextPath().length());
        RequestResource requestResource = resourseParseService.parseUri(requestURI);
        if (isFetchFile(request)) {
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
                log.warn("get file error,{}", e.getMessage());
                response404(response);
            }
        } else if (isPutFile(request)) {
            //upload
            if (!isAuth(request)) {
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
                FileInputStream fis = new FileInputStream(file);
                ServletOutputStream outputStream = response.getOutputStream()
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

    private boolean isAuth(HttpServletRequest request) {
        String authorization = request.getHeader("authorization");
        if (StringUtils.isEmpty(authorization)) {
            return false;
        }
        authorization = authorization.substring(6).trim();
        String[] auth = new String(Base64.getDecoder().decode(authorization)).split(":");
        String user = auth[0];
        String pwd = auth[1];
        return mavenProperties.getUser().equals(user) && mavenProperties.getPass().equals(pwd);
    }

    private boolean isFetchFile(HttpServletRequest request) {
        return request.getMethod().equalsIgnoreCase(HttpMethod.GET.toString());
    }

    private boolean isPutFile(HttpServletRequest request) {
        return request.getMethod().equalsIgnoreCase(HttpMethod.PUT.toString());
    }

}
