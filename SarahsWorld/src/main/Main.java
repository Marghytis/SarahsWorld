package main;

import org.lwjgl.opengl.GL11;

import core.Core;
import core.Listener;
import core.Renderer;
import core.Updater;
import core.Window;
import menu.Menu;
import menu.Settings;
import util.Color;
import util.math.IntVec;
import world.World;

/**
 * Contains the 'main' function, which starts the game. Also contains the static objects for the game core, menu and the currently loaded world.
 * @author Mario
 *
 */
public class Main {
	
	//configuration parameters. TODO: read from a file
	final static String TEX_ATLAS_TABLE_PATH = "res/TexAtlasTable.res";
	
	public static Menu menu;
	public static World world;
	public static Core core;
	public static IntVec SIZE, HALFSIZE;
	public static long WINDOW;
	
	/**
	 * Creates a Window, loads or creates a world and resets the core classes. Then it starts the game loop.
	 * @param args
	 */
	public static void main(String[] args)
	{
		
		initializeGame("Sarahs World");

		resetCoreLists();

		core.coreLoop();
	}
	
	/**
	 * Initializes the window, core object, menu and world.
	 * @param worldName name of the world to be loaded or created 
	 */
	static void initializeGame(String worldName)
	{
		//create a core object
		core = new Core("res/creatures/Meteor.png");
		core.init(new Window("Sarahs World", true, 1, 1, true), Color.BLACK);
		if(Settings.DEBUG_LEVEL > 1)
			System.out.println("OpenGL version: " + GL11.glGetString(GL11.GL_VERSION));
		
		SIZE = core.SIZE;
		HALFSIZE = core.SIZE_HALF;
		WINDOW = core.getWindow().getHandle();
		

		//create a menu object and load or create a world object
		menu = new Menu();
		if(Save.worldSaved("worlds/world.sw")){//TODO make the path variable
			world = Save.loadWorld("worlds/world.sw");
		} else {
			world = new World();
		}
	}
	
	/**
	 * Resets the lists of 'Updaters', 'Renderers' and 'Listeners'
	 */
	public static void resetCoreLists()
	{
		Updater.updaters.clear();
		Updater.updaters.add(menu);
		Updater.updaters.add(world.engine);
		Updater.updaters.add(world.window);

		Renderer.renderers.clear();
		Renderer.renderers.add(world.window);
		Renderer.renderers.add(menu);

		Listener.listeners.clear();
		Listener.listeners.add(menu);
		Listener.listeners.add(world.avatar.type.avatar);
	}
	
}
