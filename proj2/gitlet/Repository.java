package gitlet;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

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
            exitWithError("A Gitlet version-control system already exists "
                    + "in the current directory.");
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
     * Create a new commit by saving a snapshot of tracked files in recent commit and staging area.
     * A commit only updates the content of files it is tracking that have been staged for addition;
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
            logs.append(commitLog(next));
        }
        return logs.toString();
    }

    /**
     * @return a string representation of all commits that have been made in the order of time.
     */
    public static String globalLog() {
        List<Commit> allCommits = Commit.readAllCommits()
                                        .stream()
                                        .map(Commit::readCommit)
                                        .sorted(Comparator.comparing(Commit::timestamp).reversed())
                                        .collect(Collectors.toList());
        StringBuilder logs = new StringBuilder();
        for (Commit next : allCommits) {
            logs.append(commitLog(next));
        }
        return logs.toString();
    }

    /**
     * Build a formatted commit information.
     * @param next the commit to be read.
     * @return a StringBuilder.
     */
    private static StringBuilder commitLog(Commit next) {
        StringBuilder log = new StringBuilder();
        log.append("===\n");
        log.append("commit ").append(next.hashValue()).append("\n");
        log.append("Date: ").append(next.commitTime()).append("\n");
        log.append(next.commitMessage()).append("\n").append("\n");
        return log;
    }

    /**
     * @return a string displaying the current status, including:
     *   - branches that currently exist, marking the head branch with a *
     *   - staged files: staged for addition
     *   - Removed files: staged for removal
     *   - Modified files that are not staged for commit
     *   - Untracked files
     */
    public static String status() {
        StringBuilder status = new StringBuilder();

        List<String> allBranches = Branch.getAllBranches();
        String headBranch = Head.getHeadState();
        status.append("=== Branches ===\n");
        for (String branch : allBranches) {
            if (branch.equals(headBranch)) {
                status.append("*");
            }
            status.append(branch).append("\n");
        }
        status.append("\n");

        Stage stagingArea = new Stage();
        Map<String, String> stagedMap = stagingArea.stageMap();
        Set<String> stagedFiles = stagedMap.keySet();
        List<String> sortedStage = new ArrayList<>(stagedFiles);
        Collections.sort(sortedStage);
        status.append("=== Staged Files ===\n");
        for (String file : sortedStage) {
            status.append(file).append("\n");
        }
        status.append("\n");

        Set<String> removedFiles = stagingArea.removeFiles();
        List<String> sortedRemoval = new ArrayList<>(removedFiles);
        Collections.sort(sortedRemoval);
        status.append("=== Removed Files ===\n");
        for (String file : sortedRemoval) {
            status.append(file).append("\n");
        }
        status.append("\n");

        Set<String> trackedFiles = new HashSet<>();
        for (String branch : allBranches) {
            Commit recentCommit = Branch.readRecentCommit(branch);
            trackedFiles.addAll(recentCommit.commitMapping().keySet());
        }

        Commit currCommit = Branch.readRecentCommit(Head.getHeadState());
        status.append("=== Modifications Not Staged For Commit ===\n");
        List<String> modified = modifiedFiles(stagedMap, removedFiles, currCommit.commitMapping());
        for (String filename : modified) {
            status.append(filename).append("\n");
        }
        status.append("\n");

        List<String> untrackedFiles = getUntrackedFiles(stagedFiles, removedFiles, trackedFiles);
        status.append("=== Untracked Files ===\n");
        for (String filename : untrackedFiles) {
            status.append(filename).append("\n");
        }

        return status.toString();
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
        overwriteFromFile(filename, currCommit);
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
        overwriteFromFile(filename, prevCommit);
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
     * Find the specified file under the current directory. Create the file if it does not exist.
     */
    public static File createFile(String filename) {
        return join(CWD, filename);
    }

    /**
     * @return all plain files in the working directory but null pointer safe.
     */
    public static List<String> allWorkingFiles() {
        List<String> workingFiles = plainFilenamesIn(CWD);
        return (workingFiles == null) ? new ArrayList<>() : workingFiles;
    }

    /**
     * Create or overwrite file based on the version from a previous commit.
     */
    private static void overwriteFromFile(String filename, Commit prevCommit) {
        String blobID = prevCommit.commitMapping().get(filename);
        if (blobID == null) {
            exitWithError("File does not exist in that commit.");
        }
        byte[] savedVersion = Blob.readBlob(blobID);
        writeContents(createFile(filename), (Object) savedVersion);
    }

    /**
     * Overwrite all files from a previous commit.
     */
    private static void overwriteAllFiles(Commit prevCommit) {
        Set<String> savedFiles = prevCommit.commitMapping().keySet();
        for (String file : savedFiles) {
            overwriteFromFile(file, prevCommit);
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
     * If a file is tracked in the head branch but not in the check-out branch, it will be deleted.
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
        List<String> allBranches = Branch.getAllBranches();
        if (allBranches.contains(branchName)) {
            exitWithError("A branch with that name already exists.");
        }
    }

    /**
     * Check if the given branch name has been created before.
     */
    private static void validateBranchExists(String branchName) {
        List<String> allBranches = Branch.getAllBranches();
        if (!allBranches.contains(branchName)) {
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
     * A file in the working directory is “modified but not staged” if it is
     *   - tracked in the current commit, changed in the working directory, but not staged; or
     *   - staged for addition, but with different contents than in the working directory; or
     *   - staged for addition, but deleted in the working directory; or
     *   - not staged for removal, but tracked in the current commit and deleted.
     * @return a list of file names that satisfied these conditions.
     */
    private static List<String> modifiedFiles(Map<String, String> added, Set<String> removed,
                                              Map<String, String> tracked) {
        List<String> modified = new ArrayList<>();
        List<String> workingFiles = allWorkingFiles();
        for (String filename : workingFiles) {
            String currVersion = new Blob(findFile(filename)).blobHashValue();
            if (tracked.containsKey(filename) && !currVersion.equals(tracked.get(filename))) {
                modified.add(filename + " (modified)");
            } else if (added.containsKey(filename) && !currVersion.equals(added.get(filename))) {
                modified.add(filename + " (modified)");
            }
        }
        for (String addFile : added.keySet()) {
            if (!workingFiles.contains(addFile)) {
                modified.add(addFile + " (deleted)");
            }
        }
        for (String trackFile : tracked.keySet()) {
            if (!workingFiles.contains(trackFile) && !removed.contains(trackFile)) {
                modified.add(trackFile + " (deleted)");
            }
        }
        Collections.sort(modified);
        return modified;
    }

    /**
     * @return a list of file names that is in the working directory, but not stage for addition,
     * or tracked in the current commit.
     * This includes files that have been staged for removal, but then re-created without staging.
     */
    private static List<String> getUntrackedFiles(Set<String> added, Set<String> removed,
                                                  Set<String> tracked) {
        List<String> workingFiles = allWorkingFiles();
        List<String> untracked = new ArrayList<>();
        for (String fileName : workingFiles) {
            if (!added.contains(fileName) && !removed.contains(fileName)
                    && !tracked.contains(fileName)) {
                untracked.add(fileName);
            }
        }
        return untracked;
    }

    /**
     * Check if untracked file exists. If a working file version is not being tracked by
     * the most recent commit in the current branch, exit with an error message.
     */
    private static void checkUntrackedFiles() {
        // staging area should be clear
        Stage stagingArea = new Stage();
        Map<String, String> stagedMap = stagingArea.stageMap();
        Set<String> stagedFiles = stagedMap.keySet();
        Set<String> removedFiles = stagingArea.removeFiles();

        // no untracked modifications or files; reuse code from the `status` command
        Commit currCommit = Branch.readRecentCommit(Head.getHeadState());
        Map<String, String> commitMap = currCommit.commitMapping();
        List<String> modified = modifiedFiles(stagedMap, removedFiles, commitMap);
        List<String> untracked = getUntrackedFiles(stagedFiles, removedFiles, commitMap.keySet());

        if (!stagedFiles.isEmpty() || !removedFiles.isEmpty()
            || !modified.isEmpty() || !untracked.isEmpty()) {
            exitWithError("There is an untracked file in the way; "
                    + "delete it, or add and commit it first.");
        }
    }

    /**
     * Delete files that are not present in the given commit.
     */
    private static void deleteTrackedFiles(Commit prevCommit) {
        Set<String> savedFiles = prevCommit.commitMapping().keySet();
        List<String> workingFiles = allWorkingFiles();
        for (String filename : workingFiles) {
            if (!savedFiles.contains(filename)) {
                restrictedDelete(filename);
            }
        }
    }
}
