package pacman.controllers.examples;

import java.util.ArrayList;

import pacman.controllers.Controller;
import pacman.game.Constants.MOVE;
import pacman.game.Game;

public class Final_Controller_Rory_McGloin extends Controller<MOVE> {
	
	// optimizes DFS by keeping track of good moves instead of just returning 1
	public ArrayList<MOVE> move_queue = new ArrayList();
	
	// expected score for doing all moves in queue
	public int expected_score = 0;
	
	// depth for searching we want to search as far as possible without computational problems
	public int depth = 4;

	// default ghosts
	public static StarterGhosts ghosts = new StarterGhosts();
	
	public MOVE getMove(Game game, long timeDue) {
		// if we don't have any moves queued up perform the modified DFS to find a new series of moves
		if (move_queue.isEmpty())
			this.depthFirst(game, this.depth, timeDue, new ArrayList<MOVE>());
		// save the next move we are going to execute
		MOVE nextMove = this.move_queue.get(0);
		// remove the move we are about to execute
		this.move_queue.remove(0);
		// return our next move
		return nextMove;
	}
	
	// modified depth first search, sets priority to avoiding ghosts instead of increasing score
		public void depthFirst(Game game, int depth, long timeDue, ArrayList<MOVE> movesTaken) {
			// if we are at our depth then check to see if the path we have taken is an improvement
			// if it is change the move queue to the moves we took to achieve this
			if (depth == 0) {
				if (game.getScore() >= this.expected_score) {
					this.expected_score = game.getScore();
					this.move_queue = movesTaken;
				}
				return;
			}
			// iterate through the possible moves
			for (MOVE m : MOVE.values()) {
				Game copy = game.copy();
				copy.advanceGame(m, ghosts.getMove(copy, timeDue));
				copy.advanceGame(m, ghosts.getMove(copy, timeDue));
				copy.advanceGame(m, ghosts.getMove(copy, timeDue));
				// If the move we made killed us or did not move us, discount it. Otherwise recur
				if (!(copy.gameOver() || game.getPacmanNumberOfLivesRemaining() > copy.getPacmanNumberOfLivesRemaining() || game.getPacmanCurrentNodeIndex() == copy.getPacmanCurrentNodeIndex())) {
					ArrayList<MOVE> movesTakenCopy = (ArrayList<MOVE>) movesTaken.clone();
					movesTakenCopy.add(m);
					depthFirst(copy, depth - 1, timeDue, movesTakenCopy);
				}
				// if this move is not good but we still have an empty queue move in the opposite direction as a precaution
				else {
					if (this.move_queue.isEmpty()) {
						this.expected_score = game.getScore();
						this.move_queue = new ArrayList<MOVE>();
						this.move_queue.add(m.opposite());
						this.move_queue.add(m.opposite());
					}
				}
			}
			return;
		}

}
