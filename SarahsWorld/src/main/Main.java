package main;

import org.lwjgl.opengl.ContextAttribs;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;

import core.Core;
import core.Listener;
import core.Renderer;
import core.Updater;
import core.Window;
import menu.Menu;
import menu.Settings;
import newStuff.StaticInit;
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
		//set openGL version
		Window.contextAttribs = new ContextAttribs(3, 3)
		    .withForwardCompatible(true)
		    .withProfileCore(true);
		
		//create a core object
		core = new Core(worldName);
		
		if(Settings.DEBUG_LEVEL > 1)
			System.out.println("OpenGL version: " + GL11.glGetString(GL11.GL_VERSION));

		StaticInit.checkGLErrors(true);
		Display.setVSyncEnabled(false);

		//create a menu object and load or create a world object
		menu = new Menu();
		if(Save.worldSaved()){
			world = Save.loadWorld();
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
		Updater.updaters.add(world.window);

		Renderer.renderers.clear();
		Renderer.renderers.add(world.window);
		Renderer.renderers.add(menu);

		Listener.listeners.clear();
		Listener.listeners.add(menu);
		Listener.listeners.add(world.avatar.type.avatar);
	}
	
}
