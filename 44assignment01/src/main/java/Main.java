import gridgames.display.ConsoleDisplay;
import gridgames.display.Display;
import gridgames.display.EV3Display;
import pizzafinder.PizzaFinder;
import pizzafinder.data.PizzaFinderAction;
import gridgames.data.Direction;
import gridgames.data.action.Action;
import pizzafinder.player.ModelBasedReflexPlayer;
import pizzafinder.player.PizzaFinderPlayer;
import pizzafinder.player.SimpleReflexPlayer;
import gridgames.grid.Cell;
import gridgames.player.Player;
import gridgames.player.HumanPlayer;
import gridgames.player.EV3Player;

import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class Main {
	
    public static void main(String[] args) {
    	List<Action> allActions = Arrays.asList(PizzaFinderAction.ALL_ACTIONS);
    	if(args.length > 0 && "-console".equals(args[0])) {
    		runOnConsole(allActions);
    	} else {
    		runOnRobot(allActions);
    	}
    }
    
    public static void runOnConsole(List<Action> allActions) {
    	Scanner scanner = new Scanner(System.in);
    	Display display = new ConsoleDisplay();
        String choice;
        Player player = null;
        PizzaFinder game = null;
        int boardSize = 0;
        
        do {
            do {
                System.out.print("How many rows/columns would you like? [4-7]: ");
                boardSize = scanner.nextInt();
            } while(boardSize < 4 || boardSize > 7);

        	game = new PizzaFinder(display, boardSize, boardSize);
        	player = getPlayer(scanner, game);
            do {
            	game.play(player);
                System.out.print("Play again? [YES, NO]: ");
                choice = scanner.next().toLowerCase();
            } while(!choice.equals("yes") && !choice.equals("no"));
        } while(choice.equals("yes"));
        scanner.close();
    }
    
    public static void runOnRobot(List<Action> allActions) {
    	EV3Display display = new EV3Display();
    	PizzaFinder game = new PizzaFinder(display, 5, 5);
        Cell initialCell = game.getInitialCell();
        EV3Player robot = new EV3Player(new ModelBasedReflexPlayer(PizzaFinderAction.getAllActions(), display, initialCell, Direction.RIGHT));
        display.setEv3Display(robot.getEv3Display());
        robot.displayInstructions();
        game.play(robot);
    }
    
    private static Player getPlayer(Scanner scanner, PizzaFinder game) {
    	List<Action> actions = PizzaFinderAction.getAllActions();
    	Display display = game.getDisplay();
    	Cell initialCell = game.getInitialCell();
    	Player player = null;
    	String choice;
    	 do {
             System.out.print("Human play or computer play? [HUMAN, COMPUTER]: ");
             choice = scanner.next().toLowerCase();
         } while(!choice.equals("human") && !choice.equals("computer"));
    	 
    	 if(choice.equals("human")) {
    		 PizzaFinderPlayer pizzaFinderPlayer = new PizzaFinderPlayer(actions, display, initialCell, Direction.RIGHT);
    		 player = new HumanPlayer(pizzaFinderPlayer, scanner);
         } else {
             do {
                 System.out.print("Simple or model-based reflex agent? [SIMPLE, MODEL-BASED]: ");
                 choice = scanner.next().toLowerCase();
             } while(!choice.equals("simple") && !choice.equals("model-based"));
             if(choice.equals("simple")) {
                 player = new SimpleReflexPlayer(actions, display, initialCell, Direction.RIGHT);
             } else {
                player = new ModelBasedReflexPlayer(actions, display, initialCell, Direction.RIGHT);
             }
         }
    	return player;
    }
}