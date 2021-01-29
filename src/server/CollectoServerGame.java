/*
 * Collecto Network System
 * University of Twente
 * Tim Yeung s2085615
 * January 2021
 */
package server;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import game.Board;
import game.Collecto;
import util.CollectoInterface;
import util.CollectoNetworker;
import util.Communications;

/**
 * The Class CollectoServerGame.
 */
public class CollectoServerGame extends Collecto {

	/**
	 * Enum Condition to more easily communicate the end game condition
	 */
	public enum Condition {

		/** The victory player one. */
		VICTORY_PLAYER_ONE,

		/** The victory player two. */
		VICTORY_PLAYER_TWO,

		/** The disconnect. */
		DISCONNECT,

		/** The draw. */
		DRAW
	}

	/**
	 * The client handlers assigned to this game in the order of player 1 and player
	 * 2.
	 */
	private List<CollectoClientHandler> playingClients = new ArrayList<CollectoClientHandler>();

	/** The game id, which is used to distinguish this game in the console. */
	private int gameId;

	/**
	 * Instantiates a new CollectoServerGame.
	 *
	 * @requires p1 != null, p2 != null.
	 * @ensures this.gameId = gamedId, playingClients.size() == 2,
	 *          playingClients.add(p1), playingClients.add(p2), board = new Board().
	 * @param gameId: the id to assign to gameId.
	 * @param p1:     the CollectoClientHandler assigned as player 1.
	 * @param p2:     the CollectoClientHandler assigned as player 2.
	 */
	public CollectoServerGame(int gameId, CollectoClientHandler p1, CollectoClientHandler p2) {
		this.gameId = gameId;

		playingClients.add(p1);
		playingClients.add(p2);

		board = new Board();
	}

	/**
	 * Removes the passed client handler from the List playingClients and calls the
	 * gameOver() method with a DISCONNECT argument.
	 *
	 * @requires client != null.
	 * @ensures playingClients.remove(client).
	 * @param client: the client handler associated with the disconnected client.
	 */
	synchronized public void disconnectClient(CollectoClientHandler client) {
		showMessage("Client " + client.getName() + " disconnected");
		playingClients.remove(client);
		gameOver(Condition.DISCONNECT);
	}

	/**
	 * Starts a new game by calling the NEWGAME implementation of both client
	 * handlers, passing the order and name of the players and the state of the grid
	 * of the Board.
	 * 
	 * @requires playingClients.size() == 2, for (CollectoClientHandler cch :
	 *           playingClients){cch != null}.
	 */
	synchronized public void newGame() {

		CollectoClientHandler playerOne = playingClients.get(0);
		CollectoClientHandler playerTwo = playingClients.get(1);

		playerOne.newGame(CollectoNetworker.toCommunicationString(board), playerOne.getName(), playerTwo.getName(),
				this);
		playerTwo.newGame(CollectoNetworker.toCommunicationString(board), playerOne.getName(), playerTwo.getName(),
				this);
	}

	/**
	 * Attempts to play the move passed in the parameter move on the board. If
	 * succesful will call MOVE protocol for both client handlers and check whether
	 * game is over. If a move is played out of turn or is invalid, the ERROR
	 * implementation of the client handler who sent the move will be called.
	 *
	 * @requires move != null, playingClients.contains(player), board != null,
	 *           playingClients.size() == 2, for (CollectoClientHandler cch :
	 *           playingClients){cch != null}.
	 * @param move:   the move sent by the client as an Integer array.
	 * @param player: the client handler who requested the move.
	 */
	synchronized public void receiveMove(int[] move, CollectoClientHandler player) {
		try {
			if (playingClients.get(board.firstPlayerTurn ? 0 : 1).equals(player)) {
				if (board.isValidMove(move)) {
					board.makeMove(move);
					playingClients.get(0).sendMove(move);
					playingClients.get(1).sendMove(move);
					showMessage("Move " + Board.moveToReadableString(move) + "played by " + player.getName());
					if (board.noMovesLeft()) {
						getWinner();
					}
				} else {
					showMessage("Illegal move by " + player.getName());
					player.showError("Illegal move");
				}
			} else {
				showMessage("Move requested by non playing player");
				player.showError("Not your move");
			}
		} catch (IOException e) {
			disconnectClient(player);
		} catch (IndexOutOfBoundsException e) {
		}
	}

	/**
	 * Ends the game by calling the appropriate protocol implementations to the
	 * client handlers, depending on the condition passed as a parameter. This
	 * method results in all references to this game to be cleared.
	 *
	 * @requires condition != null, playingClients.size() == 2, for
	 *           (CollectoClientHandler cch : playingClients){cch != null}.
	 * @ensures playingClients.size() == 0.
	 * @param condition: the condition for ending the game as a Condition ENUM.
	 */
	synchronized public void gameOver(Condition condition) {

		if (condition.equals(Condition.DISCONNECT)) {
			try {
				playingClients.get(0).gameOver(Communications.GO + Communications.DELIM + Communications.DISCONNECT
						+ Communications.DELIM + playingClients.get(0).getName());
			} catch (IndexOutOfBoundsException e) {
				showMessage("both clients disconnected");
			}
		} else {
			CollectoClientHandler playerOne = playingClients.get(0);
			CollectoClientHandler playerTwo = playingClients.get(1);

			switch (condition) {
			case VICTORY_PLAYER_ONE:
				playerOne.gameOver(Communications.GO + Communications.DELIM + Communications.VICTORY
						+ Communications.DELIM + playerOne.getName());
				playerTwo.gameOver(Communications.GO + Communications.DELIM + Communications.VICTORY
						+ Communications.DELIM + playerOne.getName());
				break;
			case VICTORY_PLAYER_TWO:
				playerOne.gameOver(Communications.GO + Communications.DELIM + Communications.VICTORY
						+ Communications.DELIM + playerTwo.getName());
				playerTwo.gameOver(Communications.GO + Communications.DELIM + Communications.VICTORY
						+ Communications.DELIM + playerTwo.getName());
				break;
			case DRAW:
				playerOne.gameOver(Communications.GO + Communications.DELIM + Communications.DRAW);
				playerTwo.gameOver(Communications.GO + Communications.DELIM + Communications.DRAW);
				break;
			default:
				break;
			}
		}

		playingClients.clear();

		showMessage("game ended");
	}

	/**
	 * Count the points of both players and call the gameOver() method with the
	 * appropriate Condition based on the amount of points per player.
	 *
	 * @requires board != null.
	 */
	public void getWinner() {

		int playerOnePoints = board.countPoints(true);
		int playerTwoPoints = board.countPoints(false);

		if (playerOnePoints == playerTwoPoints) {
			gameOver(Condition.DRAW);
			showMessage("Draw with each " + playerOnePoints + " points");
			return;
		} else if (playerOnePoints > playerTwoPoints) {
			gameOver(Condition.VICTORY_PLAYER_ONE);
			showMessage("Player one won with " + playerOnePoints + " points");
			return;
		} else {
			showMessage("Player two won with " + playerTwoPoints + " points");
			gameOver(Condition.VICTORY_PLAYER_TWO);
		}
	}

	/**
	 * Show message on the console, prepended by the id of this game in order to
	 * distinguish it in the console.
	 *
	 * @param msg: the message to be printed to the console.
	 */
	private void showMessage(String msg) {
		CollectoInterface.showMessage("Game " + gameId + ": " + msg);
	}
}
