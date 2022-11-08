package gitlet;

import java.io.File;
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
    private static final File HEAD_FILE = join(GITLET_DIR, "HEAD");

    public static Commit currCommit;



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
}
