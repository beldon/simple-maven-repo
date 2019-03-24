package beldon.maven.service;

import java.io.File;
import java.io.IOException;

/**
 * @author beldon
 */
public interface DownloadService {
    void download(String url, File savePath) throws IOException;
}
