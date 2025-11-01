# CHESS_ENGINE

**Targets**

1.Initial Design Based on the string based move triggers [Accomplished]

-> Clone the Repository to your local Machine <br>
-> cd to the Directory of the Package folder <br>

> javac -d class src/chessGameEngine/\*.java <br>
> java -cp class chessGameEngine.Test <br>

->Or to just run the .jar file of the package[Recommended]<br>

> java -jar ./class/chessGameEngine.jar<br>

->To create a JAR file <br>

> jar cvfm chessGameEngine.jar manifest.txt chessGameEngine<br>

[The manifest.txt contains the class which contains main method]<br>

> just Type **from** and **to** Positions<br> [ fromCol[a-h] fromRow[1-8] toCol[a-h] toRow[1-8] ]<br>

    (eg) e2e4,a7a6....

2.UI plugin <br>
-> Initally Stage to just Display the board,moves and Turn.<br>
![GUI](Game_Visual_Stage2.png)
3.Developing Reinforcement Learning model for the Bot.<br>
->Stockfish Engine has been used for BestMoves ,the custom model could be implemented but the perfermance wouldn't be as much as Stockfish advanced humongous model but still an exciting work to try implement one.<br>
->The custom model would follow the UCI guidelines for communication with GameEngine as followed by current Engine being used
