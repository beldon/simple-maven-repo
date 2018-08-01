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

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class RepoServiceImpl implements RepoService {

    private final Map<String, RepositoryData> repositoryDataMap = new HashMap<>();

    @Autowired
    private MavenProperties mavenProperties;

    @Autowired
    private DownloadService downloadService;

    private File localRepositoryDir;

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
        if (!localRepositoryDir.exists()) {
            localRepositoryDir.mkdirs();
        }
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
        File file = new File(localRepositoryDir, filePath);
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
}
