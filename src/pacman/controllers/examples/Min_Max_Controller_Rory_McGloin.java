package pacman.controllers.examples;

import pacman.controllers.Controller;
import pacman.game.Constants.MOVE;
import pacman.game.Game;

public class Min_Max_Controller_Rory_McGloin extends Controller<MOVE> {
	
	public static StarterGhosts ghosts = new StarterGhosts();

	public MOVE getMove(Game game, long timeDue) {
		// set the depth of min_max and start with the maximizing player
		int depth = 4;
		boolean maximizing = true;
		
		// call min_max algorithm
		return this.min_max(game, depth, maximizing, timeDue);
	}
	
	private MOVE min_max(Game game, int depth, boolean maximizing, long timeDue) {
		// if we are out of depth then end recursion
		if (depth == 0)
			return MOVE.NEUTRAL;
		MOVE high_move = MOVE.NEUTRAL;
		
		// if the maximizing player's turn, find the highest local move
		if (maximizing) {
			int high_score = -1;
			for (MOVE m: MOVE.values()) {
				Game copy = game.copy();
				copy.advanceGame(m, ghosts.getMove(game, timeDue));
				copy.advanceGame(m, ghosts.getMove(game, timeDue));
				copy.advanceGame(m, ghosts.getMove(game, timeDue));
				copy.advanceGame(m, ghosts.getMove(game, timeDue));
				if (copy.getScore() > high_score) {
					high_score = copy.getScore();
					high_move = m;
				}
			}
		}
		// if the minimizing player's turn, find the lowest local move
		else {
			int high_score = game.getScore() + 1000;
			for (MOVE m: MOVE.values()) {
				Game copy = game.copy();
				copy.advanceGame(m, ghosts.getMove(game, timeDue));
				copy.advanceGame(m, ghosts.getMove(game, timeDue));
				copy.advanceGame(m, ghosts.getMove(game, timeDue));
				copy.advanceGame(m, ghosts.getMove(game, timeDue));
				if (copy.getScore() < high_score) {
					high_score = copy.getScore();
					high_move = m;
				}
			}
		}
		
		// advance with the chosen move by the current player
		game.advanceGame(high_move, ghosts.getMove(game, timeDue));
		
		// recur, remove one depth and alternate the current player
		this.min_max(game, depth - 1, !maximizing, timeDue);
		
		// return the move this player chose
		return high_move;
	}

}
