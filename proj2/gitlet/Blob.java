package gitlet;

import java.io.File;
import java.io.Serializable;

import static gitlet.Repository.OBJECT_DIR;
import static gitlet.Utils.*;

public class Blob implements Serializable {

    private String id;

    private byte[] bytes;

    private File fileName;

    private String filePath;

    private File blobSaveFileName;

    public Blob(File fileName) {
        this.fileName = fileName;
        this.bytes = readFile();
        this.filePath = fileName.getPath();
        this.id = generateID();
        this.blobSaveFileName = generateBlobSaveFileName();
    }

    private byte[] readFile() {
        return readContents(fileName);
    }

    private String generateID() {
        return sha1(filePath, bytes);
    }

    private File generateBlobSaveFileName() {
        return join(OBJECT_DIR, id);
    }

    public void save() {
        writeObject(blobSaveFileName, this);
    }

    public String getBlobId() {
        return id;
    }

    public String getPath() {
        return filePath;
    }

}
