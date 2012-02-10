

Annexes
=======

## Git notes

### [Git: Move recent commit to a new branch](http://stackoverflow.com/questions/1628563/git-move-recent-commit-to-a-new-branch)

```bash
git branch newbranch
git reset --hard HEAD~3  # go back 3 commits
git checkout newbranch
``` 

This can be used when file in the current branch should be committed in an other branch, but a checkout would loose the modifications.
