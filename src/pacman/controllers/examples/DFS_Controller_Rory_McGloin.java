package pacman.controllers.examples;

import pacman.controllers.Controller;
import pacman.game.Constants.MOVE;
import pacman.game.Game;

import static pacman.game.Constants.*;

public class DFS_Controller_Rory_McGloin extends Controller<MOVE> {
	
	public static StarterGhosts ghosts = new StarterGhosts();

	@Override
	public MOVE getMove(Game game, long timeDue) {
		// set BFS depth
		int depth = 4;
		
		// Get last move made, by default keep moving in last direction
		int highScore = -1;
		MOVE highMOVE = game.getPacmanLastMoveMade();
		
		// Execute a depth first search, saving the first move that go us to the highest score
		for (MOVE m : MOVE.values()) {
			// Make a copy of game and create temporary holder for highScore
			int tempScore;
			Game copy = game.copy();
			
			// Save the location of pacman before we try and make a move to recognize commands that don't actually move pacman
			int location = copy.getPacmanCurrentNodeIndex();
			copy.advanceGame(m, ghosts.getMove(copy, timeDue));
			
			// If pacman has not moved this move is not helpful and we discount it
			if (copy.getPacmanCurrentNodeIndex() == location)
				tempScore = -1;
			else
				tempScore = depthFirst(copy, depth - 1, timeDue);
			
			// If the new score is higher save it, or if its the same but keeps us moving in the same direction
			if (tempScore > highScore || (tempScore >= highScore && m.equals(game.getPacmanLastMoveMade()))) {
				highScore = tempScore;
				highMOVE = m;
			}
		}
		return highMOVE;
	}
	
	// Depth first search, iterates through moves each time fully exploring the new move before looking at the others
	public int depthFirst(Game game, int depth, long timeDue) {
		// return the current score if we are at the end of the depth
		if (depth == 0) {
			return game.getScore();
		}
		int highScore = 0;
		for (MOVE m : MOVE.values()) {
			Game copy = game.copy();
			copy.advanceGame(m, ghosts.getMove(copy, timeDue));
			int tempScore;
			// If the move we made killed us, discount it
			if (copy.gameOver())
				tempScore = -1;
			else
				tempScore = depthFirst(copy, depth - 1, timeDue);
			if (tempScore > highScore) 
				highScore = tempScore;
		}
		return highScore;
	}

}
