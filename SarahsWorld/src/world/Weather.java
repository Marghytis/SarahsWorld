package world;

import extra.effects.particleEffects.FogWorld;
import extra.effects.particleEffects.SnowWorld;

/**
 * Should contain wind, fog, rain, snow, clouds, sunshine, thunder and lightning
 * @author Mario
 *
 */
public class Weather {

	public FogWorld fog = new FogWorld(200);
	public SnowWorld snow = new SnowWorld(500);
	private World world;
	
	public Weather(World world) {
		this.world = world;
	}
	
	public void addEffects(){
		world.window.addEffect(fog);
		world.window.addEffect(snow);
		snow.spawnUninitialized();
	}
	
	public boolean update(double delta) {
		return false;
	}
}
