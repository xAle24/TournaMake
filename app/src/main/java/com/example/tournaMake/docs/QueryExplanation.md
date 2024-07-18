Sure, let's break down the SQL query you provided:

```sql
SELECT GAME.*, COUNT(MATCH_TM.gameID) as times_played
FROM GAME
JOIN MATCH_TM ON GAME.gameID = MATCH_TM.gameID
JOIN MAIN_TEAM_SCORE ON MATCH_TM.matchID = MAIN_TEAM_SCORE.matchID
WHERE MAIN_TEAM_SCORE.email = :email
GROUP BY GAME.name
```

### Explanation

1. **SELECT Clause**:
   ```sql
   SELECT GAME.*, COUNT(MATCH_TM.gameID) as times_played
   ```
    - `GAME.*`: Selects all columns from the `GAME` table.
    - `COUNT(MATCH_TM.gameID) as times_played`: Counts the number of times each game has been played and assigns this count to the alias `times_played`.

2. **FROM Clause**:
   ```sql
   FROM GAME
   ```
    - Specifies the `GAME` table as the main table to query.

3. **JOIN Clauses**:
    - `JOIN MATCH_TM ON GAME.gameID = MATCH_TM.gameID`:
        - Performs an inner join between the `GAME` table and the `MATCH_TM` table on the `gameID` column, meaning only rows with matching `gameID` in both tables will be included.
    - `JOIN MAIN_TEAM_SCORE ON MATCH_TM.matchID = MAIN_TEAM_SCORE.matchID`:
        - Performs another inner join between the `MATCH_TM` table and the `MAIN_TEAM_SCORE` table on the `matchID` column, meaning only rows with matching `matchID` in both tables will be included.

4. **WHERE Clause**:
   ```sql
   WHERE MAIN_TEAM_SCORE.email = :email
   ```
    - Filters the results to only include rows where the `email` column in the `MAIN_TEAM_SCORE` table matches the provided email value (`:email` is a placeholder for a parameter value).

5. **GROUP BY Clause**:
   ```sql
   GROUP BY GAME.name
   ```
    - Groups the results by the `name` column from the `GAME` table. This means that the `COUNT(MATCH_TM.gameID)` will be calculated for each group of games with the same name.

### Purpose of the Query

The query retrieves information about games along with the number of times each game has been played by a specific user (identified by the `email` provided). The `times_played` count is calculated based on the number of matches associated with each game.

### What Each Part Does:

- **Tables Involved**:
    - `GAME`: Contains details about different games.
    - `MATCH_TM`: Contains information about matches, including which game each match is associated with.
    - `MAIN_TEAM_SCORE`: Contains scoring information for matches, including the email of the player/team associated with each match.

- **Joins**:
    - Connects the `GAME` table to the `MATCH_TM` table using `gameID`.
    - Connects the `MATCH_TM` table to the `MAIN_TEAM_SCORE` table using `matchID`.

- **Filtering**:
    - Only includes rows where the `email` in `MAIN_TEAM_SCORE` matches the given email parameter.

- **Grouping and Aggregation**:
    - Groups the results by game name and counts the number of times each game has been played (`times_played`).

### Simplified Explanation

The query is designed to find out how many times each game has been played by a specific user. It joins three tables to get the necessary data, filters it by the user's email, groups it by game name, and counts the number of matches for each game.