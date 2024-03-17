package gitlet;

import java.io.File;
import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static gitlet.Repository.*;
import static gitlet.Utils.*;

/**
 * Represents the Staging Area of Gitlet version control system.
 */
public class Stage implements Serializable {
    /** The file for staging area under the .gitlet directory. */
    public static final File STAGE_FILE = join(GITLET_DIR, "stage");

    /** Map from file name to a blob object. */
    private final Map<String, String> addition;
    /** Set of file names to be removed from the current commit. */
    private final Set<String> removal;

    /**
     * Set up a staging area when command `add` or `rm` is called.
     * If the stage file already exists, put all of its entries to this instance.
     */
    public Stage() {
        this.addition = new HashMap<>();
        this.removal = new HashSet<>();
        if (STAGE_FILE.exists()) {
            Stage prevStage = readFromStage();
            this.addition.putAll(prevStage.stageMap());
            this.removal.addAll(prevStage.removeFiles());
        }
    }

    /**
     * @return the file mapping of the current staging area.
     */
    public Map<String, String> stageMap() {
        return this.addition;
    }

    /**
     * @return the set of files to be removed in the next commit.
     */
    public Set<String> removeFiles() {
        return this.removal;
    }

    /**
     * Find file under the current directory.
     * @param filename the name of the file.
     * @return a File object if it exists; otherwise, exit with error message.
     */
    private File findFile(String filename) {
        File file = join(CWD, filename);
        if (!file.exists()) {
            exitWithError("File does not exist.");
        }
        return file;
    }

    /**
     * Add a file to the staging area.
     * If the file does not exist in the staging area, stage the file for addition.
     * If it is already staged, overwrite the previous entry with the new contents.
     * However, if the current working version is identical to the version in the current commit,
     * do not stage the file, and remove it from the staging area if it is already there.
     *
     * @param filename as specified by the command line argument.
     */
    public void addToStagingArea(String filename) {
        File newFile = findFile(filename);
        Blob fileBlob = new Blob(newFile);

        // if the current file version is identical to the most recent commit
        Commit currCommit = Commit.readRecentCommit();
        String oldBlobID = currCommit.commitMapping().get(filename);
        String newBlobID = fileBlob.blobHashValue();
        if (oldBlobID.equals(newBlobID)) {
            addition.remove(filename);
        } else {
            addition.put(filename, fileBlob.blobHashValue()); // stage the file for addition
            removal.remove(filename);  // no longer staged for removal
            fileBlob.saveBlob();  // save a snapshot
        }
        // In either cases, update the stage file
        writeToStage();
    }

    /**
     * Handle the `rm` command.
     * Unstage the file if it is currently staged for addition.
     * If the file is in the current commit, stage for removal and remove it from working directory.
     * If the file is neither staged nor tracked by the head commit, exit with an error message.
     *
     * @param filename as specified by the command line argument.
     */
    public void removeFromStagingArea(String filename) {
        File toBeRemoved = join(CWD, filename);
        Commit currCommit = Commit.readRecentCommit();

        if (!addition.containsKey(filename) && !currCommit.commitMapping().containsKey(filename)) {
            exitWithError("No reason to remove the file.");
        }

        if (addition.containsKey(filename)) {
            addition.remove(filename);  // unstage the file from addition
        } else {
            removal.add(filename);  // stage for removal if it has not been added
            restrictedDelete(toBeRemoved);
        }
        writeToStage();  // update the stage file after addition or removal
    }

    /**
     * Read the saved stage file as a Map.
     * @return the Map from file names to Blob objects.
     */
    protected static Stage readFromStage() {
        return readObject(STAGE_FILE, Stage.class);
    }

    /**
     * Save the current staging area into the stage file.
     */
    private void writeToStage() {
        writeObject(STAGE_FILE, this);
    }
}
