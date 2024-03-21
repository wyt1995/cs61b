package gitlet;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static gitlet.Repository.GITLET_DIR;
import static gitlet.Repository.merge;
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
        checkDirectory(directory);
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
     * Append the current branch’s commits to the end of the given branch at the given remote.
     */
    public static void push(String remoteName, String remoteBranch) {
        validateRemoteExists(remoteName);
        String path = remoteGitletPath(remoteName);
        checkRemoteGitInit(path);

        // If the Gitlet system on the remote machine exists but does not have the input branch,
        // then simply add the branch to the remote Gitlet.
        Branch localCurrBranch = Branch.readCurrentBranch(Head.getHeadState());
        if (!Branch.allRemoteBranches(path).contains(remoteBranch)) {
            Branch.saveRemoteBranch(path, localCurrBranch);
            return;
        }

        Branch remoteCurrBranch = Branch.remoteCurrentBranch(path, remoteBranch);
        String remoteCurrCommit = remoteCurrBranch.getRecentCommit();
        List<String> localCommitHistory = localCurrBranch.getCommits();
        int index = localCommitHistory.indexOf(remoteCurrCommit);
        if (index == -1) {
            exitWithError("Please pull down remote changes before pushing.");
        }

        for (int i = index - 1; i >= 0; i -= 1) {
            Commit next = Commit.readCommit(localCommitHistory.get(i));
            copyCommit(next, path);
            remoteCurrBranch.addCommit(next.hashValue());
        }
        Branch.saveRemoteBranch(path, remoteCurrBranch);
    }

    /**
     * Copy one commit from local directory to remote server.
     * @param commit the local commit to be copied.
     * @param path the path to remote gitlet.
     */
    private static void copyCommit(Commit commit, String path) {
        File remoteCommitDir = join(path, "logs");
        File remoteBlobDir = join(path, "objects");

        Map<String, String> commitMap = commit.commitMapping();
        for (String file : commitMap.keySet()) {
            String blobID = commitMap.get(file);
            File remoteBlob = join(remoteBlobDir, blobID);
            if (!remoteBlob.exists()) {
                byte[] content = Blob.readBlob(blobID);
                writeContents(remoteBlob, (Object) content);
            }
        }
        writeObject(join(remoteCommitDir, commit.hashValue()), commit);
    }

    /**
     * Brings down commits from the remote Gitlet repository into the local Gitlet repository.
     */
    public static void fetch(String remoteName, String remoteBranchName) {
        validateRemoteExists(remoteName);
        String path = remoteGitletPath(remoteName);
        checkRemoteGitInit(path);
        checkRemoteBranch(path, remoteBranchName);

        File remoteBranchFile = join(Branch.remoteDirectory(path), remoteBranchName);
        Branch remoteBranch = readObject(remoteBranchFile, Branch.class);
        List<String> remoteCommits = remoteBranch.getCommits();

        String fetchBranchName = remoteName + "/" + remoteBranchName;
        Branch localNewBranch;
        if (!Branch.checkBranchExists(fetchBranchName)) {
            localNewBranch = new Branch(fetchBranchName);
        } else {
            localNewBranch = Branch.readCurrentBranch(fetchBranchName);
        }

        List<String> localCommits = Commit.readAllCommits();
        for (String commit : remoteCommits) {
            if (!localCommits.contains(commit)) {
                copyFromCommit(commit, path);
                localNewBranch.addCommit(commit);
            }
        }
        localNewBranch.saveBranch();
    }

    /**
     * Copy one commit from remote machine to local gitlet directory.
     * @param commitID the hash ID of the commit to be copied.
     * @param path the path to the remote gitlet.
     */
    private static void copyFromCommit(String commitID, String path) {
        Commit remoteCommit = Commit.readRemoteCommit(path, commitID);
        Map<String, String> remoteFileMap = remoteCommit.commitMapping();
        for (String file : remoteFileMap.keySet()) {
            String blobID = remoteFileMap.get(file);
            if (!Blob.checkBlobExists(blobID)) {
                File remoteBlob = Blob.findRemoteBlob(path, blobID);
                Blob copied = new Blob(remoteBlob);
                copied.saveBlob();
            }
        }
        remoteCommit.saveCommit();
    }

    /**
     * Fetch branch [remote name]/[remote branch name] as for the fetch command,
     * and then merge that fetch into the current branch.
     */
    public static void pull(String remoteName, String remoteBranchName) {
        fetch(remoteName, remoteBranchName);
        merge(remoteName + "/" + remoteBranchName);
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
    private static void checkRemoteName(String name) {
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

    /**
     * A valid path must have a /.gitlet directory.
     */
    private static void checkDirectory(String path) {
        if (!path.endsWith("/.gitlet")) {
            exitWithError("Invalid Gitlet directory.");
        }
    }

    /**
     * Get the path to the remote gitlet server.
     */
    private static String remoteGitletPath(String remoteName) {
        File remoteFile = join(REMOTE_DIR, remoteName);
        return remoteFile.exists() ? readContentsAsString(remoteFile) : "";
    }

    /**
     * Check if the given path is a gitlet directory.
     */
    private static void checkRemoteGitInit(String path) {
        if (!join(path).exists()) {
            exitWithError("Remote directory not found.");
        }
    }

    private static void checkRemoteBranch(String path, String branchName) {
        List<String> remoteBranches = Branch.allRemoteBranches(path);
        if (!remoteBranches.contains(branchName)) {
            exitWithError("That remote does not have that branch.");
        }
    }
}
