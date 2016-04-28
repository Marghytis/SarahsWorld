package world;

import effects.particles.FogWorld;

public class Weather {

	public FogWorld fog = new FogWorld(200);
	
	public void addEffects(){
		WorldWindow.effects.add(fog);
	}
}
