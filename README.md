


# TournaMake: an app to create tournaments
TournaMake is an app that allows you to create your own tournaments and keep track of the scores of all the teams playing. It also allows you to create one-shot matches with an unlimited number of teams playing. You can add games and see statistics for each of them. You can edit your profile picture and see bar charts representing how many times you played certain games.

## Main profiles and guest profiles
The application currently distinguishes between main profiles, created upon registration, and guest profiles, created ad-hoc in the profile menu to quickly allow you to reach the minimum number of participants you need for your matches and tournaments. This difference will likely be removed in future updates. For now, the limitations of guest profiles are that charts can't be plotted for them, nor can you see them in the specific profile screen.

## Match creation
To create a match, select a game and start adding teams. Each team can contain 0, one or more profiles (which can be both main and guest profiles). You can add them by pressing the "Add member" button, and remove them by clicking on the X besides each team member. You can remove entire teams by pressing the trash can in the bottom right corner of each team element.

**Disclaimer**: there is currently an annoying behaviour that makes the newly added team appear at the top of the column, with the same name and members of other present teams. To correctly see the empty team, scroll down and then up again before modifying the newly added team.

Once you are ready, you can click "Create Match" to navigate to the match screen. Here you can see all the members in each team and edit each team's score. Clicking on the save button will write the current state to the database, but no feedback for the user is provided. Just know that it happens :) . Clicking on the end button will let you choose the winners, which can be more than one. Leave all the fields blank to register a draw. **Note**: in tournaments you are not allowed to register draws or multiple winners, so you need to determine a single winner.

## Tournament creation
A tournament is a set of matches, each involving exactly two teams (the teams can have an unlimited number of members though). After each match is completed, the winning team advances to the next round. Clicking on the matches will open the same screen opened by clicking one of the one-shot matches.
The application currently supports just Single Elimination tournaments type. This means that as soon as one team loses it's automatically out of the tournament. We plan on adding a Double Elimination tournament type to allow players to continue if they undergo one defeat at most.
The important thing to remember is that to create a tournament you need a minimum of 2 teams and **can only start a tournament if the team number is a power of 2** (such as 2, 4, 8, 16 and so on). If your number of teams does not match the required number, you can add placeholder guest profiles to fill in the gaps, and use the rule that those placeholders always lose against a legitimate team.

# Why this project?
This project was realised for the Mobile Systems Programming course of the University of Computer Science and Engineering at University of Bologna, Cesena campus. It was also our intention to use it in our everyday life to have fun with friends and keep track of our scores.

### See the code handed in for the exam
To see the code we handed in for the exam, go to the *Switch branches/tags* button in GitHub and select the tag "exam-version".

# Acknowledgements
Many thanks to **AdamMc331** for their work with the Jetpack Compose library we heavily relied on. You can find the repository here: https://github.com/AdamMc331/ComposeTournamentBracket.
