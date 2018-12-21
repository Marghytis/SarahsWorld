package world;

import effects.particles.FogWorld;

/**
 * Should contain wind, fog, rain, snow, clouds, sunshine, thunder and lightning
 * @author Mario
 *
 */
public class Weather {

	public FogWorld fog = new FogWorld(200);
	private World world;
	
	public Weather(World world) {
		this.world = world;
	}
	
	public void addEffects(){
		world.window.addEffect(fog);
	}
	
	public boolean update(double delta) {
		return false;
	}
}
