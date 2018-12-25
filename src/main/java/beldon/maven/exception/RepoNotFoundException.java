package beldon.maven.exception;

/**
 * @author Beldon
 */
public class RepoNotFoundException extends Exception {
    public RepoNotFoundException(String msg) {
        super(msg);
    }
}
