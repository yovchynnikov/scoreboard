This is a simple implementation of scoreboard.

Test cases:
- no live matches -> empty scoreboard
- create match if scoreboard is empty -> match in scoreboard
- create match if scoreboard is not empty -> match in scoreboard with correct order
- create match if home team already participates in other live match -> throw exception
- create match if guest team already participates in other live match -> throw exception
- update match -> match in scoreboard
- update match with changing order -> match in scoreboard with correct order
- update match if match doesn't exist -> throw exception
- finish match -> match not in scoreboard
- finish match if match doesn't exist -> throw exception
- create match after finishing -> match in scoreboard
- validations: empty team name(s), negative score(s)

WIP.