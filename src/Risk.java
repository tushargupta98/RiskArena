/*
 * Risk.java starts execution of RiskArena. See READ_ME for more
 * 
 * In charge of output and errorout messages
 * In charge of setting the game up, as of now through the Scanner / System.in
 * Contains the main turn-by-turn execution loop
 * Uses a Game object, the RiskArena game engine
 * 
 * Evan Radkoff
 */

import java.awt.Color;
import java.lang.String;

import javax.swing.SwingUtilities;

/*
 * The Risk class, containing main()
 */
public class Risk {

	final static String PROJECT_NAME = "RiskArena";
	final static int MIN_PLAYERS = 2;	// Minimum numbers that can play
	final static int MAX_PLAYERS = 6;	// Maximum numbers that can play
	// ^ If changing MAX_PLAYERS, you may need to set the number of armies each player starts with in Game.placeArmies()
	// 		^ as well as Risk.getPlayerColor()
	final static String MAPS_DIR_NAME = "src/maps/"; // name of the directory containing map files
	final static String FONT_PATH = "src/fonts/AmericanTypewriter.ttc";
	final static String BOT_PATH = "src"; // location of bots
	final static String LOG_PATH = "src/logs/";
	final static String RISKBOT_PREFIX = "RiskBot";	// any file beginning with this is considered by BotSniffer as a potential AI option
	final static boolean output_to_std = true;
	final static boolean input_from_std = false;

	//private static InputListener console_input;
	private static int num_players;	// The number of players in a given game.
	private static Player players[];	// Structure to hold player names when read from input
	private static Game game;	// The instance of the game engine class, Game
	private static SetUp setup; // Game set up panel

	// Hello World!
	public static void main(String[] args) {
		
		setup = new SetUp();
		players = setup.getPlayers();
		if(players == null) {
			System.exit(1);
		}
		
		game = new Game(players, MAPS_DIR_NAME + setup.getMap());
		game.init();
		game.play();
	}

	/*
	 * Returns the color of a given player id. If more than 6 players are allowed,
	 * this needs to be manually changed.
	 */
	public static Color getPlayerColor(int player_id) {
		switch(player_id) {
		case 0:
			return Color.red;
		case 1:
			return Color.blue;
		case 2:
			return Color.green;
		case 3:
			return Color.magenta;
		case 4:
			return new Color(0.2f,0.2f,0.2f);
		case 5:
			return Color.yellow;
		default:
			return Color.gray;
		}
	}

	/* Called by various methods to send something to whatever
	 * output is being used.
	 * toSay is the string wishing to be outputted
	 * tabbed is whether or not it should be prepended with a \t
	 */
	public static void sayOutput(final String toSay, final int output_format_style) {
		if(game == null && output_format_style == OutputFormat.ERROR) {
			System.err.println(toSay);
		} else if (game == null) {
			System.out.println(toSay);
		} else
			game.sayOutput(toSay, output_format_style);
	}

	/*
	 * Called by various methods to send something to whatever
	 * output is being used.
	 * toSay is the string wishing to be outputted
	 */
	public static void sayOutput(final String toSay) {
		sayOutput(toSay, OutputFormat.NORMAL);
	}

	/* Called by various methods to send something to whatever
	 * error output is being used.
	 * @param The string wishing to be outputted error message.
	 */
	public static void sayError(final String toSay) {
		sayOutput(toSay, OutputFormat.ERROR);
	}

}
