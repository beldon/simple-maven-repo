package beldon.maven.service.impl;

import beldon.maven.service.DownloadService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.URL;
import java.net.URLConnection;

@Service
@Slf4j
public class DownloadServiceImpl implements DownloadService {
    @Override
    public void download(String url, File savePath) throws IOException {
        log.info("download file: {}", url);
        URL realUrl = new URL(url);
        URLConnection connection = realUrl.openConnection();
        connection.setConnectTimeout(10000);
        connection.connect();
        RandomAccessFile randomAccessFile = null;
        try (
                InputStream is = connection.getInputStream();
        ) {
            if (!savePath.getParentFile().exists()) {
                savePath.getParentFile().mkdirs();
            }
            randomAccessFile = new RandomAccessFile(savePath, "rw");
            int length;
            byte[] buffer = new byte[1024];
            while ((length = is.read(buffer)) != -1) {
                randomAccessFile.write(buffer, 0, length);
            }
        } finally {
            if (randomAccessFile != null) {
                randomAccessFile.close();
            }
        }
    }
}
