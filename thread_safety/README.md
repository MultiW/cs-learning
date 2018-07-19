# Thread Safety Notes
## Motivation
I got stumped by a parent's question working at an elementary-level coding camp   

During work at Coding with Kids, I had my students demo their Minecraft Modding projects to their parents. 
One of the parents who came was a software developer. His student had built a multiplayer hide-and-seek mod. We designed it so that 
there is one "seeker" in the game, and that's the first player who joins the game. In code, there's a onJoin event-handler
that is called when a player joins the game. When the first player joins, he sets a boolean variable shared by all players to be true. Any player 
who joins afterwards will know from the global variable that the "seeker" has been chosen, so they will be a "hider". When the student 
demoed this to the parent and showed him the code, the first thing the parent said is that there is a bug in the code. He asks 
how the code will handle the case when two players join the game at once, causing a thread safety issue. FAIR question. But I had no 
idea what he even meant. I mumbled something about singletons and we moved on.

## The issue
In the students' example above, when two players join simultaneously, both players may end up becoming a seeker. This happens when one 
player checks the common variable and finds it to be false, and before he can set the variable to true, the second player 
checks the variable and also finds it to be false. Thus, both players find that there is no seeker yet and both players become a seeker.   

The problem arises because the game's process runs multiple threads in parallel. To do so, it runs part of one 
thread, switches to another thread and runs part of that thread, then switches to another thread and etc. When the first player joins, 
he performs two steps: checking the common variable and setting the common variable. Suppose two players join, then the game process 
must handle two threads. It can execute the threads' code in the following order: make the first thread perform a check, make the second 
thread perform a check, then make the first thread perform a set, and make the second thread perform a set. This would effectively create 
two seekers in the game.

## Notes
[Volatile variables](https://www.javamex.com/tutorials/synchronization_volatile_typical_use.shtml)
* ```volatile``` keyword indicates a variable is accessed by multiple threads
* This forces the variable to read from and written to only in the main memory
  * Prevents another thread from caching the variable.
  * If another thread caches the variable, then its copy of the variable won't be in sync with other thread's variables
* Different threads may have their own cached copy of a static variable

[Java threading tutorial](https://winterbe.com/posts/2015/04/07/java8-concurrency-tutorial-thread-executor-examples/)

[Java synchronized example](https://www.geeksforgeeks.org/synchronized-in-java/)
