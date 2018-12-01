package main;

import org.lwjgl.opengl.GL11;

import core.Core;
import core.Game;
import core.Input;
import core.Speaker;
import core.Window;
import menu.Menu;
import menu.Settings;
import util.math.IntVec;
import world.SoundManager;
import world.World;

/**
 * Contains the 'main' function, which starts the game. Also contains the static objects for the game core, menu and the currently loaded world.
 * @author Mario
 *
 */
public class Main implements Game {
	
	//configuration parameters. TODO: read from a file
	final static String TEX_ATLAS_TABLE_PATH = "res/TexAtlasTable.res";
	public final static String SETTINGS_PATH = "res/Settings.txt";
	
	public static Input input;
	public static Menu menu;
	public static World world;
	public static SoundManager sound;
	public static Core core;
	public static IntVec SIZE, HALFSIZE;
	public static long WINDOW;
	
	/**
	 * Creates a Window, loads or creates a world and resets the core classes. Then it starts the game loop.
	 * @param args
	 */
	public static void main(String[] args)
	{
		Window.showSplashScreen("res/Titel.png");
		
		initializeGame("Sarahs World");

		core.start(true);
	}
	
	/**
	 * Initializes the window, core object, menu and world.
	 * @param worldName name of the world to be loaded or created 
	 */
	static void initializeGame(String worldName)
	{
		input = new Input();
		Window window = new Window("Sarahs World", false, 1, 1, true, true, input);
		Speaker speaker = new Speaker();
		Game game = new Main();
		//create a core object
		core = new Core(window, speaker, game, 60);
		if(Settings.getInt("DEBUG_LEVEL") > 1)
			System.out.println("OpenGL version: " + GL11.glGetString(GL11.GL_VERSION));
		
		SIZE = core.SIZE;
		HALFSIZE = core.SIZE_HALF;
	}

	public void init() {
		WINDOW = core.getWindow().getHandle();
		//create a menu object and load or create a world object
		menu = new Menu();
		if(Save.worldSaved("worlds/world.sw")){//TODO make the path variable
			world = Save.loadWorld("worlds/world.sw");
		} else {
			world = new World();
		}
		sound = new SoundManager();

		resetCoreLists();
	}
	
	/**
	 * Resets the lists of 'Updaters', 'Renderers' and 'Listeners'
	 */
	public static void resetCoreLists()
	{
		core.setUpdaters(
				menu,
				sound,
				world.engine,
				world.window);

		core.getWindow().setRenderers(
				world.window,
				menu);

		core.getWindow().setListeners(
				menu,
				world.avatar.type.avatar);
	}
	
}
