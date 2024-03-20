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
        this.name = name;
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
        commits.remove(recentCommit);
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
        Branch currBranch = readCurrentBranch(branchName);
        return readRecentCommit(currBranch);
    }

    /**
     * Read the current branch from a saved file.
     */
    public static Branch readCurrentBranch(String branchName) {
        File branchFile = join(BRANCH_DIR, branchName);
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
}
