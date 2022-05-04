package pizzafinder.player;

import gridgames.display.Display;
import lejos.robotics.navigation.Move;
import pizzafinder.data.Percept;
import pizzafinder.data.PizzaFinderAction;
import pizzafinder.data.PizzaFinderItem;
import gridgames.data.action.Action;
import gridgames.data.Direction;
import gridgames.data.action.MoveAction;
import gridgames.grid.Cell;

import java.util.*;

public class ModelBasedReflexPlayer extends PizzaFinderPlayer {
	
	private ArrayList<ArrayList<Cell>> knownCells;

    public ModelBasedReflexPlayer(List<Action> actions, Display display, Cell initialCell, Direction facingDirection) {
    	super(actions, display, initialCell, facingDirection);
        this.knownCells = new ArrayList<>();
    }

    public Action getAction() {

    	Cell currentCell = getCell();
		List<Percept> percepts = getPercepts();
		Action[] moveActions = new Action[0];
		Random r = new Random();


		addVisitedCell(getCell());

		//if you smell the pizza eat it
		if(percepts.contains(Percept.SMELL)) {
			return PizzaFinderAction.EAT;
		}

		// if you bump into a wall, update board and visit unvisited neighbor
		// cell, otherwise go a random direction that you aren't facing and doesn't
		// have a known boundary
		else if(percepts.contains(Percept.BUMP)) {
			addWallCell(getFacingDirection());
			if(getUnexploredBumplessMoves(getFacingDirection()) != null) {
				moveActions = getUnexploredBumplessMoves(getFacingDirection());
			}
			if(moveActions.length > 0) {
				return moveActions[r.nextInt(moveActions.length)];
			} else {
				moveActions = getWalllessBumplessMoves(getFacingDirection());
				return moveActions[r.nextInt(moveActions.length)];
			}
		}

		else {
			if(getNewMoves() != null) {
				moveActions = getNewMoves();
			} else {
				moveActions = getNonWallMoves();
			}
			return moveActions[r.nextInt(moveActions.length)];
		}


    }

	/**
	 * Random direction that simply isn't a wall or known boundary
	 * @return
	 */
	private Action[] getNonWallMoves() {
		System.out.println("Trying to find any move without a wall");
    	ArrayList moveActions = new ArrayList<Action>();
    	Action[] toReturn;

    	if(getCell().getRow() > 0) {
    		if(getKnownCell(getCell().getRow() - 1, getCell().getCol()) != null) {
    			if(!(getKnownCell(getCell().getRow() - 1, getCell().getCol()).contains(PizzaFinderItem.WALL))) {
					moveActions.add(MoveAction.UP);
				}
    		} else {
    			moveActions.add(MoveAction.UP);
			}
		}

    	if(getKnownCell(getCell().getRow(), getCell().getCol() + 1) == null) {
    		moveActions.add(MoveAction.RIGHT);
		} else {
    		if(!(getKnownCell(getCell().getRow(), getCell().getCol() + 1).contains(PizzaFinderItem.WALL))) {
    			moveActions.add(MoveAction.RIGHT);
    		}
		}

		if(getKnownCell(getCell().getRow() + 1, getCell().getCol()) == null) {
			moveActions.add(MoveAction.DOWN);
		} else {
			if(!(getKnownCell(getCell().getRow() + 1, getCell().getCol()).contains(PizzaFinderItem.WALL))) {
				moveActions.add(MoveAction.DOWN);
			}
		}

		if(getCell().getCol() > 0) {
			if(getKnownCell(getCell().getRow(), getCell().getCol() - 1) != null) {
				if (!(getKnownCell(getCell().getRow(), getCell().getCol() - 1).contains(PizzaFinderItem.WALL))) {
					moveActions.add(MoveAction.LEFT);
				}
			} else {
				moveActions.add(MoveAction.LEFT);
			}
		}

		toReturn = new Action[moveActions.size()];
		for(int i=0; i< moveActions.size(); i++) {
			toReturn[i] = (Action) moveActions.get(i);
		}
		System.out.println(moveActions);
		return toReturn;
	}

	private Action[] getNewMoves() {
		System.out.println("Trying to find new moves after not bumping");
		ArrayList moveActions = new ArrayList<Action>();
		Action[] toReturn;

			if ((getCell().getRow() > 0)) {
				if(getKnownCell(getCell().getRow() - 1, getCell().getCol()) == null) {
					moveActions.add(MoveAction.UP);
				} else if (!getKnownCell(getCell().getRow() - 1, getCell().getCol()).wasVisited()) {
					if (!getKnownCell(getCell().getRow() - 1, getCell().getCol()).contains(PizzaFinderItem.WALL)) {
						moveActions.add(MoveAction.UP);
					}
				}
			}

			if(getKnownCell(getCell().getRow(), getCell().getCol() + 1) != null) {
				if(!getKnownCell(getCell().getRow(), getCell().getCol() + 1).wasVisited()) {
					if (!getKnownCell(getCell().getRow(), getCell().getCol() + 1).contains(PizzaFinderItem.WALL)) {
						moveActions.add(MoveAction.RIGHT);
					}
				}
			} else {
				moveActions.add(MoveAction.RIGHT);
			}

			if(getKnownCell(getCell().getRow() + 1, getCell().getCol()) != null) {
				if(!getKnownCell(getCell().getRow() + 1, getCell().getCol()).wasVisited()) {
					if (!getKnownCell(getCell().getRow() + 1, getCell().getCol()).contains(PizzaFinderItem.WALL)) {
						moveActions.add(MoveAction.DOWN);
					}
				}
			} else {
				moveActions.add(MoveAction.DOWN);
			}

			if ((getCell().getCol() > 0)) {
				if(getKnownCell(getCell().getRow(), getCell().getCol() - 1) == null) {
					moveActions.add(MoveAction.LEFT);
				} else if (!getKnownCell(getCell().getRow(), getCell().getCol() - 1).wasVisited()) {
					if (!getKnownCell(getCell().getRow(), getCell().getCol() - 1).contains(PizzaFinderItem.WALL)) {
						moveActions.add(MoveAction.LEFT);
					}
				}
			}


		toReturn = new Action[moveActions.size()];
		for(int i=0; i< moveActions.size(); i++) {
			toReturn[i] = (Action) moveActions.get(i);
		}
		if(toReturn.length == 0) {
			return null;
		}
		return toReturn;
	}

	private void addVisitedCell(Cell cell) {
		addVisitedCell(cell.getRow(), cell.getCol());
	}
	
	private void addVisitedCell(int row, int col) {
		if(row < 0 || col < 0) {
			return;
		}

		//add appropriate number of rows if needed (should be 1 at most)
		while(knownCells.size() <= row) {
			knownCells.add(new ArrayList<>());
		}
		
		ArrayList<Cell> cellRow = knownCells.get(row);
		//if cell has already been recognized
		if(col < cellRow.size()) {
			cellRow.get(col).setVisited(true);
		}
		//if cell has yet to be recognized
		else {
			//add appropriate number of columns if needed
			while(cellRow.size() < col) {
				cellRow.add(new Cell(row, cellRow.size()));
			}
			Cell cell = new Cell(row, col);
			cell.setVisited(true);
			cellRow.add(cell);
		}
	}
	
	private Cell getKnownCell(int row, int col) {
		if(row >= 0 && row < knownCells.size()) {
			ArrayList<Cell> cellRow = knownCells.get(row);
			if(col >= 0 && col < cellRow.size()) {
				return cellRow.get(col);
			}
		}
		return null;
	}
    
    private void addWallCell(Direction facingDirection) {
		int wallRow = getWallRow(facingDirection);
		int wallCol = getWallCol(facingDirection);
		if(wallRow >=0 && wallCol >= 0) {
			addWallCell(wallRow, wallCol);
		}
    }
    
    private void addWallCell(int row, int col) {
		if(row < 0 || col < 0) {
			return;
		}

		//add appropriate number of rows if needed (should be 1 at most)
		while(knownCells.size() <= row) {
			knownCells.add(new ArrayList<>());
		}
		
		ArrayList<Cell> cellRow = knownCells.get(row);
		//if cell has already been recognized
		if(col < cellRow.size()) {
			cellRow.get(col).add(PizzaFinderItem.WALL);
		}
		//if cell has yet to be recognized
		else {
			//add appropriate number of columns if needed
			while(cellRow.size() < col) {
				cellRow.add(new Cell(row, cellRow.size()));
			}
			Cell cell = new Cell(row, col);
			cell.add(PizzaFinderItem.WALL);
			cellRow.add(cell);
		}
	}
    
    private int getWallRow(Direction facingDirection) {
    	int curentRow = getCell().getRow();
    	if(Direction.UP.equals(facingDirection)) {
    		return curentRow - 1;
    	} else if(Direction.DOWN.equals(facingDirection)) {
    		return curentRow + 1;
    	} else {
    		return curentRow;
    	}
    }
    
    private int getWallCol(Direction facingDirection) {
    	int currentCol = getCell().getCol();
    	if(Direction.LEFT.equals(facingDirection)) {
    		return currentCol - 1;
    	} else if(Direction.RIGHT.equals(facingDirection)) {
    		return currentCol + 1;
    	} else {
    		return currentCol;
    	}
    }

	/**
	 * Used after bumping to look for possible moves that have not been explored
	 *
	 * @param facingDirection
	 * @return
	 */
	private Action[] getUnexploredBumplessMoves(Direction facingDirection) {
		System.out.println("Trying to find unexplored moves after bumping");
		ArrayList moveActions = new ArrayList<Action>();
		Action[] toReturn;

		if ((getCell().getRow() > 0)) {
			if (getKnownCell(getCell().getRow() - 1, getCell().getCol()) != null) {
				if (!(getKnownCell(getCell().getRow() - 1, getCell().getCol()).wasVisited())) {
					if (!getKnownCell(getCell().getRow() - 1, getCell().getCol()).contains(PizzaFinderItem.WALL)) {
						moveActions.add(MoveAction.UP);
					}
				}
			} else {
				moveActions.add(MoveAction.UP);
			}
		}

		if(getKnownCell(getCell().getRow(), getCell().getCol() + 1) != null) {
			if(!getKnownCell(getCell().getRow(), getCell().getCol() + 1).wasVisited()) {
				if (!getKnownCell(getCell().getRow(), getCell().getCol() + 1).contains(PizzaFinderItem.WALL)) {
					moveActions.add(MoveAction.RIGHT);
				}
			}
		} else {
			moveActions.add(MoveAction.RIGHT);
		}

		if(getKnownCell(getCell().getRow() + 1, getCell().getCol()) != null) {
			if(!getKnownCell(getCell().getRow() + 1, getCell().getCol()).wasVisited()) {
				if (!getKnownCell(getCell().getRow() + 1, getCell().getCol()).contains(PizzaFinderItem.WALL)) {
					moveActions.add(MoveAction.DOWN);
				}
			}
		} else {
			moveActions.add(MoveAction.DOWN);
		}


		if ((getCell().getCol() > 0)) {
			if(getKnownCell(getCell().getRow(), getCell().getCol() - 1) != null) {
				if (!getKnownCell(getCell().getRow(), getCell().getCol() - 1).wasVisited()) {
					if (!getKnownCell(getCell().getRow(), getCell().getCol() - 1).contains(PizzaFinderItem.WALL)) {
						moveActions.add(MoveAction.LEFT);
					}
				}
			} else {
				moveActions.add(MoveAction.LEFT);
			}
		}

		toReturn = new Action[moveActions.size()];
		for(int i=0; i< moveActions.size(); i++) {
			toReturn[i] = (Action) moveActions.get(i);
		}
		return toReturn;
	}

	/**
	 * Used after bumping getting the possible directions that do not have walls
	 * and are not the direction you just bumped into. Used if there are no unexplored
	 * possible moves found by getUnexploredBumplessMoves
	 * @param facingDirection
	 * @return possible directions
	 */
	private Action[] getWalllessBumplessMoves(Direction facingDirection) {
		System.out.println("Trying to find walless moves after bumping");
		ArrayList moveActions = new ArrayList<Action>();
		Action[] toReturn;

		System.out.println("Cell is "+getCell().getRow() + " "+getCell().getCol());

		if(getCell().getRow() > 0) {
			if(getKnownCell(getCell().getRow() - 1, getCell().getCol()) != null) {
				if (!(getKnownCell(getCell().getRow() - 1, getCell().getCol()).contains(PizzaFinderItem.WALL))) {
					moveActions.add(MoveAction.UP);
				}
			} else {
				moveActions.add(MoveAction.UP);
			}
		}

		if(getKnownCell(getCell().getRow(), getCell().getCol() + 1) != null) {
			if (!(getKnownCell(getCell().getRow(), getCell().getCol() + 1).contains(PizzaFinderItem.WALL))) {
				moveActions.add(MoveAction.RIGHT);
			}
		} else {
			moveActions.add(MoveAction.RIGHT);
		}

		if(getKnownCell(getCell().getRow() + 1, getCell().getCol()) != null) {
			if (!(getKnownCell(getCell().getRow() + 1, getCell().getCol()).contains(PizzaFinderItem.WALL))) {
				moveActions.add(MoveAction.DOWN);
			}
		} else {
			moveActions.add(MoveAction.DOWN);
		}

		if(getCell().getCol() > 0) {
			if(getKnownCell(getCell().getRow(), getCell().getCol() - 1) != null) {
				if(!(getKnownCell(getCell().getRow(), getCell().getCol() - 1).contains(PizzaFinderItem.WALL))) {
						moveActions.add(MoveAction.LEFT);
				}
			} else {
				moveActions.add(MoveAction.LEFT);
			}
		}

		System.out.println(moveActions);
		toReturn = new Action[moveActions.size()];
		for(int i=0; i< moveActions.size(); i++) {
			toReturn[i] = (Action) moveActions.get(i);
		}

		return toReturn;
	}
}