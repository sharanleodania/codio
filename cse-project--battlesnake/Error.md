ERRORS

1
Compilation ERROR
had duplicate declarations as for conveniance intially just copied the similar staement pasted and forget to change a key elment.
Fix
removed the incoreect declarations of JsonNode body.

2
Had a expected at line 243 ERROR
found the reaosn was missing semicolon prob
fixed by adding a semicolon at there and also happend anoter time at line 284

3
cannot find sysmbol: variable moveRequest
Multiple cannot find symbol erros having creating a rough verison of avoid others
foudn the problem was my incorrect declarations of moveRequest 
added JsonNode moveRequest to my parameter of avoidOthers() and also updated its call in the move() method

4
Logic erros in the self collision sysmtem 
for the border system the original avoid border i used harcoded to check for the values that i somehow messed up counting +1 so i repalce the hardcorded which was also getting messed up if i try other game mods with automatic updating values
 
 also in this same part the eslef if being in the corner intially method was only checking one dangerous direction and letting snake go outside
 had to replace the simple else if with seperate if statements so that each border condition is checked independently.

 5
 there was the loggical move i forgot to implement now i realize after fixing intially i just finished adding the snake chaser if its small method and then it started dying radomly running out of moves now i know took a good 10 minutes and multiple check 
 to finlly add a final move if thse problem like now i have down if usually works out if it runs out of moves.