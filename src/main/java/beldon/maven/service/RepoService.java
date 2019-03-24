package beldon.maven.service;

import beldon.maven.bean.RepositoryData;
import beldon.maven.exception.RepoFileNotFoundException;
import beldon.maven.exception.RepoNotFoundException;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author beldon
 */
public interface RepoService {

    /**
     * 根据仓库id获取仓库
     *
     * @param id
     * @return
     */
    RepositoryData getRepo(String id) throws RepoNotFoundException;

    /**
     * 获取文件
     *
     * @param repoId   仓库id
     * @param filePath 文件路径
     * @return
     */
    File getFile(String repoId, String filePath) throws RepoNotFoundException, RepoFileNotFoundException;


    /**
     * 保存文件
     *
     * @param repoId
     * @param filePath
     * @param inputStream
     */
    void saveFile(String repoId, String filePath, InputStream inputStream) throws RepoNotFoundException, IOException;
}
