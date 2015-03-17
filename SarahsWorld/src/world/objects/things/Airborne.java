package world.objects.things;

import java.util.Random;

import world.objects.ThingType;
import world.objects.ai.Acceleration;
import world.objects.ai.AiAddon;
import world.objects.ai.Animating;
import world.objects.ai.Grounding;
import world.objects.ai.Life;
import world.objects.ai.Position;
import world.objects.ai.Velocity;

public class Airborne extends Moving {
	
	public Acceleration acc;

	public Airborne(ThingType type, Random random, Animating ani, Position pos,	Velocity vel, Life life, Grounding ground, Acceleration acc, AiAddon... addons) {
		super(type, random, ani, pos, vel, life, ground, addons);
	}
	
}
