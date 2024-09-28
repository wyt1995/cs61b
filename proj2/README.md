# Gitlet

This project mimics the core functionality of the Git version control system, 
including init, add, commit, remove, log, checkout, status, branch, merge, and remote feetures.

**What I have learned**: Git version control is crucial for software development, 
but is also hard to learn as it is not always intuitive. I bet I'm not the only one 
who's experienced the frustration of using Git for version control, only to lose track of files 
or changes that should have been preserved, leaving me scrambling to find a solution.
By implementing my own version of Git, I have gained a deeper understanding of how it works.

I came across this project as a part of CS 61B, the data structure course at UC Berkeley as taught 
in the spring 2021 semester. This is my first time writing a codebase of thousands of lines 
with very little skeleton or external help. I think this experience was both challenging and rewarding.

All classes and functions are carefully documented.

**Usage**: After compilation, type `java gitlet.Main` in the shell, followed by a specific git command.

## Commands
The commands used by Gitlet can have subtle differences with the real Git. I will go through each command in detail as follows.

### init
Creates a new Gitlet version control system in the current directory. 
This system will automatically start with one commit, which contains no files and has the `initial commit` message. 
It will have a default `master` branch, whose HEAD initially points to this initial commit. 
All commits in all repositories will trace back to this one.

This command also creates a `.gitlet` folder under the current working directory, with the following structure:
```
.gitlet
  |--objects  (for file contents)
  |--logs     (for commit records)
  |--branches (for the structure tree)
  |--HEAD     (for the current Head pointer)
  |--stage    (for staging area)
  |--remote   (for remote repository)
```

### add
A file name is required following the `add` command. It adds a copy of the file as it currently exists to the staging area.
Staging an already-staged file overwrites the previous entry in the staging area with the new contents.
If the current working version of the file is identical to the version in the most recent commit, 
do not stage it to be added, and remove it from the staging area if it is already there 
(as can happen when a file is changed, added, and then changed back to its original version).

### commit
Saves a snapshot of tracked files in the current commit and staging area
so they can be restored at a later time, creating a new commit.
By default, each commit’s snapshot of files will be exactly the same as its parent commit’s 
snapshot of files; it will keep versions of files exactly as they are, and not update them. 
A commit will only update the contents of files it is tracking that have been staged for addition at the time of commit.
Files tracked in the current commit may be untracked in the new commit as a result being staged for removal by the `rm` command.

Git tracks the changes made in the working file and only saves the difference from the previous commit.
Gitlet simplifies the file reading by saving a whole copy.

### rm
Unstage the file if it is currently staged for addition. If the file is tracked in the current commit, 
stage it for removal and remove the file from the working directory.

### log
Starting at the current head commit, display information about each commit backwards along the commit tree 
until the initial commit, following the first parent commit links, ignoring any second parents found in merge commits. 
This is what you get with `git log --first-parent`.

### global-log
This is similar to the `log` command, except that it displays information about all commits ever made.
Commits are ordered by the time it has been made.

### find
Prints out the ids of all commits that have the given commit message, one per line. 
If there are multiple such commits, it prints the ids out on separate lines.

### status
Displays what branches currently exist, and marks the current branch with a *. 
The output then displays what files have been staged for addition or removal. Here is an example:
```
=== Branches ===
*master
other-branch
  
=== Staged Files ===
hello.c
README.md
  
=== Removed Files ===
goodbye.txt
  
=== Modifications Not Staged For Commit ===
newfile.txt (deleted)
design.html (modified)
  
=== Untracked Files ===
random.stuff
```

### checkout
There are three possible use cases for `checkout`:
1. `chekout [filename]`: Takes the version of the file as it exists in the head commit and puts it in the working directory, overwriting the version of the file that’s already there if there is one.
2. `checkout [commit] [filename]`: Takes the version of the file as it exists in the commit with the given id, and puts it in the working directory, overwriting the version of the file that’s already there if there is one.
3. `checkout [branch]`: Takes all files in the commit at the head of the given branch, and puts them in the working directory, overwriting the versions of the files that are already there if they exist. At the end of this command, the given branch is now considered the current branch (HEAD).

### branch
Creates a new branch with the given name, and points it at the current head commit.
If a branch with the given name already exists, print the error message A branch with that name already exists.

### rm-branch
Deletes the branch with the given name. This only deletes the pointer associated with the branch; 
it does not mean to delete all commits that were created under the branch.

### reset
Checks out all the files tracked by the given commit. Removes tracked files that are not present in that commit. Also moves the current branch’s head to that commit node.
This is similar to `git reset` with the `--hard` option.

### merge
Merges files from the given branch into the current branch.
It first finds the split point of the current branch and the given branch. 
If the split point is the current branch, then the effect is to check out the given branch.
Otherwise, it considers the files tracked at the split point, current commit, as well as the given branch.
Any files modified in different ways in the current and given branches are in conflict, which will 
be replaced by the contents of both branches.


## Testing
The course staff at Berkeley designed an integration test framework. A `tester.py` script interprets
files with an `.in` extension to validate command-line outputs. I have extended this framework with 
more test cases, all stored under the `testing` directory. My implementation has also passed all 
tests provided on Gradescope.

For more information, read full specification of the project [here](https://sp21.datastructur.es/materials/proj/proj2/proj2) 
as well as the CS 61B course [homepage](https://sp21.datastructur.es/).

