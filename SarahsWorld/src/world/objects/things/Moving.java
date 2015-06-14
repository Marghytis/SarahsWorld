package world.objects.things;

import java.util.Random;

import world.objects.ai.AiAddon;
import world.objects.ai.Animating;
import world.objects.ai.Grounding;
import world.objects.ai.Life;
import world.objects.ai.Position;
import world.objects.ai.Velocity;
import world.things.ThingType;

public class Moving extends Thing2 {

	//PLUGINS
	public Velocity vel;
	public Grounding ground;
	
	Moving(ThingType type, Random random, Animating ani, Position pos, Velocity vel, Life life, Grounding ground, AiAddon... addons) {
		super(type, random, pos, ani, life, addons);
		this.vel = vel;
		this.ground = ground;
	}
}
