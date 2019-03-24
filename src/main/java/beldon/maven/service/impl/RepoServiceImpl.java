package beldon.maven.service.impl;

import beldon.maven.bean.RepositoryData;
import beldon.maven.config.properties.MavenProperties;
import beldon.maven.config.properties.RepoProperties;
import beldon.maven.exception.RepoFileNotFoundException;
import beldon.maven.exception.RepoNotFoundException;
import beldon.maven.service.DownloadService;
import beldon.maven.service.RepoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StreamUtils;

import javax.annotation.PostConstruct;
import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author beldon
 */
@Service
@Slf4j
public class RepoServiceImpl implements RepoService {

    private final Map<String, RepositoryData> repositoryDataMap = new HashMap<>();

    private final MavenProperties mavenProperties;
    private final DownloadService downloadService;
    private File localRepositoryDir;

    @Autowired
    public RepoServiceImpl(MavenProperties mavenProperties, DownloadService downloadService) {
        this.mavenProperties = mavenProperties;
        this.downloadService = downloadService;
    }

    @PostConstruct
    public void initData() {
        List<RepoProperties> repos = mavenProperties.getRepos();
        if (!CollectionUtils.isEmpty(repos)) {
            for (RepoProperties repo : repos) {
                RepositoryData repositoryData = new RepositoryData();
                repositoryData.setId(repo.getId());
                repositoryData.setName(repo.getName());
                repositoryData.setType(repo.getType());
                repositoryData.setPath(repo.getPath());
                repositoryData.setProxyUrl(repo.getProxyUrl());
                repositoryData.setGroup(repo.getGroup());
                repositoryDataMap.put(repo.getId(), repositoryData);
            }
        }
        localRepositoryDir = new File(mavenProperties.getLocalRepository());
        localRepositoryDir.mkdirs();
    }

    @Override
    public RepositoryData getRepo(String id) throws RepoNotFoundException {
        if (!repositoryDataMap.containsKey(id)) {
            throw new RepoNotFoundException(id + " not found");
        }
        return repositoryDataMap.get(id);
    }

    @Override
    public File getFile(String repoId, String filePath) throws RepoNotFoundException, RepoFileNotFoundException {
        RepositoryData repo = getRepo(repoId);
        File repoDir = new File(localRepositoryDir, repo.getId());
        File file = new File(repoDir, filePath);
        if (file.exists()) {
            return file;
        }

        if (repo.getType().equals("proxy")) {
            String url = repo.getProxyUrl() + filePath;
            try {
                downloadService.download(url, file);
                return file;
            } catch (IOException e) {
                log.error("download file error." + filePath);
            }
        }

        List<String> group = repo.getGroup();
        if (!CollectionUtils.isEmpty(group)) {
            for (String id : group) {
                try {
                    file = getFile(id, filePath);
                    break;
                } catch (RepoFileNotFoundException e) {
                    // ignore
                }
            }
        }
        if (file == null) {
            throw new RepoFileNotFoundException(filePath + " not found");
        }
        return file;
    }

    @Override
    public void saveFile(String repoId, String filePath, InputStream inputStream) throws RepoNotFoundException, IOException {
        RepositoryData repo = getRepo(repoId);
        File repoDir = new File(localRepositoryDir, repo.getId());
        File file = new File(repoDir, filePath);
        if (file.exists()) {
            file.delete();
        }
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
        try (
                FileOutputStream fos = new FileOutputStream(file)
        ) {
            StreamUtils.copy(inputStream, fos);
        }
    }
}
