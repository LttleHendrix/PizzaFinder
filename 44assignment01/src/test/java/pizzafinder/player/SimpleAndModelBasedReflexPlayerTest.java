package pizzafinder.player;

import static org.junit.jupiter.api.Assertions.*;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

import gridgames.grid.Cell;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import gridgames.data.action.Action;
import gridgames.data.Direction;
import gridgames.data.action.MoveAction;
import gridgames.data.item.MoveItem;
import gridgames.display.ConsoleDisplay;
import pizzafinder.data.Percept;
import pizzafinder.data.PizzaFinderAction;
import gridgames.grid.Board;
import pizzafinder.data.PizzaFinderItem;
import pizzafinder.grid.PizzaFinderBoard;

public class SimpleAndModelBasedReflexPlayerTest {
	
	private SimpleReflexPlayer simpleReflexPlayer;
	private ModelBasedReflexPlayer modelBasedReflexPlayer;
	private Board board;
	private ConsoleDisplay display;
	private List<Percept> smellPercept = new ArrayList<>();
	private List<Percept> bumpPercept = new ArrayList<>();
	private List<Percept> bothPercepts = new ArrayList<>();
	private Field knownCells;
	private Method addVisitedCell;
	private Method addWallCell;
	private Method getKnownCell;

	@BeforeEach
	public void setUp() throws Exception {
		Random r = new Random();
		int boardSize = r.nextInt(4) + 4;
		display = new ConsoleDisplay();
		board = new Board(boardSize, boardSize);
		board.getCell(0,0).add(MoveItem.PLAYER);
		simpleReflexPlayer = new SimpleReflexPlayer(PizzaFinderAction.getAllActions(), display, board.getCell(0, 0), Direction.RIGHT);
		modelBasedReflexPlayer = new ModelBasedReflexPlayer(PizzaFinderAction.getAllActions(), display, board.getCell(0, 0), Direction.RIGHT);
		smellPercept.add(Percept.SMELL);
		bumpPercept.add(Percept.BUMP);
		bothPercepts.add(Percept.SMELL);
		bothPercepts.add(Percept.BUMP);
		knownCells = ModelBasedReflexPlayer.class.getDeclaredField("knownCells");
		knownCells.setAccessible(true);
		addVisitedCell = ModelBasedReflexPlayer.class.getDeclaredMethod("addVisitedCell", int.class, int.class);
		addVisitedCell.setAccessible(true);
		addWallCell = ModelBasedReflexPlayer.class.getDeclaredMethod("addWallCell", int.class, int.class);
		addWallCell.setAccessible(true);
		getKnownCell = ModelBasedReflexPlayer.class.getDeclaredMethod("getKnownCell", int.class, int.class);
		getKnownCell.setAccessible(true);
	}

	//SIMPLE REFLEX AGENT TESTS/////////////////////////////////////////////////////////////////////////////////////////
	
	@Test
	public void testSimpleReflexAgentGetActionEat() {
		try {
			simpleReflexPlayer.setPercepts(new ArrayList<>());
			assertNotEquals(PizzaFinderAction.EAT, simpleReflexPlayer.getAction(),"getAction should not return EAT without that sweet sweet smell of pizza in the current cell for simple reflex agent");
			simpleReflexPlayer.setPercepts(bumpPercept);
			assertNotEquals(PizzaFinderAction.EAT, simpleReflexPlayer.getAction(),"getAction should not return EAT without that sweet sweet smell of pizza in the current cell for simple reflex agent");
			simpleReflexPlayer.setPercepts(smellPercept);
			assertEquals(PizzaFinderAction.EAT, simpleReflexPlayer.getAction(),"getAction should return EAT when you smell that tasty 'za in the current cell for simple reflex agent");
			simpleReflexPlayer.setPercepts(bothPercepts);
			assertEquals(PizzaFinderAction.EAT, simpleReflexPlayer.getAction(),"getAction should return EAT when you smell that tasty 'za in the current cell for simple reflex agent");
		} catch(Exception e) {
			e.printStackTrace();
			fail("check the console for the exception stack trace");
		}
	}
	
	@Test
	public void testSimpleReflexAgentGetActionMove() {
		try {
			Action returnedMove;
			simpleReflexPlayer.setPercepts(new ArrayList<>());
			for(Action move : MoveAction.MOVE_ACTIONS) {
				Set<Action> moves = new HashSet<>();
				simpleReflexPlayer.setFacingDirection(Direction.getDirectionFromMoveAction(move));
				
				for(int i=0; i<50; i++) {
					returnedMove = simpleReflexPlayer.getAction();
					moves.add(returnedMove);
				}
				assertEquals(4, moves.size(),"getAction should return a move in any direction when no percepts are received for simple reflex agent");
			}
		} catch(Exception e) {
			e.printStackTrace();
			fail("check the console for the exception stack trace");
		}
	}
	
	@Test
	public void testSimpleReflexAgentGetActionMoveWithBump() {
		try {
			Action returnedMove;
			simpleReflexPlayer.setPercepts(bumpPercept);
			for(Action move : MoveAction.MOVE_ACTIONS) {
				Set<Action> moves = new HashSet<>();
				simpleReflexPlayer.setFacingDirection(Direction.getDirectionFromMoveAction(move));
				
				for(int i=0; i<50; i++) {
					returnedMove = simpleReflexPlayer.getAction();
					moves.add(returnedMove);
					assertNotEquals(move, returnedMove,"getAction should not return a move in the direction the player is facing when the bump percept is received for simple reflex agent");
				}
				assertEquals(3, moves.size(),"getAction should return a move in any direction other than the direction the player is facing when the bump percept is received for simple reflex agent");
			}
		} catch(Exception e) {
			e.printStackTrace();
			fail("check the console for the exception stack trace");
		}
	}

	//MODEL-BASED REFLEX AGENT TESTS////////////////////////////////////////////////////////////////////////////////////
	@Test
	public void testModelBasedReflexAgentGetActionEat() {
		try {
			modelBasedReflexPlayer.setPercepts(new ArrayList<>());
			assertNotEquals(PizzaFinderAction.EAT, modelBasedReflexPlayer.getAction(), "getAction should not return EAT without that sweet sweet smell of pizza in the current cell for model-based reflex agent");
			modelBasedReflexPlayer.setPercepts(bumpPercept);
			assertNotEquals(PizzaFinderAction.EAT, modelBasedReflexPlayer.getAction(),"getAction should not return EAT without that sweet sweet smell of pizza in the current cell for model-based reflex agent");
			modelBasedReflexPlayer.setPercepts(smellPercept);
			assertEquals(PizzaFinderAction.EAT, modelBasedReflexPlayer.getAction(),"getAction should return EAT when you smell that tasty 'za in the current cell for model-based reflex agent");
			modelBasedReflexPlayer.setPercepts(bothPercepts);
			assertEquals(PizzaFinderAction.EAT, modelBasedReflexPlayer.getAction(),"getAction should return EAT when you smell that tasty 'za in the current cell for model-based reflex agent");
		} catch(Exception e) {
			e.printStackTrace();
			fail("check the console for the exception stack trace");
		}
	}

	@Test
	public void testModelBasedReflexAgentGetActionMoveFromInteriorCell() {
		Action move;
		ArrayList<MoveAction> invalidMoves;

		try {
			for(Direction direction : Direction.ALL_DIRECTIONS) {
				for(int i=0; i<10; i++) {
					movePlayerToRandomInteriorCell();
					invalidMoves = new ArrayList<>();

					//test with player in interior cell and no walls or visited neighbors
					resetPlayer(direction, null);
					move = modelBasedReflexPlayer.getAction();
					assertNotNull(move,"getAction should not return null with no neighboring walls or visited neighbors from an interior cell for model-based reflex agent");

					//test with player in interior cell and 1 wall and no visited neighbors
					resetPlayer(direction, null);
					invalidMoves.clear();
					invalidMoves.add(MoveAction.getMoveActionFromDirection(addAdjacentWallRandomly()));
					move = modelBasedReflexPlayer.getAction();
					for(MoveAction ma: invalidMoves) {
						assertNotEquals(ma, move,"getAction should return a move in the direction of an unvisited neighbor with no percepts, 1 neighboring wall, and no visited neighbors from an interior cell for model-based reflex agent");
					}

					//test with player in interior cell and no walls and 1 visited neighbors
					resetPlayer(direction, null);
					invalidMoves.clear();
					invalidMoves.add(MoveAction.getMoveActionFromDirection(addAdjacentVisitedCellRandomly()));
					move = modelBasedReflexPlayer.getAction();
					for(MoveAction ma: invalidMoves) {
						assertNotEquals(ma, move,"getAction should return a move in the direction of an unvisited neighbor with no percepts, no neighboring walls, and 1 visited neighbor from an interior cell for model-based reflex agent");
					}

					//test with player in interior cell and 2 walls and no visited neighbors
					resetPlayer(direction, null);
					invalidMoves.clear();
					invalidMoves.add(MoveAction.getMoveActionFromDirection(addAdjacentWallRandomly()));
					invalidMoves.add(MoveAction.getMoveActionFromDirection(addAdjacentWallRandomly()));
					move = modelBasedReflexPlayer.getAction();
					for(MoveAction ma: invalidMoves) {
						assertNotEquals(ma, move,"getAction should return a move in the direction of an unvisited neighbor with no percepts, 2 neighboring walls, and no visited neighbors from an interior cell for model-based reflex agent");
					}

					//test with player in interior cell and 2 visited neighbors
					resetPlayer(direction, null);
					invalidMoves.clear();
					invalidMoves.add(MoveAction.getMoveActionFromDirection(addAdjacentVisitedCellRandomly()));
					invalidMoves.add(MoveAction.getMoveActionFromDirection(addAdjacentVisitedCellRandomly()));
					move = modelBasedReflexPlayer.getAction();
					for(MoveAction ma: invalidMoves) {
						assertNotEquals(ma, move,"getAction should return a move in the direction of an unvisited neighbor with no percepts, no neighboring walls, and 2 visited neighbors from an interior cell for model-based reflex agent");
					}

					//test with player in interior cell and 3 visited neighbors
					resetPlayer(direction, null);
					invalidMoves.clear();
					invalidMoves.add(MoveAction.getMoveActionFromDirection(addAdjacentVisitedCellRandomly()));
					invalidMoves.add(MoveAction.getMoveActionFromDirection(addAdjacentVisitedCellRandomly()));
					invalidMoves.add(MoveAction.getMoveActionFromDirection(addAdjacentVisitedCellRandomly()));
					move = modelBasedReflexPlayer.getAction();
					for(MoveAction ma: invalidMoves) {
						assertNotEquals(ma, move,"getAction should return a move in the direction of an unvisited neighbor with no percepts from an interior cell for model-based reflex agent");
					}

					//test with player in interior cell and 4 visited neighbors
					resetPlayer(direction, null);
					invalidMoves.clear();
					invalidMoves.add(MoveAction.getMoveActionFromDirection(addAdjacentVisitedCellRandomly()));
					invalidMoves.add(MoveAction.getMoveActionFromDirection(addAdjacentVisitedCellRandomly()));
					invalidMoves.add(MoveAction.getMoveActionFromDirection(addAdjacentVisitedCellRandomly()));
					invalidMoves.add(MoveAction.getMoveActionFromDirection(addAdjacentVisitedCellRandomly()));
					move = modelBasedReflexPlayer.getAction();
					assertNotNull(move,"getAction should return a move in the direction of a visited neighbor with no percepts and all neighbors visited from an interior cell for model-based reflex agent");
				}
			}
		} catch(Exception e) {
			e.printStackTrace();
			fail("check the console for the exception stack trace");
		}
	}

	@Test
	public void testModelBasedReflexAgentGetActionMoveFromExteriorCell() {
		Action move;
		ArrayList<MoveAction> invalidMoves;
		Cell currentCell;
		int row;
		int col;

		try {
			for(int i=0; i<10; i++) {
				movePlayerToRandomExteriorCell();
				currentCell = modelBasedReflexPlayer.getCell();
				row = currentCell.getRow();
				col = currentCell.getCol();

				for(Direction direction : Direction.ALL_DIRECTIONS) {

					//test with player in exterior cell and no walls or visited neighbors
					resetPlayer(direction, null);
					invalidMoves = new ArrayList<>();
					move = modelBasedReflexPlayer.getAction();
					assertNotNull(move,"getAction should not return null with no percepts from an exterior cell for model-based reflex agent");

					//if exterior cell is not upper-left cell
					if(!(row == 0 && col == 0)) {
						//test with player in exterior cell and 1 wall
						resetPlayer(direction, null);
						invalidMoves.clear();
						invalidMoves.add(MoveAction.getMoveActionFromDirection(addAdjacentWallRandomly()));
						move = modelBasedReflexPlayer.getAction();
						for(MoveAction ma: invalidMoves) {
							assertNotEquals(ma, move,"getAction should return a move in the direction of an unvisited neighbor with no percepts, 1 neighboring wall, and no visited neighbors from an exterior cell for model-based reflex agent");
						}

						//test with player in exterior cell and 1 visited neighbor
						resetPlayer(direction, null);
						invalidMoves.clear();
						invalidMoves.add(MoveAction.getMoveActionFromDirection(addAdjacentVisitedCellRandomly()));
						move = modelBasedReflexPlayer.getAction();
						for(MoveAction ma: invalidMoves) {
							assertNotEquals(ma, move, "getAction should return a move in the direction of an unvisited neighbor with no percepts, no neighboring walls, and 1 visited neighbor from an exterior cell for model-based reflex agent");
						}

						//test with player in exterior cell and 2 walls
						resetPlayer(direction, null);
						invalidMoves.clear();
						invalidMoves.add(MoveAction.getMoveActionFromDirection(addAdjacentWallRandomly()));
						invalidMoves.add(MoveAction.getMoveActionFromDirection(addAdjacentWallRandomly()));
						move = modelBasedReflexPlayer.getAction();
						for(MoveAction ma: invalidMoves) {
							assertNotEquals(ma, move, "getAction should return a move in the direction of an unvisited neighbor with no percepts, 2 neighboring walls, and no visited neighbors from an exterior cell for model-based reflex agent");
						}

						//test with player in exterior cell and 2 visited neighbors
						resetPlayer(direction, null);
						invalidMoves.clear();
						invalidMoves.add(MoveAction.getMoveActionFromDirection(addAdjacentVisitedCellRandomly()));
						invalidMoves.add(MoveAction.getMoveActionFromDirection(addAdjacentVisitedCellRandomly()));
						move = modelBasedReflexPlayer.getAction();
						for(MoveAction ma: invalidMoves) {
							assertNotEquals(ma, move, "getAction should return a move in the direction of an unvisited neighbor with no percepts, no neighboring walls, and 2 visited neighbors from an exterior cell for model-based reflex agent");
						}

						//test with player in exterior cell, 2 walls, and 1 visited neighbors
						resetPlayer(direction, null);
						MoveAction m = MoveAction.getMoveActionFromDirection(addAdjacentVisitedCellRandomly());
						addAdjacentWallRandomly();
						addAdjacentWallRandomly();
						move = modelBasedReflexPlayer.getAction();
						assertEquals(m, move,"getAction should return a move in the direction of a visited neighbor with no percepts, 2 neighboring walls, and 1 visited neighbors from an exterior cell for model-based reflex agent");

						//test with player in exterior cell and 3 visited neighbors
						resetPlayer(direction, null);
						invalidMoves.clear(); //these are really valid moves
						invalidMoves.add(MoveAction.getMoveActionFromDirection(addAdjacentVisitedCellRandomly()));
						invalidMoves.add(MoveAction.getMoveActionFromDirection(addAdjacentVisitedCellRandomly()));
						invalidMoves.add(MoveAction.getMoveActionFromDirection(addAdjacentVisitedCellRandomly()));
						move = modelBasedReflexPlayer.getAction();
						boolean found = false;
						for(MoveAction ma: invalidMoves) {
							if(ma.equals(move)) {
								found = true;
								break;
							}
						}
						assertTrue(found,"getAction should return a move in the direction of a visited neighbor with no percepts and 3 visited neighbors from an exterior cell for model-based reflex agent");
					}
				}
			}

			//test with player in 0,0 with 1 visited neighbor
			movePlayerToCell(0,0);
			resetPlayer(Direction.RIGHT, null);
			addAdjacentVisitedCellRandomly(Direction.RIGHT);
			move = modelBasedReflexPlayer.getAction();
			assertEquals(MoveAction.RIGHT, move,"getAction should return the move in the direction of the visited neighbor with no percepts from 0,0 and the other neighbor is visited for model-based reflex agent");

			resetPlayer(Direction.DOWN, null);
			addAdjacentVisitedCellRandomly(Direction.DOWN);
			move = modelBasedReflexPlayer.getAction();
			assertEquals(MoveAction.DOWN, move,"getAction should return the move in the direction of the visited neighbor with no percepts from 0,0 and the other neighbor is visited for model-based reflex agent");

		} catch(Exception e) {
			e.printStackTrace();
			fail("check the console for the exception stack trace");
		}
	}

	@Test
	public void testModelBasedReflexAgentGetActionMoveWithBumpFromInteriorCell() {
		Action move;
		ArrayList<MoveAction> invalidMoves;
		MoveAction moveInFacingDirection;

		try {
			for(Direction direction : Direction.ALL_DIRECTIONS) {
				moveInFacingDirection = MoveAction.getMoveActionFromDirection(direction);

				for(int i=0; i<10; i++) {
					movePlayerToRandomInteriorCell();

					//test with player in interior cell and no walls or visited neighbors
					resetPlayer(direction, bumpPercept);
					invalidMoves = new ArrayList<>();
					invalidMoves.add(moveInFacingDirection);
					move = modelBasedReflexPlayer.getAction();
					assertNotNull(move,"getAction should return a move when the bump percept is received from an interior cell for model-based reflex agent");
					assertNotEquals(moveInFacingDirection, move,"getAction should not return a move in the direction the player is facing when the bump percept is received from an interior cell for model-based reflex agent");

					//test with player in interior cell and 1 wall and no visited neighbors
					resetPlayer(direction, bumpPercept);
					invalidMoves.clear();
					invalidMoves.add(moveInFacingDirection);
					invalidMoves.add(MoveAction.getMoveActionFromDirection(addAdjacentWallRandomly(direction)));
					move = modelBasedReflexPlayer.getAction();
					for(MoveAction ma: invalidMoves) {
						assertNotNull(move,"getAction should return a move when the bump percept is received from an interior cell for model-based reflex agent");
						assertNotEquals(ma, move,"getAction should not return a move in the direction of a wall when the bump percept is received from an interior cell for model-based reflex agent");
					}

					//test with player in interior cell and no walls and 1 visited neighbors
					resetPlayer(direction, bumpPercept);
					invalidMoves.clear();
					invalidMoves.add(moveInFacingDirection);
					invalidMoves.add(MoveAction.getMoveActionFromDirection(addAdjacentVisitedCellRandomly(direction)));
					move = modelBasedReflexPlayer.getAction();
					for(MoveAction ma: invalidMoves) {
						assertNotNull(move,"getAction should return a move when the bump percept is received from an interior cell for model-based reflex agent");
						assertNotEquals(ma, move,"getAction should not return a move in the direction of a visited neighbor when the bump percept is received from an interior cell for model-based reflex agent");
					}

					//test with player in interior cell and 2 walls
					resetPlayer(direction, bumpPercept);
					invalidMoves.clear();
					invalidMoves.add(moveInFacingDirection);
					invalidMoves.add(MoveAction.getMoveActionFromDirection(addAdjacentWallRandomly(direction)));
					invalidMoves.add(MoveAction.getMoveActionFromDirection(addAdjacentWallRandomly(direction)));
					move = modelBasedReflexPlayer.getAction();
					for(MoveAction ma: invalidMoves) {
						assertNotNull(move,"getAction should return a move when the bump percept is received from an interior cell for model-based reflex agent");
						assertNotEquals(ma, move,"getAction should not return a move in the direction of a wall when the bump percept is received from an interior cell for model-based reflex agent");
					}

					//test with player in interior cell and 2 visited neighbors
					resetPlayer(direction, bumpPercept);
					invalidMoves.clear();
					invalidMoves.add(moveInFacingDirection);
					invalidMoves.add(MoveAction.getMoveActionFromDirection(addAdjacentVisitedCellRandomly(direction)));
					invalidMoves.add(MoveAction.getMoveActionFromDirection(addAdjacentVisitedCellRandomly(direction)));
					move = modelBasedReflexPlayer.getAction();
					for(MoveAction ma: invalidMoves) {
						assertNotNull(move,"getAction should return a move when the bump percept is received from an interior cell for model-based reflex agent");
						assertNotEquals(ma, move,"getAction should not return a move in the direction of a visited neighbor when the bump percept is received from an interior cell for model-based reflex agent");
					}

					//test with player in interior cell and 3 visited neighbors
					resetPlayer(direction, bumpPercept);
					invalidMoves.clear();
					invalidMoves.add(moveInFacingDirection);
					invalidMoves.add(MoveAction.getMoveActionFromDirection(addAdjacentVisitedCellRandomly(direction)));
					invalidMoves.add(MoveAction.getMoveActionFromDirection(addAdjacentVisitedCellRandomly(direction)));
					invalidMoves.add(MoveAction.getMoveActionFromDirection(addAdjacentVisitedCellRandomly(direction)));
					move = modelBasedReflexPlayer.getAction();
					assertNotNull(move,"getAction should return a move when the bump percept is received from an interior cell for model-based reflex agent");
					assertNotEquals(moveInFacingDirection, move,"getAction should return a move in the direction of a visited neighbor when the bump percept is received from an interior cell and all other neighbors are visited for model-based reflex agent");
				}
			}
		} catch(Exception e) {
			e.printStackTrace();
			fail("check the console for the exception stack trace");
		}
	}

	@Test
	public void testModelBasedReflexAgentGetActionMoveWithBumpFromExteriorCell() {
		Action move;
		ArrayList<MoveAction> invalidMoves;
		MoveAction moveInFacingDirection;
		Cell currentCell;
		int row;
		int col;

		try {
			for(int i=0; i<10; i++) {
				movePlayerToRandomExteriorCell();
				currentCell = modelBasedReflexPlayer.getCell();
				row = currentCell.getRow();
				col = currentCell.getCol();

				for(Direction direction : Direction.ALL_DIRECTIONS) {
					moveInFacingDirection = MoveAction.getMoveActionFromDirection(direction);

					//test with player in exterior cell and no walls or visited neighbors
					resetPlayer(direction, bumpPercept);
					invalidMoves = new ArrayList<>();
					invalidMoves.add(moveInFacingDirection);
					move = modelBasedReflexPlayer.getAction();
					assertNotEquals(moveInFacingDirection, move,"getAction should not return a move in the direction the player is facing when the bump percept is received from an exterior cell for model-based reflex agent");

					//if exterior cell is not upper-left cell
					if(!(row == 0 && col == 0)) {
						//test with player in exterior cell and 1 wall
						resetPlayer(direction, bumpPercept);
						invalidMoves.clear();
						invalidMoves.add(moveInFacingDirection);
						invalidMoves.add(MoveAction.getMoveActionFromDirection(addAdjacentWallRandomly(direction)));
						move = modelBasedReflexPlayer.getAction();
						for(MoveAction ma: invalidMoves) {
							assertNotEquals(ma, move,"getAction should not return a move in the direction of a wall when the bump percept is received from an exterior cell for model-based reflex agent");
						}

						//test with player in exterior cell and 1 visited neighbor
						resetPlayer(direction, bumpPercept);
						invalidMoves.clear();
						invalidMoves.add(moveInFacingDirection);
						invalidMoves.add(MoveAction.getMoveActionFromDirection(addAdjacentVisitedCellRandomly(direction)));
						move = modelBasedReflexPlayer.getAction();
						for(MoveAction ma: invalidMoves) {
							assertNotEquals(ma, move,"getAction should not return a move in the direction of a visited cell when the bump percept is received from an exterior cell for model-based reflex agent");
						}

						//test with player in exterior cell and 2 visited neighbors
						resetPlayer(direction, bumpPercept);
						invalidMoves.clear();
						invalidMoves.add(moveInFacingDirection);
						invalidMoves.add(MoveAction.getMoveActionFromDirection(addAdjacentVisitedCellRandomly(direction)));
						invalidMoves.add(MoveAction.getMoveActionFromDirection(addAdjacentVisitedCellRandomly(direction)));
						move = modelBasedReflexPlayer.getAction();
						assertNotEquals(moveInFacingDirection, move,"getAction should return a move in the direction of a visited neighbor when the bump percept is received from an exterior cell and all other neighbors are visited for model-based reflex agent");
					}
				}
			}

			//test with player in 0,0 with 1 visited neighbor
			movePlayerToCell(0,0);
			resetPlayer(Direction.RIGHT, bumpPercept);
			addAdjacentVisitedCellRandomly(Direction.RIGHT);
			move = modelBasedReflexPlayer.getAction();
			assertEquals(MoveAction.DOWN, move,"getAction should return the move in the direction of the visited neighbor when the bump percept is received from 0,0 and the other neighbor is visited for model-based reflex agent");

			resetPlayer(Direction.DOWN, bumpPercept);
			addAdjacentVisitedCellRandomly(Direction.DOWN);
			move = modelBasedReflexPlayer.getAction();
			assertEquals(MoveAction.RIGHT, move,"getAction should return the move in the direction of the visited neighbor when the bump percept is received from 0,0 and the other neighbor is visited for model-based reflex agent");

		} catch(Exception e) {
			e.printStackTrace();
			fail("check the console for the exception stack trace");
		}
	}

	@Test
	public void testModelBasedReflexAgentGetActionBumpWallRecognition() {
		Cell playerCell;
		Cell wallCell = null;
		int row;
		int col;

		try {
			modelBasedReflexPlayer.setPercepts(bumpPercept);
			for(Direction direction : Direction.ALL_DIRECTIONS) {
				modelBasedReflexPlayer.setFacingDirection(direction);
				for(int i=0; i<5; i++) {
					movePlayerToRandomInteriorCell();
					modelBasedReflexPlayer.getAction();
					playerCell = board.getPlayerCell();
					row = playerCell.getRow();
					col = playerCell.getCol();

					if(Direction.UP.equals(direction)) {
						wallCell = getKnownCell(row-1, col);
					} else if(Direction.RIGHT.equals(direction)) {
						wallCell = getKnownCell(row, col+1);
					} else if(Direction.DOWN.equals(direction)) {
						wallCell = getKnownCell(row+1, col);
					} else  if(Direction.LEFT.equals(direction)) {
						wallCell = getKnownCell(row, col-1);
					}

					//test with player in interior cell
					assertTrue(wallCell.contains(PizzaFinderItem.WALL),"getAction should cause the agent to remember the wall cell when a bump occurs for model-based reflex agent");
					wallCell.remove(PizzaFinderItem.WALL);
				}
			}
		} catch(Exception e) {
			e.printStackTrace();
			fail("check the console for the exception stack trace");
		}
	}

	@Test
	public void testDifferentBoardSize() {
		try {
			board = new Board(5, 5);
			simpleReflexPlayer.setPercepts(new ArrayList<>());
			assertNotEquals(PizzaFinderAction.EAT, simpleReflexPlayer.getAction(),"getAction should not return EAT without that sweet sweet smell of pizza in the current cell for simple reflex agent");
			simpleReflexPlayer.setPercepts(bumpPercept);
			assertNotEquals(PizzaFinderAction.EAT, simpleReflexPlayer.getAction(),"getAction should not return EAT without that sweet sweet smell of pizza in the current cell for simple reflex agent");
			simpleReflexPlayer.setPercepts(smellPercept);
			assertEquals(PizzaFinderAction.EAT, simpleReflexPlayer.getAction(),"getAction should return EAT when you smell that tasty 'za in the current cell for simple reflex agent");
			simpleReflexPlayer.setPercepts(bothPercepts);
			assertEquals(PizzaFinderAction.EAT, simpleReflexPlayer.getAction(),"getAction should return EAT when you smell that tasty 'za in the current cell for simple reflex agent");
		} catch(Exception e) {
			e.printStackTrace();
			fail("check the console for the exception stack trace");
		}
	}

	private void movePlayerToRandomInteriorCell() {
		Random r = new Random();
		int row = r.nextInt(board.getNumRows()-2)+1;
		int col = r.nextInt(board.getNumCols()-2)+1;
		movePlayerToCell(row, col);
	}

	private void movePlayerToRandomExteriorCell() {
		Random r = new Random();
		int rowOrCol = r.nextInt(2);
		if(rowOrCol == 0) {
			movePlayerToCell(0, r.nextInt(board.getNumCols()-2)+1);
		} else {
			movePlayerToCell(r.nextInt(board.getNumRows()-2)+1, 0);
		}

	}

	private void movePlayerToCell(int row, int col) {
		board.getPlayerCell().remove(MoveItem.PLAYER);
		board.getCell(row, col).add(MoveItem.PLAYER);
		modelBasedReflexPlayer.setCell(board.getPlayerCell());
	}

	@SuppressWarnings("unchecked")
	private void resetPlayer(Direction facingDirection, List<Percept> percepts) {
		modelBasedReflexPlayer.setFacingDirection(facingDirection);
		if(percepts != null) {
			modelBasedReflexPlayer.setPercepts(percepts);
		}
		try {
			((List<List<Cell>>)knownCells.get(modelBasedReflexPlayer)).clear();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
	}

	private Direction addAdjacentWallRandomly() {
		return addAdjacentWallRandomly(null);
	}

	private Direction addAdjacentWallRandomly(Direction notInThisDirection) {
		Cell playerCell = board.getPlayerCell();
		int row = playerCell.getRow();
		int col = playerCell.getCol();
		Cell cellUp = getKnownCell(row-1, col);
		Cell cellRight = getKnownCell(row, col+1);
		Cell cellDown = getKnownCell(row+1, col);
		Cell cellLeft = getKnownCell(row, col-1);
		ArrayList<Direction> directions = new ArrayList<Direction>(Arrays.asList(Direction.ALL_DIRECTIONS));
		Direction wallDirection = null;

		if(notInThisDirection != null) {
			directions.remove(notInThisDirection);
		}

		if(row == 0 || (cellUp != null && (cellUp.contains(PizzaFinderItem.WALL) || cellUp.wasVisited()))) {
			directions.remove(Direction.UP);
		}
		if(col == board.getNumCols()-1 || (cellRight != null && (cellRight.contains(PizzaFinderItem.WALL) || cellRight.wasVisited()))) {
			directions.remove(Direction.RIGHT);
		}
		if(row == board.getNumRows()-1 || (cellDown != null && (cellDown.contains(PizzaFinderItem.WALL) || cellDown.wasVisited()))) {
			directions.remove(Direction.DOWN);
		}
		if(col == 0 || (cellLeft != null && (cellLeft.contains(PizzaFinderItem.WALL) || cellLeft.wasVisited()))) {
			directions.remove(Direction.LEFT);
		}

		//randomly pick a wall-less neighbor and add a wall
		if(!directions.isEmpty()) {
			Random r = new Random();
			int i = r.nextInt(directions.size());
			wallDirection = directions.get(i);

			if(Direction.UP.equals(wallDirection)) {
				addWallCell(row-1,  col);
			} else if(Direction.RIGHT.equals(wallDirection)) {
				addWallCell(row, col+1);
			} else if(Direction.DOWN.equals(wallDirection)) {
				addWallCell(row+1, col);
			} else if(Direction.LEFT.equals(wallDirection)) {
				addWallCell(row, col-1);
			}
		}
		return wallDirection;
	}

	private Direction addAdjacentVisitedCellRandomly() {
		return addAdjacentVisitedCellRandomly(null);
	}

	private Direction addAdjacentVisitedCellRandomly(Direction notInThisDirection) {
		Cell playerCell = board.getPlayerCell();
		int row = playerCell.getRow();
		int col = playerCell.getCol();
		Cell cellUp = getKnownCell(row-1, col);
		Cell cellRight = getKnownCell(row, col+1);
		Cell cellDown = getKnownCell(row+1, col);
		Cell cellLeft = getKnownCell(row, col-1);
		ArrayList<Direction> directions = new ArrayList<Direction>(Arrays.asList(Direction.ALL_DIRECTIONS));
		Direction visitedCellDirection = null;

		if(notInThisDirection != null) {
			directions.remove(notInThisDirection);
		}

		if(row == 0 || (cellUp != null && (cellUp.contains(PizzaFinderItem.WALL) || cellUp.wasVisited()))) {
			directions.remove(Direction.UP);
		}
		if(col == board.getNumCols()-1 || (cellRight != null && (cellRight.contains(PizzaFinderItem.WALL) || cellRight.wasVisited()))) {
			directions.remove(Direction.RIGHT);
		}
		if(row == board.getNumRows()-1 || (cellDown != null && (cellDown.contains(PizzaFinderItem.WALL) || cellDown.wasVisited()))) {
			directions.remove(Direction.DOWN);
		}
		if(col == 0 || (cellLeft != null && (cellLeft.contains(PizzaFinderItem.WALL) || cellLeft.wasVisited()))) {
			directions.remove(Direction.LEFT);
		}

		//randomly pick an unvisited neighbor and set it as visited
		if(!directions.isEmpty()) {
			Random r = new Random();
			int i = r.nextInt(directions.size());
			visitedCellDirection = directions.get(i);

			if(Direction.UP.equals(visitedCellDirection)) {
				addVisitedCell(row-1,  col);
			} else if(Direction.RIGHT.equals(visitedCellDirection)) {
				addVisitedCell(row, col+1);
			} else if(Direction.DOWN.equals(visitedCellDirection)) {
				addVisitedCell(row+1, col);
			} else if(Direction.LEFT.equals(visitedCellDirection)) {
				addVisitedCell(row, col-1);
			}
		}
		return visitedCellDirection;
	}

	private Cell getKnownCell(int row, int col) {
		Cell knownCell = null;
		try {
			knownCell = (Cell) this.getKnownCell.invoke(modelBasedReflexPlayer, row, col);
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		return knownCell;
	}

	private void addWallCell(int row, int col) {
		try {
			this.addWallCell.invoke(modelBasedReflexPlayer, row, col);
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
	}

	private void addVisitedCell(int row, int col) {
		try {
			this.addVisitedCell.invoke(modelBasedReflexPlayer, row, col);
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
	}
}
