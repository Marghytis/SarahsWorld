package effects;

import core.Listener;
import main.Main;

public interface Effect extends Listener {

	public void update(double delta);
	
	public void render(float scaleX, float scaleY);
	
	default public void render() {render(Main.world.window.scaleX, Main.world.window.scaleY);};
	
	public boolean living();
	
}
