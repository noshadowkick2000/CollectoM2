package server;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import game.Board;
import game.COLOUR;
import game.Collecto;
import util.Communications;

public class CollectoServerGame extends Collecto {

	private List<CollectoClientHandler> playingClients = new ArrayList<CollectoClientHandler>();
	private int currentPlayer = -1;

	private int gameId;

	public CollectoServerGame(int gameId, CollectoClientHandler p1, CollectoClientHandler p2) {
		this.gameId = gameId;

		playingClients.add(p1);
		playingClients.add(p2);

		board = new Board();
	}

	synchronized public void requestMove(int[] move, CollectoClientHandler player) {
		try {
			if (playingClients.get(currentPlayer).equals(player)) {
				List<COLOUR> wonBalls = makeMove(move);
				if (wonBalls != null) {
					switchPlayer();
					player.giveBalls(wonBalls);
					playingClients.get(0).sendMove(move);
					playingClients.get(1).sendMove(move);
					showMessage("Move " + moveToString(move) + "played by " + player.getName());
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

	synchronized public void disconnectClient(CollectoClientHandler client) {
		showMessage("Client " + client.getName() + " disconnected");
		playingClients.remove(client);
		endGame(Condition.DISCONNECT);
	}

	private void switchPlayer() {
		currentPlayer = (currentPlayer == 0) ? 1 : 0;
	}

	private String moveToString(int[] move) {
		String returnString = "";
		for (int m : move) {
			returnString += m + " ";
		}
		return returnString;
	}

	private void showMessage(String msg) {
		CollectoServer.showMessage("Game " + gameId + ": " + msg);
	}

	synchronized public void startGame() {

		CollectoClientHandler playerOne = playingClients.get(0);
		CollectoClientHandler playerTwo = playingClients.get(1);

		playerOne.startGame(board.toCommunicationString(), playerOne.getName(), playerTwo.getName(), this);
		playerTwo.startGame(board.toCommunicationString(), playerOne.getName(), playerTwo.getName(), this);

		currentPlayer = 0;
	}

	synchronized public void endGame(Condition condition) {

		if (condition.equals(Condition.DISCONNECT)) {
			try {
				playingClients.get(0).endGame(Communications.GO + Communications.DELIM + Communications.DISCONNECT
						+ Communications.DELIM + playingClients.get(0).getName());
			} catch (IndexOutOfBoundsException e) {
				showMessage("both clients disconnected");
			}
		} else {
			CollectoClientHandler playerOne = playingClients.get(0);
			CollectoClientHandler playerTwo = playingClients.get(1);

			switch (condition) {
			case VICTORY_PLAYER_ONE:
				playerOne.endGame(
						Communications.GO + Communications.DELIM + Communications.VICTORY + Communications.DELIM + playerOne.getName());
				playerTwo.endGame(
						Communications.GO + Communications.DELIM + Communications.VICTORY + Communications.DELIM + playerOne.getName());
				break;
			case VICTORY_PLAYER_TWO:
				playerOne.endGame(
						Communications.GO + Communications.DELIM + Communications.VICTORY + Communications.DELIM + playerTwo.getName());
				playerTwo.endGame(
						Communications.GO + Communications.DELIM + Communications.VICTORY + Communications.DELIM + playerTwo.getName());
				break;
			case DRAW:
				playerOne.endGame(Communications.GO + Communications.DELIM + Communications.DRAW);
				playerTwo.endGame(Communications.GO + Communications.DELIM + Communications.DRAW);
				break;
			default:
				break;
			}

		}

		playingClients.clear();
		currentPlayer = -1;
		
		showMessage("game ended");
	}

	public void getWinner() {

		int playerOnePoints = playingClients.get(0).countPoints();
		int playerTwoPoints = playingClients.get(1).countPoints();

		if (playerOnePoints == playerTwoPoints) {
			endGame(Condition.DRAW);
			showMessage("Draw with each " + playerOnePoints + " points");
			return;
		} else if (playerOnePoints > playerTwoPoints) {
			endGame(Condition.VICTORY_PLAYER_ONE);
			showMessage("Player one won with " + playerOnePoints + " points");
			return;
		} else {
			showMessage("Player two won with " + playerTwoPoints + " points");
			endGame(Condition.VICTORY_PLAYER_TWO);
		}
	}
}
