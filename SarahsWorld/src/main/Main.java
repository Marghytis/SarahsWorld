package main;

import menu.MenuManager;
import world.worldGeneration.Save;
import core.Core;
import core.Listener;
import core.Renderer;
import core.Updater;
import core.Window;

public class Main {
	
	public static MenuManager menu;
	public static world.worldGeneration.World world;

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
		if(Save.worldSaved()){
			world = Save.loadWorld();
		} else {
			world = new world.worldGeneration.World();
		}

		Updater.updaters.add(menu);
		Updater.updaters.add(world.window);

		Renderer.renderers.add(world.window);
		Renderer.renderers.add(menu);

		Listener.listeners.add(menu);
		Listener.listeners.add(world.avatar.avatar);
		
		core.coreLoop();
	}
	
}
