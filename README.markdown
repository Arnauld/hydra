
Links to starts
===============

* <a href="http://msdn.microsoft.com/en-us/library/hh194784(v=vs.110).aspx">TPL Dataflow API doc (en)</a>
* [Introduction à TPL DataFlow en C# (french)](http://msdn.microsoft.com/fr-fr/vcsharp/hh301085)
* [Sous le capot : Etude de la Task Parallel Library](http://blogs.msdn.com/b/eternalcoding/archive/2011/05/16/fr-sous-le-capot-etude-de-la-task-parallel-library.aspx)

* [Task Parallel Library in .NET 4.0 – Part 1](http://codingndesign.com/blog/?p=195)
* [Task Parallel Library in .NET 4.0 – Part 2](http://codingndesign.com/blog/?p=198)
* [Task Parallel Library in .NET 4.0 – Part 3](http://codingndesign.com/blog/?p=202)
* [TPL DataFlow–BufferBlock<T> & Load Balancing](http://codingndesign.com/blog/?p=212)

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
