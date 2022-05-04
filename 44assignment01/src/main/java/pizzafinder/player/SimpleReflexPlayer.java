package pizzafinder.player;

import gridgames.data.Direction;
import gridgames.data.action.MoveAction;
import gridgames.display.Display;
import gridgames.data.action.Action;
import gridgames.grid.Cell;
import pizzafinder.data.Percept;
import pizzafinder.data.PizzaFinderAction;

import java.util.List;
import java.util.Random;

public class SimpleReflexPlayer extends PizzaFinderPlayer {

	public SimpleReflexPlayer(List<Action> actions, Display display, Cell initialCell, Direction facingDirection) {
		super(actions, display, initialCell, facingDirection);
	}

	public Action getAction() {
		List<Percept> percepts = getPercepts();
		Action[] moveActions;
		Random r = new Random();

		//if you smell the pizza eat it
		if(percepts.contains(Percept.SMELL)) {
			return PizzaFinderAction.EAT;
		}

		//otherwise if you feel a wall go another direction
		else if(percepts.contains(Percept.BUMP)) {
			moveActions = getNonBumpMoves(getFacingDirection());
			return moveActions[r.nextInt(moveActions.length)];
		}
		//otherwise, go in some random direction
		else {
			moveActions = MoveAction.MOVE_ACTIONS;
			return moveActions[r.nextInt(moveActions.length)];
		}
	}


	private Action[] getNonBumpMoves(Direction facingDirection) {
		Action[] moveActions = new Action[3];
		int i=0;
		if(!Direction.UP.equals(facingDirection)) {
			moveActions[i] = MoveAction.UP;
			i++;
		}
		if(!Direction.RIGHT.equals(facingDirection)) {
			moveActions[i] = MoveAction.RIGHT;
			i++;
		}
		if(!Direction.DOWN.equals(facingDirection)) {
			moveActions[i] = MoveAction.DOWN;
			i++;
		}
		if(!Direction.LEFT.equals(facingDirection)) {
			moveActions[i] = MoveAction.LEFT;
			i++;
		}
		return moveActions;
	}




}
