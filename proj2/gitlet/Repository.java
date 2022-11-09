package gitlet;


import java.io.File;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static gitlet.Utils.*;

import static gitlet.MyUtiles.*;

// TODO: any imports you need here

/** Represents a gitlet repository.
 *  TODO: It's a good idea to give a description here of what else this Class
 *  does at a high level.
 *
 *  @author TODO
 */
public class Repository {
    /**
     * TODO: add instance variables here.
     *
     * List all instance variables of the Repository class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided two examples for you.
     */

    /** The current working directory. */
    public static final File CWD = new File(System.getProperty("user.dir"));
    /*
     *   .gitlet
     *      |--objects
     *      |     |--commit and blob
     *      |--refs
     *      |    |--heads
     *      |         |--master
     *      |--HEAD
     *      |--stage
     */
    public static final File GITLET_DIR = join(CWD, ".gitlet");
    public static final File OBJECT_DIR = join(GITLET_DIR, "objects");
    public static final File REFS_DIR = join(GITLET_DIR, "refs");
    public static final File HEADS_DIR = join(REFS_DIR, "heads");
    public static final File HEAD_FILE = join(GITLET_DIR, "HEAD");
    public static final File ADD_STAGE_FILE = join(GITLET_DIR, "add_stage");
    public static final File REMOVE_STAGE_FILE = join(GITLET_DIR, "remove_stage");


    public static Commit currCommit;
    public static Stage addStage;
    public static Stage removeStage;



    /* TODO: fill in the rest of this class. */
    public static void init() {

        if (GITLET_DIR.exists()) {
            System.out.println("A Gitlet version-control system already exists in the current directory.");
            System.exit(0);
        }

        mkdir(GITLET_DIR);
        mkdir(OBJECT_DIR);
        mkdir(REFS_DIR);
        mkdir(HEADS_DIR);

        initCommit();
        initHEAD();
        initHeads();

    }

    private static void initCommit() {
        Commit initCommit = new Commit();
        currCommit = initCommit;
        initCommit.save();
    }


    private static void initHEAD() {
        writeContents(HEAD_FILE, "master");
    }

    private static void initHeads() {
        File HEADS_FILE = join(HEADS_DIR, "master");
        writeContents(HEADS_FILE, currCommit.getID());
    }

    public static void add(String file) {
        File fileName = getFileFromCWD(file);
        if (!fileName.exists()) {
            System.out.println("File does not exist.");
            System.exit(0);
        }
        Blob blob = new Blob(fileName);
        storeBlob(blob);
    }

    public static void storeBlob(Blob blob) {
        currCommit = readCurrCommit();
        addStage = readAddStage();
        removeStage = readRemoveStage();

        if (addStage.isNewBlob(blob)) {
            blob.save();
            if (addStage.isFilePathExists(blob.getPath())) {
                addStage.delete(blob);
            }
            addStage.add(blob);
            addStage.saveAddStage();
        }

    }

    private static File getFileFromCWD(String file) {
        return Paths.get(file).isAbsolute()
                ? new File(file)
                : join(CWD, file);
    }

    private static Commit readCurrCommit() {
        String currCommitID = readCurrCommitID();
        File CURR_COMMIT_FILE = join(OBJECT_DIR, currCommitID);
        return readObject(CURR_COMMIT_FILE, Commit.class);
    }

    private static String readCurrCommitID() {
        String currentBranch = readCurrentBranch();
        File HEADS_FILE = join(HEADS_DIR, currentBranch);
        return readContentsAsString(HEADS_FILE);
    }

    private static String readCurrentBranch() {
        return readContentsAsString(HEAD_FILE);
    }

    private static Stage readAddStage() {
        if (!ADD_STAGE_FILE.exists()) {
            return new Stage();
        }
        return readObject(ADD_STAGE_FILE, Stage.class);
    }

    private static Stage readRemoveStage() {
        if (!REMOVE_STAGE_FILE.exists()) {
            return new Stage();
        }
        return readObject(ADD_STAGE_FILE, Stage.class);
    }

    public static void commit(String message) {
        if (message.equals("")) {
            System.out.println("Please enter a commit message.");
            System.exit(0);
        }
        Commit newCommit = newCommit(message);
        saveNewCommit(newCommit);
    }

    private static void saveNewCommit(Commit newCommit) {
        newCommit.save();
        addStage.clear();
        addStage.saveAddStage();
        removeStage.clear();
        removeStage.saveRemoveStage();
        saveHeads(newCommit);
    }

    private static void saveHeads(Commit newCommit) {
        currCommit = newCommit;
        String currentBranch = readCurrentBranch();
        File HEADS_FILE = join(HEADS_DIR, currentBranch);
        writeContents(HEADS_FILE, currCommit.getID());
    }

    private static Commit newCommit(String message) {
        Map<String, String> addBlobMap = findAddBlobMap();
        Map<String, String> removeBlobMap = findRemoveBlobMap();
        checkIfNewCommit(addBlobMap, removeBlobMap);

        currCommit = readCurrCommit();
        Map<String, String> blobMap = getBlobMapFromCurrCommit(currCommit);
        blobMap =caculateBlobMap(blobMap, addBlobMap, removeBlobMap);
        List<String> parents = findParents();
        return new Commit(message, blobMap, parents);
    }

    private static List<String> findParents() {
        List<String> parents = new ArrayList<>();
        currCommit = readCurrCommit();
        parents.add(currCommit.getID());
        return parents;
    }

    private static Map<String, String> findAddBlobMap() {
        addStage = readAddStage();
        return addStage.getBlobMap();
    }

    private static Map<String, String> findRemoveBlobMap() {
        removeStage = readRemoveStage();
        return removeStage.getBlobMap();
    }

    private static Map<String, String> getBlobMapFromCurrCommit(Commit currCommit) {
        return currCommit.getPathToBlobID();
    }

    private static Map<String, String> caculateBlobMap(Map<String, String> blobMap,
                                                       Map<String, String> addBlobMap,
                                                       Map<String , String> removeBlobMap) {
        if (!addBlobMap.isEmpty()) {
            for (String path : addBlobMap.keySet()) {
                blobMap.put(path, addBlobMap.get(path));
            }
        }

        if (!removeBlobMap.isEmpty()) {
            for (String path : removeBlobMap.keySet()) {
                blobMap.remove(path);
            }
        }

        return blobMap;
    }


    private static void checkIfNewCommit(Map<String, String> addBlobMap,
                                         Map<String, String> removeBlobMap) {
        if (addBlobMap.isEmpty() && removeBlobMap.isEmpty()) {
            System.out.println("No changes added to the commit.");
            System.exit(0);
        }
    }
}
