package basis.effects;

import core.Listener;
import render.Render;

public interface Effect extends Listener {

	public void update(double delta);
	
	public void render(float scaleX, float scaleY);
	
	default public void render() {render(Render.scaleX, Render.scaleY);};
	
	public boolean living();
	
}
