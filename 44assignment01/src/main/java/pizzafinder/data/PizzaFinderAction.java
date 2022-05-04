package pizzafinder.data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import gridgames.data.action.Action;
import gridgames.data.action.MoveAction;

public enum PizzaFinderAction implements Action {
    EAT;

	public static final Action[] ALL_ACTIONS = {MoveAction.UP, MoveAction.RIGHT, MoveAction.DOWN, MoveAction.LEFT, PizzaFinderAction.EAT};
	
	public String getDescription() {
		if(PizzaFinderAction.EAT.equals(this)) {
			return "eat";
		} else {
			return "";
		}
	}
	
	public Action getActionFromDescription(String description) {
		if("eat".equals(description)) {
			return PizzaFinderAction.EAT;
		} else {
			return null;
		}
	}
	
	public boolean isMove() {
		return !PizzaFinderAction.EAT.equals(this);
	}
	
	public static List<Action> getAllActions() {
		return new ArrayList<Action>(Arrays.asList(ALL_ACTIONS));
	}
}
