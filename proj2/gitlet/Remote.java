package gitlet;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static gitlet.Repository.GITLET_DIR;
import static gitlet.Utils.*;

/**
 * Mimic git's basic remote features.
 */
public class Remote {
    /** The remote folder under the .gitlet directory. */
    public static final File REMOTE_DIR = join(GITLET_DIR, "remote");

    /**
     * Add a remote server with name in the given directory.
     * @param remoteName the name of the remote server.
     * @param directory the path to the remote directory separated by forward slashes.
     */
    public static void addRemote(String remoteName, String directory) {
        checkRemoteName(remoteName);
        String remotePath = directory.replace("/", File.separator);
        File remoteFile = join(REMOTE_DIR, remoteName);
        writeContents(remoteFile, remotePath);
    }

    /**
     * Remove information associated with the given remote name.
     */
    public static void removeRemote(String remoteName) {
        validateRemoteExists(remoteName);
        join(REMOTE_DIR, remoteName).delete();
    }

    /**
     * @return a list of all remote names and null pointer safe.
     */
    public static List<String> getAllRemotes() {
        List<String> remotes = plainFilenamesIn(REMOTE_DIR);
        return remotes == null ? new ArrayList<>() : Collections.unmodifiableList(remotes);
    }

    /**
     * Check if a remote name already exists before creating a new one.
     * @param name the given name of the new remote.
     */
    public static void checkRemoteName(String name) {
        List<String> allRemotes = getAllRemotes();
        if (allRemotes.contains(name)) {
            exitWithError("A remote with that name already exists.");
        }
    }

    /**
     * Check if the given remote name has been created before.
     */
    private static void validateRemoteExists(String remoteName) {
        List<String> remotes = getAllRemotes();
        if (!remotes.contains(remoteName)) {
            exitWithError("A remote with that name does not exist.");
        }
    }
}
