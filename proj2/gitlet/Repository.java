package gitlet;

import java.io.File;
import java.util.List;
import java.util.Set;

import static gitlet.Utils.*;


/**
 * Represents a gitlet repository.
 * Handles commands that interact with the working directory.
 *
 * @author Yutong Wang
 */
public class Repository {
    /*
      List all instance variables of the Repository class here with a useful
      comment above them describing what that variable represents and how that
      variable is used. We've provided two examples for you.
     */

    /** The current working directory. */
    public static final File CWD = new File(System.getProperty("user.dir"));
    /** The .gitlet directory. */
    public static final File GITLET_DIR = join(CWD, ".gitlet");

    /*
      Internal structure of Gitlet

      .gitlet
          |--objects  (for file contents)
          |--logs     (for commit records)
          |--branches (for the structure tree)
          |--HEAD     (for the current Head pointer)
          |--stage    (for staging area)
          |--remote   (for remote repository)
     */

    /**
     * Initialize a new Gitlet version-control system in the working directory.
     * Automatically create a master branch with an initial commit that contains no files.
     * If Gitlet already exists in the directory, exit without overwriting the current system.
     */
    public static void init() {
        // handle exception case
        if (GITLET_DIR.exists()) {
            exitWithError("A Gitlet version-control system already exists in the current directory.");
        }

        // make directories
        GITLET_DIR.mkdir();
        Blob.OBJECT_DIR.mkdir();
        Commit.COMMIT_DIR.mkdir();
        Branch.BRANCH_DIR.mkdir();

        // make file for empty staging area
        Stage initialStage = new Stage();
        initialStage.writeToStage();

        // create initial commit
        Commit firstCommit = new Commit();
        firstCommit.saveCommit();

        // create a master branch and HEAD which points to the initial commit
        Branch master = new Branch("master");
        master.addCommit(firstCommit.hashValue());
        master.saveBranch();
        Head.setHeadPointer(master.branchName());
    }

    /**
     * Create a new commit by saving a snapshot of tracked files in the recent commit and staging area.
     * A commit only updates the contents of files it is tracking that have been staged for addition;
     * otherwise, it keeps the versions of files in its parent commit.
     * If a file is staged for removal, it will be untracked in the new commit.
     * @param message the commit message described by the user.
     */
    public static void makeCommit(String message) {
        if (message.isEmpty()) {
            exitWithError("Please enter a commit message.");
        }

        // find the current branch and the latest commit
        String branchName = Head.getHeadState();
        Branch currentBranch = Branch.readCurrentBranch(branchName);
        Commit parentCommit = Branch.readRecentCommit(currentBranch);

        // create a new commit and save to file
        Commit currentCommit = new Commit(message, parentCommit);
        currentCommit.saveCommit();

        // update the current branch
        String commitHash = currentCommit.hashValue();
        currentBranch.addCommit(commitHash);
        currentBranch.saveBranch();

        // update the HEAD pointer
        Head.setHeadPointer(currentBranch.branchName());
    }

    /**
     * Starting at the current head commit, collect information about each commit backwards
     * along the commit tree until the initial commit, ignoring any second parents in merge commits.
     * This is similar to the `git log --first-parent` command.
     * @return a string representation of this branch's commit history.
     */
    public static String logHistory() {
        Branch currentBranch = Branch.readCurrentBranch(Head.getHeadState());
        List<String> commitHistory = currentBranch.getCommits();
        StringBuilder logs = new StringBuilder();
        for (String commitID : commitHistory) {
            Commit next = Commit.readCommit(commitID);
            logs.append("===\n");
            logs.append("commit ").append(commitID).append("\n");
            logs.append("Date: ").append(next.commitTime()).append("\n");
            logs.append(next.commitMessage()).append("\n").append("\n");
        }
        return logs.toString();
    }

    /**
     * The checkout command has three possible use cases:
     *   1. `java gitlet.Main checkout -- [file name]`
     *   2. `java gitlet.Main checkout [commit id] -- [file name]`
     *   3. `java gitlet.Main checkout [branch name]`
     * which are handled by separate functions.
     * @param args array of strings containing all command line arguments.
     */
    public static void checkout(String[] args) {
        if (args.length == 3 && args[1].equals("--")) {
            checkoutFromHead(args[2]);
        } else if (args.length == 4 && args[2].equals("--")) {
            checkoutFromCommit(args[1], args[3]);
        } else if (args.length == 2) {
            checkoutToBranch(args[1]);
        } else {
            exitWithError("Incorrect operands.");
        }
    }

    /**
     * Take the version of the file in the head commit and puts it in the working directory,
     * overwriting the version of the file that’s already there if there is one.
     * The new version of the file is not staged.
     * @param filename the name of the file to be checked out.
     */
    private static void checkoutFromHead(String filename) {
        Commit currCommit = Branch.readRecentCommit(Head.getHeadState());
        overwriteFile(filename, currCommit);
    }

    /**
     * Take the version of the file as it exists in the commit with the given id,
     * and puts it in the working directory, overwriting the current file there if there is one.
     * The new version of the file is not staged.
     * @param commitID the SHA-1 ID of a previous commit.
     * @param filename the name of the file to be checked out.
     */
    private static void checkoutFromCommit(String commitID, String filename) {
        Commit prevCommit = Commit.readCommit(commitID);
        overwriteFile(filename, prevCommit);
    }

    /**
     * Find the specified file under the current directory.
     * @param filename the name of the file.
     * @return a File object if it exists; otherwise, exit with error message.
     */
    public static File findFile(String filename) {
        File file = join(CWD, filename);
        if (!file.exists()) {
            exitWithError("File does not exist.");
        }
        return file;
    }

    /**
     * Overwrite the current file version with a previous commit.
     */
    private static void overwriteFile(String filename, Commit prevCommit) {
        File currVersion = join(CWD, filename);
        String blobID = prevCommit.commitMapping().get(filename);
        if (blobID == null) {
            exitWithError("File does not exist in that commit.");
        }
        byte[] saveVersion = Blob.readBlob(blobID);
        writeContents(currVersion, (Object) saveVersion);
    }

    /**
     * Overwrite all files from a previous commit.
     */
    private static void overwriteAllFiles(Commit prevCommit) {
        Set<String> savedFiles = prevCommit.commitMapping().keySet();
        for (String file : savedFiles) {
            overwriteFile(file, prevCommit);
        }
    }

    /**
     * Creates a new branch with the given name, and points it at the current commit.
     * It does NOT immediately switch to the newly created branch.
     * If a branch with the given name already exists, exit with an error message.
     * @param branchName the name of the new branch to be created.
     */
    public static void createBranch(String branchName) {
        validateNewBranch(branchName);
        Branch diverged = new Branch(branchName);
        Commit currentCommit = Branch.readRecentCommit(Head.getHeadState());
        diverged.addCommit(currentCommit.hashValue());
        diverged.saveBranch();
    }

    /**
     * Take all files in the HEAD commit of the given branch, and put them in the working directory.
     * If a file exists in the working directory, it will be overwritten by the saved version.
     * If a file is tracked in the current branch but not in the checked-out branch, it will be deleted.
     * Overwrite the versions of the files that are already there if they exist.
     * The given branch will be considered the current branch (HEAD).
     * @param branchName the name of branch to be checked out.
     */
    public static void checkoutToBranch(String branchName) {
        validateBranchExists(branchName);
        validateCurrentBranch(branchName);
        checkUntrackedFiles();

        Commit commitInBranch = Branch.readRecentCommit(branchName);
        overwriteAllFiles(commitInBranch);
        deleteTrackedFiles(commitInBranch);

        Head.setHeadPointer(branchName);
    }

    /**
     * Check if a branch name already exists before creating a new one.
     * @param branchName the given name of the new branch.
     */
    private static void validateNewBranch(String branchName) {
        List<String> allBranches = plainFilenamesIn(Branch.BRANCH_DIR);
        if (allBranches != null && allBranches.contains(branchName)) {
            exitWithError("A branch with that name already exists.");
        }
    }

    /**
     * Check if the given branch name has been created before.
     */
    private static void validateBranchExists(String branchName) {
        List<String> allBranches = plainFilenamesIn(Branch.BRANCH_DIR);
        if (allBranches == null || !allBranches.contains(branchName)) {
            exitWithError("No such branch exists.");
        }
    }

    /**
     * Check if the given branch name is the current HEAD branch.
     */
    private static void validateCurrentBranch(String branchName) {
        String currBranch = Head.getHeadState();
        if (currBranch.equals(branchName)) {
            exitWithError("No need to checkout the current branch.");
        }
    }

    /**
     * Check if untracked file exists. If a working file version is not being tracked by
     * the most recent commit in the current branch, exit with an error message.
     */
    private static void checkUntrackedFiles() {
        Commit currCommit = Branch.readRecentCommit(Head.getHeadState());
        Set<String> savedFiles = currCommit.commitMapping().keySet();
        List<String> workingFiles = plainFilenamesIn(CWD);
        if (workingFiles == null) {
            return;
        }
        for (String filename : workingFiles) {
            if (!savedFiles.contains(filename)) {
                exitWithError("There is an untracked file in the way; " +
                        "delete it, or add and commit it first.");
            }
        }
    }

    /**
     * Delete files that are not present in the given commit.
     */
    private static void deleteTrackedFiles(Commit prevCommit) {
        Set<String> savedFiles = prevCommit.commitMapping().keySet();
        List<String> workingFiles = plainFilenamesIn(CWD);
        if (workingFiles == null) {
            return;
        }
        for (String filename : workingFiles) {
            if (!savedFiles.contains(filename)) {
                restrictedDelete(filename);
            }
        }
    }
}
