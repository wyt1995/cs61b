package gitlet;

import java.io.File;
import java.io.Serializable;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static gitlet.Repository.GITLET_DIR;
import static gitlet.Utils.*;

/**
 * Represents a Gitlet commit object.
 * A commit is a combination of timestamp, log message, mapping of file names to blob objects,
 * and references to (one or two) parent commits. Gitlet limits merge operation to two parents.
 */
public class Commit implements Serializable {
    /** The logs directory for commits information. */
    public static final File COMMIT_DIR = join(GITLET_DIR, "logs");
    /** The automatic message of the initial commit. */
    public static final String INIT_COMMIT_MSG = "initial commit";

    /** The date and time of this Commit. */
    private final Date timestamp;
    /** The log message of this Commit. */
    private final String message;
    /** The SHA-1 value of this Commit. */
    private final String hashValue;
    /** The parent (previous) commit of this one, represented by its SHA-1 value. */
    private final String parent;
    /** The second parent commit reference for merges. */
    private final String secondParent;
    /** Map from file names to the hash value of each blob. */
    private final Map<String, String> fileMapping;


    /**
     * Constructor for the first commit when `gitlet init` is called.
     * All repositories automatically share this same commit.
     */
    public Commit() {
        this.timestamp = new Date(0);  // 00:00:00 UTC, Thursday, January 1st, 1970
        this.message = INIT_COMMIT_MSG;
        this.parent = this.secondParent = "";  // empty string represents no such parent
        this.fileMapping = new HashMap<>();
        this.hashValue = generateHashValue();
    }

    /**
     * Constructor for the commit command with a single parent reference.
     */
    public Commit(String message, Commit parent) {
        this(message, parent, null);
    }

    /**
     * Constructor for merging two branches where a commit can have two parents.
     * @param message the commit message as specified in the command line argument.
     * @param parent the reference to the parent commit.
     *               All commit must have a parent except for the initial commit.
     * @param secondParent the reference to the second parent for merges.
     */
    public Commit(String message, Commit parent, Commit secondParent) {
        this.timestamp = new Date();
        this.message = message;
        this.parent = parent.hashValue();
        this.secondParent = (secondParent == null) ? "" : secondParent.hashValue();
        this.fileMapping = new HashMap<>(parent.commitMapping());
        this.readStagingArea();
        this.hashValue = generateHashValue();
    }

    /**
     * @return the datetime of this commit.
     */
    public Date timestamp() {
        return this.timestamp;
    }

    /**
     * @return a formatted timestamp of this commit.
     */
    public String commitTime() {
        return dateFormat(this.timestamp);
    }

    /**
     * @return the commit message.
     */
    public String commitMessage() {
        return this.message;
    }

    /**
     * @return a string representation of a commit's hash value.
     */
    public String hashValue() {
        return this.hashValue;
    }

    /**
     * @return an unmodifiable file map in the current commit.
     */
    public Map<String, String> commitMapping() {
        return Collections.unmodifiableMap(fileMapping);
    }

    /**
     * @return the hash value of the parent commit.
     */
    public String parentCommit() {
        return this.parent;
    }

    /**
     * @return the hash value of the second parent commit.
     */
    public String secondParentCommit() {
        return this.secondParent;
    }

    /**
     * Read from the current staging area, and then clear the stage file.
     */
    private void readStagingArea() {
        Stage currStage = new Stage();
        Map<String, String> add = currStage.stageMap();
        Set<String> remove = currStage.removeFiles();
        if (add.isEmpty() && remove.isEmpty()) {
            exitWithError("No changes added to the commit.");
        }

        // create a new map to store info in the staging area
        this.fileMapping.putAll(add);
        this.fileMapping.keySet().removeAll(remove);

        // clear staging area after a copy has been made
        currStage.clearStagingArea();
        currStage.writeToStage();
    }

    /**
     * Generate a unique hash ID of a single commit.
     * Two commits have the same SHA-1 if they have the same metadata,
     * the same mapping of names to references, and the same parent reference.
     */
    private String generateHashValue() {
        return sha1(dateFormat(timestamp), message, parent, secondParent, fileMapping.toString());
    }

    /**
     * @return a formatted string of the given timestamp.
     */
    public static String dateFormat(Date time) {
        Format dateFormatter = new SimpleDateFormat("E MMM d HH:mm:ss yyyy Z");
        return dateFormatter.format(time);
    }

    /**
     * Save the current commit to a file with its SHA-1 value as file name.
     */
    protected void saveCommit() {
        File commitFile = join(COMMIT_DIR, this.hashValue);
        writeObject(commitFile, this);
    }

    /**
     * Read a past commit from files in the commit directory.
     * @param commitID the SHA-1 value of the commit.
     * @return the Commit instance.
     */
    public static Commit readCommit(String commitID) {
        if (commitID.length() < 40) {
            commitID = findPrefix(commitID);
        }
        File commitInfo = join(COMMIT_DIR, commitID);
        if (!commitInfo.exists()) {
            exitWithError("No commit with that id exists.");
        }
        return readObject(commitInfo, Commit.class);
    }

    /**
     * Support abbreviated commit ID lookup.
     * @param abbrev the shortened ID with fewer than 40 characters.
     * @return the complete 40-character hexadecimal hash value. If there is no commit ID fits
 *             the abbreviation, return an empty string.
     */
    public static String findPrefix(String abbrev) {
        List<String> commitWithPrefix = readAllCommits().stream()
                               .filter(c -> c.startsWith(abbrev)).collect(Collectors.toList());
        return commitWithPrefix.isEmpty() ? "" : commitWithPrefix.get(0);
    }

    /**
     * @return all plain files in the working directory but null pointer safe.
     */
    public static List<String> readAllCommits() {
        List<String> allCommits = plainFilenamesIn(COMMIT_DIR);
        return (allCommits == null) ? new ArrayList<>() : allCommits;
    }

    /**
     * @return a formatted commit information.
     */
    @Override
    public String toString() {
        return "===\n"
               + "commit " + this.hashValue() + "\n"
               + "Date: " + this.commitTime() + "\n"
               + this.commitMessage() + "\n";
    }

    /**
     *@return the file representing the commit directory in a remote server reachable by path.
     */
    public static File remoteCommitDir(String path) {
        return join(path, "logs");
    }

    /**
     * @return the Commit instance in the remote machine.
     */
    public static Commit readRemoteCommit(String path, String commitID) {
        return readObject(join(remoteCommitDir(path), commitID), Commit.class);
    }
}
