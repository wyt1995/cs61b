package gitlet;

import java.io.File;
import java.io.Serializable;

import static gitlet.Repository.*;
import static gitlet.Utils.*;

/**
 * The saved contents of files. Each blob is tracked in a single commit.
 */
public class Blob implements Serializable {
    /** The blobs folder under the .gitlet directory. */
    public static final File OBJECT_DIR = join(GITLET_DIR, "objects");

    /** The content of a blob is represented by a stream of bytes. */
    private final byte[] content;
    /** Each blob has a unique hash value. */
    private final String blobHash;

    /**
     * A Blob is represented by file content and a unique hash id associated with it.
     * @param text the File to be saved.
     */
    public Blob(File text) {
        this.content = readContents(text);
        this.blobHash = generateBlobID();
    }

    /**
     * Generate a SHA-1 hash value based on the file content.
     */
    private String generateBlobID() {
        return sha1((Object) this.content);
    }

    /**
     * @return the SHA-1 hash value of the blob.
     * Can be used for file comparison: If two blobs have the same SHA-1,
     * we assume that they have the same content.
     */
    public String blobHashValue() {
        return this.blobHash;
    }

    /**
     * Write blob into a new file under directory `.gitlet/object` with its SHA-1 as file name.
     */
    public void saveBlob() {
        File blobFile = join(OBJECT_DIR, blobHash);
        writeContents(blobFile, (Object) this.content);
    }

    /**
     * Read saved snapshot file as a stream of bytes.
     * @param blobID the file name (SHA-1) of the blob.
     */
    public static byte[] readBlob(String blobID) {
        return readContents(join(OBJECT_DIR, blobID));
    }

    /**
     * Read saved snapshot file as a string.
     * @param blobID the file name (SHA-1) of the blob.
     */
    public static String readBlobAsString(String blobID) {
        return readContentsAsString(join(OBJECT_DIR, blobID));
    }

    /**
     * @return true if the given blob already exists, false otherwise.
     */
    public static boolean checkBlobExists(String blobID) {
        return join(OBJECT_DIR, blobID).exists();
    }

    /**
     * Find the blob object with its ID in the remote machine.
     */
    public static File findRemoteBlob(String path, String blobID) {
        return join(path, "objects", blobID);
    }
}
