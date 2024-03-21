package gitlet;

import static gitlet.Repository.*;
import static gitlet.Utils.*;

/**
 * Driver class for Gitlet, a subset of the Git version-control system.
 * @author Yutong Wang
 */
public class Main {
    /**
     * Usage: java gitlet.Main ARGS, where ARGS contains
     * <COMMAND> <OPERAND1> <OPERAND2> ...
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
            case "global-log":
                // handle the `log` command
                validateGitInit();
                validateArgc(args, 1);
                System.out.println(globalLog());
                break;
            case "checkout":
                // handle the `checkout [file name/commit id/branch name]` command
                validateGitInit();
                checkout(args);
                break;
            case "find":
                // handle the `find [commit message]` command
                validateGitInit();
                validateArgc(args, 2);
                System.out.println(findCommitID(args[1]));
                break;
            case "status":
                // handle the `status` command
                validateGitInit();
                validateArgc(args, 1);
                System.out.println(status());
                break;
            case "branch":
                // handle the `branch [branch name]` command
                validateGitInit();
                validateArgc(args, 2);
                createBranch(args[1]);
                break;
            case "rm-branch":
                // handle the `rm-branch [branch name]` command
                validateGitInit();
                validateArgc(args, 2);
                removeBranch(args[1]);
                break;
            case "reset":
                // handle the `reset [commit id]` command
                validateGitInit();
                validateArgc(args, 2);
                resetHard(args[1]);
                break;
            case "merge":
                // handle the `merge [branch name]` command
                validateGitInit();
                validateArgc(args, 2);
                merge(args[1]);
                break;
            case "add-remote":
                // handle the `add-remote [remote name] [name of remote directory]/.gitlet` command
                validateGitInit();
                validateArgc(args, 3);
                Remote.addRemote(args[1], args[2]);
                break;
            case "rm-remote":
                // handle the `rm-remote [remote name]` command
                validateGitInit();
                validateArgc(args, 2);
                Remote.removeRemote(args[1]);
                break;
            case "push":
                // handle the `push [remote name] [remote branch name]` command
                validateGitInit();
                validateArgc(args, 3);
                Remote.push(args[1], args[2]);
                break;
            case "fetch":
                // handle the `fetch [remote name] [remote branch name]` command
                validateGitInit();
                validateArgc(args, 3);
                Remote.fetch(args[1], args[2]);
                break;
            case "pull":
                // handle the `pull [remote name] [remote branch name]` command
                validateGitInit();
                validateArgc(args, 3);
                Remote.pull(args[1], args[2]);
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
