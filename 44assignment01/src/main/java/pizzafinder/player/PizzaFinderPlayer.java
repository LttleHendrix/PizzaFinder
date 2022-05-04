package pizzafinder.player;

import java.util.ArrayList;
import java.util.List;

import gridgames.data.Direction;
import gridgames.data.action.Action;
import gridgames.display.Display;
import gridgames.grid.Cell;
import gridgames.player.Player;
import pizzafinder.data.Percept;

public class PizzaFinderPlayer extends Player {
	
	private List<Percept> percepts;
	private Direction facingDirection;

	public PizzaFinderPlayer(List<Action> actions, Display display, Cell initialCell, Direction facingDirection) {
		super(actions, display, initialCell);
		this.facingDirection = facingDirection;
		this.percepts = new ArrayList<Percept>();
	}
	
	public List<Percept> getPercepts() {
		return this.percepts;
	}
	
	public void setPercepts(List<Percept> percepts) {
		this.percepts = percepts;
	}
	
	public Direction getFacingDirection() {
		return this.facingDirection;
	}
	
	public void setFacingDirection(Direction facingDirection) {
		this.facingDirection = facingDirection;
	}

	@Override
	public Action getAction() {
		return null;
	}
}
