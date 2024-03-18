package gitlet;

import java.io.File;
import java.util.List;

import static gitlet.Utils.*;


/** Represents a gitlet repository.
 *  It's a good idea to give a description here of what else this Class
 *  does at a high level.
 *
 *  @author Yutong Wang
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
        }

        else if (args.length == 4 && args[2].equals("--")) {
            checkoutFromCommit(args[1], args[3]);
        }

        else if (args.length == 2) {
            return;
        }

        else {
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
}
