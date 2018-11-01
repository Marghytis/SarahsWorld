package world;

import effects.particles.FogWorld;
import main.Main;

/**
 * Should contain wind, fog, rain, snow, clouds, sunshine, thunder and lightning
 * @author Mario
 *
 */
public class Weather {

	public static FogWorld fog = new FogWorld(200);
	
	public void addEffects(){
		Main.world.window.addEffect(fog);
	}
	
	public boolean update(double delta) {
		return false;
	}
}
