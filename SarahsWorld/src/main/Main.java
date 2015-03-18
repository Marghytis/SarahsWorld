package main;

import menu.MenuManager;
import util.math.Vec;
import world.World;
import core.Core;
import core.Listener;
import core.Renderer;
import core.Updater;
import core.Window;

public class Main {
	
	public static MenuManager menu;
	public static World world;

	public static void main(String[] args){
		
		String worldName = "Sarahs World";
		Core core = new Core(worldName);
		if(Window.WIDTH == 0){
			System.out.println("ERROR AT START!!! Try again.");
			Window.destroy();
			System.exit(0);
			return;
		}

		menu = new MenuManager();
		world = new World(worldName, false);

		Updater.updaters.add(menu);
		Updater.updaters.add(world);

		Renderer.renderers.add(world);
		Renderer.renderers.add(menu);

		Listener.listeners.add(menu);
		Listener.listeners.add(world.avatar);
		
		core.coreLoop();
	}
	
}
