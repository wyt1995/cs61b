package gitlet;

import static gitlet.Repository.*;
import static gitlet.Utils.*;

/** Driver class for Gitlet, a subset of the Git version-control system.
 *  @author Yutong Wang
 */
public class Main {

    /** Usage: java gitlet.Main ARGS, where ARGS contains
     *  <COMMAND> <OPERAND1> <OPERAND2> ... 
     */
    public static void main(String[] args) {
        // handle empty args
        validateNonemptyArgs(args);

        String firstArg = args[0];
        switch (firstArg) {
            case "init":
                // handle the `init` command
                validateArgc(args, 1);
                init();
                break;
            case "add":
                // handle the `add [filename]` command
                validateGitInit();
                validateArgc(args, 2);
                Stage addStage = new Stage();
                addStage.addToStagingArea(args[1]);
                break;
            case "commit":
                // handle the `commit [message]` command
                validateGitInit();
                validateArgc(args, 2);
                makeCommit(args[1]);
                break;
            case "rm":
                // handle the `rm [filename]` command
                validateGitInit();
                validateArgc(args, 2);
                Stage rmStage = new Stage();
                rmStage.removeFromStagingArea(args[1]);
                break;
            case "log":
                // handle the `log` command
                validateGitInit();
                validateArgc(args, 1);
                System.out.println(logHistory());
                break;
            case "checkout":
                // handle the `checkout [file name/commit id/branch name]` command
                validateGitInit();
                checkout(args);
                break;
            default:
                validateGitInit();
                exitWithError("No command with that name exists.");
        }
    }


    /**
     * Checks the number of arguments against the expected number.
     * Exit program if they do not match.
     * @param args Arguments array from command line.
     * @param argc Expected number of arguments.
     */
    public static void validateArgc(String[] args, int argc) {
        if (args.length != argc) {
            exitWithError("Incorrect operands.");
        }
    }

    /**
     * Exit program if the command line argument is empty.
     * @param args Arguments array from command line.
     */
    public static void validateNonemptyArgs(String[] args) {
        if (args.length == 0) {
            exitWithError("Please enter a command.");
        }
    }

    /**
     * Check whether the current working directory has a .gitlet subdirectory.
     * If it is not initialized, exit the program.
     */
    public static void validateGitInit() {
        if (!GITLET_DIR.exists()) {
            exitWithError("Not in an initialized Gitlet directory.");
        }
    }
}
