package gitlet;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import static gitlet.Repository.GITLET_DIR;
import static gitlet.Utils.*;

/**
 * A branch maintains a sequence of commits.
 */
public class Branch implements Serializable {
    /** The branches folder under the .gitlet directory. */
    public static final File BRANCH_DIR = join(GITLET_DIR, "branches");

    private final String name;
    private final List<String> commits;

    /**
     * Set up the current branch by reading a saved file.
     * @param name the name of the branch.
     */
    public Branch(String name) {
        this.name = changeName(name);
        this.commits = new LinkedList<>();
        if (join(BRANCH_DIR, this.name).exists()) {
            Branch currBranch = readCurrentBranch(this.name);
            this.commits.addAll(currBranch.getCommits());
        }
    }

    /**
     * @return the name of the branch.
     */
    public String branchName() {
        return name;
    }

    /**
     * Replace `/` in any branch name. Only necessary because of the specification.
     */
    private static String changeName(String name) {
        return name.replace("/", "__");
    }

    /**
     * @return an unmodifiable list of commits under the current branch.
     */
    public List<String> getCommits() {
        return Collections.unmodifiableList(commits);
    }

    /**
     * @return the most recent commit ID of the current branch.
     */
    public String getRecentCommit() {
        return getCommits().get(0);
    }

    /**
     * Add a commit to the current branch.
     * The most recent commit is always at the front of the sequence.
     * @param recentCommit a commit represented by its SHA-1 value.
     */
    public void addCommit(String recentCommit) {
        commits.add(0, recentCommit);
    }

    /**
     * Add all previous commits in the parent branch to this one.
     * Used when creating a new branch.
     * @param prevCommits a list of commit hash IDs.
     */
    public void addPrevCommits(List<String> prevCommits) {
        commits.addAll(prevCommits);
    }

    /**
     * Read the most recent commit from saved branch files.
     * @param currBranch the current working branch.
     */
    public static Commit readRecentCommit(Branch currBranch) {
        String currCommit = currBranch.getRecentCommit();
        return Commit.readCommit(currCommit);
    }

    /**
     * Read the most recent commit from saved branch files.
     * @param branchName the name of the current working branch.
     */
    public static Commit readRecentCommit(String branchName) {
        Branch currBranch = readCurrentBranch(changeName(branchName));
        return readRecentCommit(currBranch);
    }

    /**
     * Read the current branch from a saved file.
     */
    public static Branch readCurrentBranch(String branchName) {
        File branchFile = join(BRANCH_DIR, changeName(branchName));
        return readObject(branchFile, Branch.class);
    }

    /**
     * Save the current branch information to a file with its NAME as filename.
     */
    protected void saveBranch() {
        File branchFile = join(BRANCH_DIR, this.name);
        writeObject(branchFile, this);
    }

    /**
     * @return a list of all active branches and null pointer safe.
     */
    public static List<String> getAllBranches() {
        List<String> allBranches = plainFilenamesIn(Branch.BRANCH_DIR);
        return allBranches == null ? new ArrayList<>() : Collections.unmodifiableList(allBranches);
    }

    /**
     * @return true if the given branch name already exists, false otherwise.
     */
    public static boolean checkBranchExists(String branchName) {
        return getAllBranches().contains(changeName(branchName));
    }

    /**
     * @return the file representing the branch folder in the remote server reachable by path.
     */
    public static File remoteDirectory(String path) {
        return join(path, "branches");
    }

    /**
     * @return the specified branch in the remote machine.
     */
    public static Branch remoteCurrentBranch(String path, String branchName) {
        File remoteBranch = join(remoteDirectory(path), changeName(branchName));
        return readObject(remoteBranch, Branch.class);
    }

    /**
     * Save the given branch information to a file with its NAME as filename.
     */
    public static void saveRemoteBranch(String path, Branch local) {
        File branchFile = join(remoteDirectory(path), local.branchName());
        writeObject(branchFile, local);
    }

    /**
     * @return a list of branch names that are active in the remote server.
     */
    public static List<String> allRemoteBranches(String path) {
        List<String> allBranches = plainFilenamesIn(remoteDirectory(path));
        return allBranches == null ? new ArrayList<>() : Collections.unmodifiableList(allBranches);
    }
}
