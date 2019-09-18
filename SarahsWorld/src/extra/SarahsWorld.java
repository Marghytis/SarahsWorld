package extra;

import basis.Save;
import core.Core;
import core.Game;
import core.Speaker;
import core.Window;
import input.PollData;
import menu.MenuManager;
import world.SoundManager;
import world.World;

public class SarahsWorld extends Core implements Game {
	
	public PollData input2;
	public MenuManager menu;
	public World world;
	public SoundManager sound;

	public SarahsWorld(Window window, Speaker speaker, int fps) {
		super(window, speaker, fps);
	}

	@Override
	public void initGame() {
		Res.init();
		input2 = getWindow().getPollData();
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
		doAfterTheRest(() -> {
			world = new World(input2);
			resetCoreLists();
		});
	}
	
	public void requestTermination() {
		getWindow().requestClose();
	}
	
	/**
	 * Resets the lists of 'Updaters', 'Renderers' and 'Listeners'
	 */
	private void resetCoreLists()
	{
		setUpdaters(
				menu,
				sound,
				world.engine,
				world.window);

		getWindow().setRenderers(
				world.window,
				menu);

		getWindow().setListeners(
				menu,
				world.listener);
	}
}
