package server;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import game.Board;
import game.Collecto;
import util.CollectoInterface;
import util.Communications;

public class CollectoServerGame extends Collecto {

	private List<CollectoClientHandler> playingClients = new ArrayList<CollectoClientHandler>();

	private int gameId;

	public CollectoServerGame(int gameId, CollectoClientHandler p1, CollectoClientHandler p2) {
		this.gameId = gameId;

		playingClients.add(p1);
		playingClients.add(p2);

		board = new Board();
	}

	synchronized public void disconnectClient(CollectoClientHandler client) {
		showMessage("Client " + client.getName() + " disconnected");
		playingClients.remove(client);
		gameOver(Condition.DISCONNECT);
	}

	synchronized public void newGame() {

		CollectoClientHandler playerOne = playingClients.get(0);
		CollectoClientHandler playerTwo = playingClients.get(1);

		playerOne.startGame(board.toCommunicationString(), playerOne.getName(), playerTwo.getName(), this);
		playerTwo.startGame(board.toCommunicationString(), playerOne.getName(), playerTwo.getName(), this);
	}
	
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
	
	private void showMessage(String msg) {
		CollectoInterface.showMessage("Game " + gameId + ": " + msg);
	}
}
