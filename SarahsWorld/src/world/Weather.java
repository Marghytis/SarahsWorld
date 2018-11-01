package world;

import effects.particles.FogWorld;
import world.render.WorldWindow;

/**
 * Should contain wind, fog, rain, snow, clouds, sunshine, thunder and lightning
 * @author Mario
 *
 */
public class Weather {

	public FogWorld fog = new FogWorld(200);
	
	public void addEffects(){
		WorldWindow.addEffect(fog);
	}
}
