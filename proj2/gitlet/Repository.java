package gitlet;

import java.io.File;

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
        Head.setHeadPointer(commitHash);
    }
}
