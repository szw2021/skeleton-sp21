package gitlet;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import static gitlet.Utils.writeObject;

public class Stage implements Serializable {

    private Map<String, String> pathToBlobID = new HashMap<>();

    public Map<String, String> getBlobMap() {
        return this.pathToBlobID;
    }

    public void clear() {
        pathToBlobID.clear();
    }

    public void saveAddStage() {
        writeObject(Repository.ADD_STAGE_FILE, this);
    }

    public void saveRemoveStage() {
        writeObject(Repository.REMOVE_STAGE_FILE, this);
    }

    public boolean isNewBlob(Blob blob) {
        if (!pathToBlobID.containsValue(blob.getBlobId())) {
            return true;
        }
        return false;
    }

    public boolean isFilePathExists(String Path) {
        if (pathToBlobID.containsKey(Path)) {
            return true;
        }
        return false;
    }

    public void delete(Blob blob) {
        pathToBlobID.remove(blob.getPath());
    }

    public void add(Blob blob) {
        pathToBlobID.put(blob.getPath(), blob.getBlobId());
    }

}
