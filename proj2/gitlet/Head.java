package gitlet;

import java.io.File;

import static gitlet.Repository.GITLET_DIR;
import static gitlet.Utils.*;


/**
 * The Head pointer tracks the current working state of the version-control system.
 * At any given time, there is only one active Head pointer at the front the current branch.
 */
public class Head {
    /** The HEAD file location under the .gitlet directory. */
    public static final File HEAD_FILE = join(GITLET_DIR, "HEAD");

    /**
     * @return the name of current branch that is tracked by the HEAD pointer.
     */
    public static String getHeadState() {
        return readContentsAsString(HEAD_FILE);
    }

    /**
     * Save the HEAD state to the head file.
     * @param currBranch the name of current working branch.
     */
    public static void setHeadPointer(String currBranch) {
        writeContents(HEAD_FILE, currBranch);
    }
}
