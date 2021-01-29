# Networked Collecto Game M2

This project contains the source code and documentation for an implementation of the M2 final programming project. The files themselves can be split up into two separate programs. One server program and one client program. The only library used for this project is the JUNIT library, which you should include yourself to run the JUNIT tests.


# Installation

Import the archived code base into the IDE of you choice and export two runnable JARs from the project using your IDE's export function. The Main class of the first JAR should be **src.client.CollectoClient** and the Main class of the second JAR should be **src.server.CollectoServer**.  When given the options for Library handling, choose to extract the required libraries into the generated JAR. You can start either of the programs by opening you command prompt and navigating to the folder where you exported the JAR. From there you can start the programs using **java -jar <program_name>.jar**

## Client program

After starting the client program, you will be asked to input the host ip adress and the port in that. Make sure to use a space inbetween the two. Each time you have written something on the console, press enter to submit and continue the program. Also note that each time you are asked for input, a "**:**" will be printed at the start of the console. You will then be asked to pick a type of player for the client. A human player will allow you to play the games yourself, while AI players only allow you to use commands while in the lobby. Note that when you are prompted to make a choice from options, you should input the number, corresponding to the choice. Once you have picked the type of player. Input the description for this server and after that the username you will use. If the username has already been taken you will be prompted again. Once you have submitted your username, the server will let you know that you are in the lobby and show you the commands you can now use. If you have used the QUEUE command, you will be entering matchmaking, where the server will attempt to set you up with another player. Once a game has been found, you will be prompted to press enter to start the match. If you are a human player, you will be able to play moves by typing the number corresponding to your desired move, or two numbers with a space inbetween if you have to make a double move. If you cannot see any moves to play, you can type **hint** to get a list of moves that can be played. If you have chosen a computer player, the client will automatically play the moves. When a game is done, you will see the outcome on the console and be sent back to the lobby, where you can once again use console commands.

## Server program

After starting the server program, you will be prompted to choose a port to host the game on. Type the host into the console to set the server description and press enter. After that, you will be prompted to type in the description of the server. Once this has been entered, the server will automatically start and connect with any incoming clients. You can see the actions being performed by the client and server in the console. If you wish to close the server, simply press enter.
 
