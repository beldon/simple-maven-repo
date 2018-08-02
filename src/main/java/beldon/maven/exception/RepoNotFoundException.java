package beldon.maven.exception;

public class RepoNotFoundException extends Exception {
    public RepoNotFoundException(String msg) {
        super(msg);
    }
}
