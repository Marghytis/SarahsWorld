package main;

import org.lwjgl.opengl.GL11;

import core.Core;
import core.Game;
import core.Input;
import core.Speaker;
import core.Window;
import input.PollData;
import menu.MenuManager;
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
	
	private static Window window;
	private static Speaker speaker;
	private static Game game;
	public static Input input;
	public static PollData input2;
	public static MenuManager menu;
	public static World world;
	public static SoundManager sound;
	public static Core core;
	public static IntVec SIZE, HALFSIZE;
	public static long WINDOW;
	public static Out out;
	
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
	
	public static void prepareTest(String worldName) {
		initializeGame(worldName);

		window.init();
		speaker.init();
		game.init();
	}
	
	/**
	 * Initializes the window, core object, menu and world.
	 * @param worldName name of the world to be loaded or created 
	 */
	private static void initializeGame(String worldName)
	{
		out = new Out(10);
		input = new Input();
		window = new Window("Sarahs World", false, 1, 1, true, true, input);
		speaker = new Speaker();
		game = new Main();
		//create a core object
		core = new Core(window, speaker, game, 60);
		if(Settings.getInt("DEBUG_LEVEL") > 1)
			System.out.println("OpenGL version: " + GL11.glGetString(GL11.GL_VERSION));
		
		SIZE = core.SIZE;
		HALFSIZE = core.SIZE_HALF;
	}

	public void init() {
		input2 = window.getPollData();
		WINDOW = core.getWindow().getHandle();
		//create a menu object and load or create a world object
		if(Save.worldSaved("worlds/world.sw")){//TODO make the path variable
			world = Save.loadWorld("worlds/world.sw", input2);
		} else {
			world = new World(input2);
		}
		menu = new MenuManager(this);
		menu.init();//because this function uses the menu variable already
		sound = new SoundManager();

		resetCoreLists();
	}
	
	public PollData getInputData() {
		return input2;
	}
	
	public SoundManager getSoundManager() {
		return sound;
	}
	
	public World getWorld() {
		return world;
	}
	
	public MenuManager getMenu() {
		return menu;
	}
	
	public void requestNewWorld() {
		Main.core.doAfterTheRest(() -> {
			world = new World(input2);
			resetCoreLists();
		});
	}
	
	public void requestTermination() {//TODO add termination function to Game
		core.getWindow().requestClose();
	}
	
	/**
	 * Resets the lists of 'Updaters', 'Renderers' and 'Listeners'
	 */
	private static void resetCoreLists()
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
				world.listener);
	}
	
}
