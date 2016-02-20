package pacman.controllers.examples;

import pacman.controllers.Controller;
import pacman.game.Constants.MOVE;
import pacman.game.Game;

public class Hill_Climber_Controller_Rory_McGloin extends Controller<MOVE> {
	
	public static StarterGhosts ghosts = new StarterGhosts();

	@Override
	public MOVE getMove(Game game, long timeDue) {
		// choose how far we want to search with the hill climber at most
		int depth = 4;
		
		// call the hill climber algorithm and use the first move found
		return this.hillClimb(game, depth, timeDue);
	}
	
	// hill climber algorithm
	public MOVE hillClimb(Game game, int depth, long timeDue) {
		int high_score = game.getScore();
		MOVE high_move = game.getPacmanLastMoveMade();
		Game high_copy = null;
		
		// keep track of lives and location to check for deaths and stationary movements
		int lives = game.getPacmanNumberOfLivesRemaining();
		int location = game.getPacmanCurrentNodeIndex();
		
		// check if keeping on the current move will keep us in place or kill us, if so set current score to -1 to be replaced
		Game temp_copy = game.copy();
		temp_copy.advanceGame(high_move, ghosts.getMove(temp_copy, timeDue));
		if (location == temp_copy.getPacmanCurrentNodeIndex() || temp_copy.wasPacManEaten())
			high_score = -1;
		
		// iterate through the neighbors searching for a "higher ground"
		for (MOVE m: MOVE.values()) {
			Game copy = game.copy();
			
			// need to advance the game 4 times to make sure we pick up a higher score
			copy.advanceGame(m, ghosts.getMove(copy, timeDue));
			copy.advanceGame(m, ghosts.getMove(copy, timeDue));
			copy.advanceGame(m, ghosts.getMove(copy, timeDue));
			copy.advanceGame(m, ghosts.getMove(copy, timeDue));
			int temp_score = copy.getScore();
			
			// check if pacman has been eaten and if so invalidate this path
			if (copy.wasPacManEaten() || copy.getPacmanNumberOfLivesRemaining() < lives)
				temp_score = -1;
			
			// check if pacman is at the same location he started in and invalidate the move
			if (copy.getPacmanCurrentNodeIndex() == location)
				temp_score = -1;
			
			// if we find a higher score save it as our preference
			if (temp_score > high_score) {
				high_score = temp_score;
				high_move = m;
				high_copy = copy;
			}
		}
		
		// if one our neighbors is higher then our current spot in score then we "climb" to it and recur
		if (high_score > game.getScore()) {
			// we perform the hillclimb search but it won't actually impact our next move so we don't return it
			this.hillClimb(high_copy, depth - 1, timeDue);
		}
		// return the locally optimal step from this juncture
		return high_move;
	}

}
