This is a simple implementation of scoreboard.

Assumptions:

1. Team name is unique and case-sensitive
2. The team can participate in only one match at the same time.
3. The race conditions are possible, but dealing with them requires more implementation details.
4. No mention how should library react to wrong updates. I added some basic validations: scores number, match existing, team names.
5. No mention the matches' history. The current implementation just removes the match from the scoreboard.

Implementation details:

1. I didn't pay much attention to concurrency, just used concurrent collections to avoid the much usual error (ConcurrentModificationException)
2. Create/update/finish operations should be atomic; with the current implementation, race conditions are possible. To make the operations atomic, more complex implementation is required. 
3. Storage isn't a separate class; it really doesn't make much sense to extract it for this implementation
4. It was difficult to choose between sort-on-insert and sort-on-get strategies. It depends on the scoreboard sizes, and a ratio of update/get requests. As a result, I choose the easiest option.  

Skipped:

1. For some reason, the match can be not closed, and this makes impossible to create a new match for one of the teams  
2. In the real world, the library should operate with IDs, not with teams pairs. Late updates can lead to wrong results. Consider the situation, when the same teams have the match next day, and the update from the previous date pops up.
3. Continue, it's possible to receive updates for the same match in reversed order. For this "updatedAt" or something like that should be introduced. 

