package pizzafinder.grid;

import java.util.ArrayList;
import java.util.List;

import gridgames.data.action.Action;
import gridgames.data.Direction;
import gridgames.data.action.MoveAction;
import pizzafinder.data.PizzaFinderItem;
import gridgames.grid.Board;
import gridgames.grid.Cell;

public class PizzaFinderBoard extends Board {

	public PizzaFinderBoard(int numRows, int numCols) {
		super(numRows, numCols);
	}
	
	@Override
	public List<Action> getValidMoveActions() {
        List<Action> moves = new ArrayList<Action>();
        Cell playerCell = getPlayerCell();
        int row = playerCell.getRow();
        int col = playerCell.getCol();

        if(row>0 && !this.cells[row-1][col].contains(PizzaFinderItem.WALL)) {
            moves.add(MoveAction.UP);
        }
        if(col<this.numCols-1 && !this.cells[row][col+1].contains(PizzaFinderItem.WALL)) {
            moves.add(MoveAction.RIGHT);
        }
        if(row<this.numRows-1 && !this.cells[row+1][col].contains(PizzaFinderItem.WALL)) {
            moves.add(MoveAction.DOWN);
        }
        if(col>0 && !this.cells[row][col-1].contains(PizzaFinderItem.WALL)) {
            moves.add(MoveAction.LEFT);
        }
        return moves;
    }
    
    public boolean isPlayerFacingWall(Direction facingDirection) {
    	Cell playerCell = getPlayerCell();
        int row = playerCell.getRow();
        int col = playerCell.getCol();
        
        if(Direction.UP.equals(facingDirection) && (row==0 || this.cells[row-1][col].contains(PizzaFinderItem.WALL))) {
        	return true;
        } else if(Direction.RIGHT.equals(facingDirection) && (col==this.numCols-1 || this.cells[row][col+1].contains(PizzaFinderItem.WALL))) {
        	return true;
        } else if(Direction.DOWN.equals(facingDirection) && (row==this.numRows-1 || this.cells[row+1][col].contains(PizzaFinderItem.WALL))) {
        	return true;
        } else if(Direction.LEFT.equals(facingDirection) && (col==0 || this.cells[row][col-1].contains(PizzaFinderItem.WALL))) {
        	return true;
        }
        return false;
    }

}
