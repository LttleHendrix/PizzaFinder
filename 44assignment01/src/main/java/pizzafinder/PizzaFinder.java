package pizzafinder;

import gridgames.data.Direction;
import gridgames.data.action.MoveAction;
import gridgames.data.item.MoveItem;
import gridgames.display.Display;
import gridgames.game.Game;
import pizzafinder.player.PizzaFinderPlayer;
import gridgames.data.action.Action;
import pizzafinder.data.Percept;
import pizzafinder.data.PizzaFinderAction;
import pizzafinder.data.PizzaFinderItem;
import pizzafinder.grid.PizzaFinderBoard;
import gridgames.grid.Cell;
import gridgames.player.Player;
import gridgames.player.EV3Player;

import java.util.*;

import lejos.hardware.Button;

public class PizzaFinder extends Game {

    public PizzaFinder(Display display, int numRows, int numCols) {
        this.board = new PizzaFinderBoard(numRows, numCols);
        initializeBoard();
        this.display = display;
        this.display.setBoard(board);
    }

    public Cell getInitialCell() {
        return this.board.getPlayerCell();
    }
    
    public Direction getInitialFacingDirection() {
    	return Direction.RIGHT;
    }

    public void play(Player player) {
    	PizzaFinderPlayer pizzaFinderPlayer = (PizzaFinderPlayer) player.getGamePlayer();
    	boolean isEV3Player = false;
    	boolean isGameOver = false;
    	Cell currentCell;
        Action action;
        
        if(player instanceof EV3Player) {
        	isEV3Player = true;
        }
        
        do {
            currentCell = this.board.getPlayerCell();
            player.setCell(currentCell);
            List<Percept> percepts = getPercepts(currentCell, pizzaFinderPlayer.getFacingDirection());
            pizzaFinderPlayer.setPercepts(percepts);

            display.addMessage("You are facing " + pizzaFinderPlayer.getFacingDirection() + ".");
            for(Percept p : percepts) {
            	display.addMessage(p.getMessage());
    		}
            display.printState(false);
        	action = player.getAction();
        	pizzaFinderPlayer.incrementNumActionsExecuted();
        	isGameOver = isGameOver(action, currentCell);
        	
        	//move player if game isn't over
        	if(!isGameOver && action instanceof MoveAction) {
        		board.movePlayer(action);
        		pizzaFinderPlayer.setFacingDirection(Direction.getDirectionFromMoveAction(action));
        		//move robot
        		if(isEV3Player) {
        			((EV3Player)player).move(pizzaFinderPlayer.getFacingDirection(), action);
        		}
        	}
        } while (!isGameOver);
        
        display.addMessage("You found the pizza in " + pizzaFinderPlayer.getNumActionsExecuted() + " moves.");
        display.printState(true);
        
        if(isEV3Player) {
        	Button.waitForAnyPress();
        }
    }
    
    public void initializeBoard() {
    	placePizzaAndWalls();
        placePlayer();
    }
    
    private List<Percept> getPercepts(Cell currentCell, Direction facingDirection) {
    	List<Percept> percepts = new ArrayList<Percept>();
    	
    	if(currentCell.contains(PizzaFinderItem.PIZZA)) {
    		percepts.add(Percept.SMELL);
    	}
    	if(((PizzaFinderBoard)this.board).isPlayerFacingWall(facingDirection)) {
    		percepts.add(Percept.BUMP);
    	}
    	return percepts;
    }

    private boolean isGameOver(Action action, Cell currentCell) {
        return PizzaFinderAction.EAT.equals(action) && currentCell.contains(PizzaFinderItem.PIZZA);
    }
    
    private void placePlayer() {
    	Cell initialCell = this.board.getCell(0, 0);
    	initialCell.add(MoveItem.PLAYER);
    	initialCell.setVisited(true);
    }
    
    private void placePizzaAndWalls() {
    	int numMovesRemaining = (int)Math.floor(.6*this.board.getNumRows()*this.board.getNumCols());
    	Random r = new Random();
    	List<Action> moves;
    	Action direction;
    	Cell cell;
    	
    	placePlayer();
    	while(numMovesRemaining > 0) {
    		moves = getMovesToUnvisitedCellsIfPossible(this.board.getValidMoveActions());
    		direction = moves.get(r.nextInt(moves.size()));
    		this.board.movePlayer(direction);
    		numMovesRemaining--;
    	}
    	
    	cell = this.board.getPlayerCell();
    	cell.add(PizzaFinderItem.PIZZA);
    	cell.remove(MoveItem.PLAYER);
    	
    	for(int i=0; i<this.board.getNumRows(); i++) {
    		for(int j=0; j<this.board.getNumCols(); j++) {
    			cell = this.board.getCell(i, j);
    			//add walls to unvisited cells
    			if(!cell.wasVisited()) {
    				cell.add(PizzaFinderItem.WALL);
    			}
    			//set visited cells to unvisited
    			else {
    				cell.setVisited(false);
    			}
    		}
    	}
    }
    
    private List<Action> getMovesToUnvisitedCellsIfPossible(List<Action> validMoves) {
        List<Action> moves = new ArrayList<Action>(validMoves);
        Cell currentCell = this.board.getPlayerCell();
        int currentRow = currentCell.getRow();
        int currentCol = currentCell.getCol();

        if(validMoves.contains(MoveAction.UP) && this.board.getCell(currentRow-1, currentCol).wasVisited()) {
           moves.remove(MoveAction.UP);
        }
        if(validMoves.contains(MoveAction.RIGHT) && this.board.getCell(currentRow, currentCol+1).wasVisited()) {
            moves.remove(MoveAction.RIGHT);
        }
        if(validMoves.contains(MoveAction.DOWN) && this.board.getCell(currentRow+1, currentCol).wasVisited()) {
        	moves.remove(MoveAction.DOWN);
        }
        if(validMoves.contains(MoveAction.LEFT) && this.board.getCell(currentRow, currentCol-1).wasVisited()) {
            moves.remove(MoveAction.LEFT);
        }
        if(moves.isEmpty()) {
        	moves = validMoves;
        }
        return moves;
    }
}
