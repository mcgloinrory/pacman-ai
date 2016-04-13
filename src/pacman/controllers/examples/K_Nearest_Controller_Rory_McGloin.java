package pacman.controllers.examples;

import pacman.controllers.Controller;
import pacman.game.Constants.MOVE;
import pacman.game.Game;

public class K_Nearest_Controller_Rory_McGloin extends Controller<MOVE> {
	
	// We set the K value
	public int K = 1;
	
	// default ghosts
	public static StarterGhosts ghosts = new StarterGhosts();

	public MOVE getMove(Game game, long timeDue) {
		// acquire training data, in this case the distance between our current score and the potential score
		int[] data = this.getTrainingData(game, timeDue);
		
		// using the training data find the K number of moves that are closest to our current score
		MOVE[] k_nearest_moves = this.getKNearest(data);
		
		// find the move that occurs the most of the K close ones and return it
		MOVE most_popular_move = this.getPopularMove(k_nearest_moves);
		return most_popular_move;
	}
	
	// acquire training data, in this case the distance between our current score and the potential score
	private int[] getTrainingData(Game game, long timeDue) {
		
		// get score differential for pursuing each of the five possible moves and add them to an array
		int[] data = new int[5];
		for (int i = 0; i < 5; i++) {
			MOVE move = MOVE.values()[i];
			Game copy = game.copy();
			copy.advanceGame(move, ghosts.getMove(copy, timeDue));
			copy.advanceGame(move, ghosts.getMove(copy, timeDue));
			copy.advanceGame(move, ghosts.getMove(copy, timeDue));
			copy.advanceGame(move, ghosts.getMove(copy, timeDue));
			data[i] = copy.getScore() - game.getScore();
		}
		return data;
	}
	
	// using the training data find the K number of moves that are closest to our current score
	private MOVE[] getKNearest(int[] data) {
		
		// look at the score differentials and take the K closest to 0
		MOVE[] moves = new MOVE[this.K];
		for (int i = 0; i < this.K; i++) {
			int nearest_index = 0;
			int nearest_value = -1;
			for (int j = 0; j < data.length; j++) {
				if ((data[j] >= 0 && data[j] < nearest_value) || nearest_value < 0) {
					nearest_index = j;
					nearest_value = data[j];
				}
			}
			// save the move with the nearest value and remove it from the next search
			data[nearest_index] = -1;
			moves[i] = MOVE.values()[nearest_index];
		}
		return moves;
	}
	
	// find the move that occurs the most of the K close ones and return it
	private MOVE getPopularMove(MOVE[] k_nearest_moves) {
		int neutral_counter = 0;
		int up_counter = 0;
		int down_counter = 0;
		int left_counter = 0;
		int right_counter = 0;
		
		// count how often each move occurs in the k closest
		for (int i = 0; i < k_nearest_moves.length; i++) {
			MOVE curr_move = k_nearest_moves[i];
			if (curr_move == MOVE.NEUTRAL)
				neutral_counter++;
			else if (curr_move == MOVE.UP)
				up_counter++;
			else if (curr_move == MOVE.DOWN)
				down_counter++;
			else if (curr_move == MOVE.LEFT)
				left_counter++;
			else if (curr_move == MOVE.RIGHT)
				right_counter++;
		}
		
		// return whichever move occurs the most in the k closest
		if (up_counter >= down_counter && up_counter >= left_counter && up_counter >= right_counter && up_counter >= neutral_counter)
			return MOVE.UP;
		else if (down_counter >= up_counter && down_counter >= left_counter && down_counter >= right_counter && down_counter >= neutral_counter)
			return MOVE.DOWN;
		else if (left_counter >= down_counter && left_counter >= up_counter && left_counter >= right_counter && left_counter >= neutral_counter)
			return MOVE.LEFT;
		else if (left_counter >= down_counter && left_counter >= left_counter && left_counter >= up_counter && left_counter >= neutral_counter)
			return MOVE.RIGHT;
		else
			return MOVE.NEUTRAL;
	}
}
