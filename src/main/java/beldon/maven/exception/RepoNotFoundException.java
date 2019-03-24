package beldon.maven.exception;

/**
 * @author beldon
 */
public class RepoNotFoundException extends Exception {
    public RepoNotFoundException(String msg) {
        super(msg);
    }
}
