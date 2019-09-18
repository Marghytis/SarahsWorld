package extra;

import org.lwjgl.opengl.GL11;

import core.Window;
import menu.Settings;
import moveToLWJGLCore.BasisMain;

/**
 * Contains the 'main' function, which starts the game. Also contains the static objects for the game core, menu and the currently loaded world.
 * @author Mario
 *
 */
public class Main extends BasisMain {

	//configuration parameters. TODO: read from a file
	public final static String TEX_ATLAS_TABLE_PATH = "res/TexAtlasTable.res";
	public final static String SETTINGS_PATH = "res/Settings.txt";
	
	private static SarahsWorld myGame;
	
	/**
	 * Creates a Window, loads or creates a world and resets the core classes. Then it starts the game loop.
	 * @param args
	 */
	public static void main(String[] args)
	{
		Window.showSplashScreen("res/Titel.png");
		
		createHardwareBindings("Sarahs World");
		createGame("Sarahs World");
		
		myGame.start(true);
	}
	
	public static void prepareTest(String worldName)
	{
		createHardwareBindings(worldName);
		createGame(worldName);

		window.init();
		speaker.init();
		myGame.initGame();
	}

	/**
	 * Creates a new game instance and updates the static variables SIZE and SIZE_HALF
	 * @param worldName
	 */
	protected static SarahsWorld createGame(String worldName)
	{
		//create a game object
		myGame = new SarahsWorld(window, speaker, 60);
		
		if(Settings.getInt("DEBUG_LEVEL") > 1)
			System.out.println("OpenGL version: " + GL11.glGetString(GL11.GL_VERSION));
		
		return myGame;
	}
	
	/**
	 * Returns the most recently created game instance
	 * @return
	 */
	public static SarahsWorld game() {
		return myGame;
	}

}
