package world.generation.zones;

import java.util.Random;

import util.math.Vec;
import world.generation.BiomeManager;
import world.generation.zones.useful.*;

public class Birthday extends Useful {

	private Section[] sections;
	private int sectionIndex;
	
	public Birthday(Random random, BiomeManager biome, double originX, boolean left, double startHeight,
			double amplifierX, double amplifierY, double aimWidth, boolean[] description) {
		super(random, biome, originX, aimWidth, startHeight, left);
		sections = new Section[]{};
	}
	
	public double step(double x){
		
		if(sections[sectionIndex].ready()){
			startNextSection(x);
		}
		
		return super.step(x);
	}
	
//	private void startNextSection(double x){
//		spline.transitionTo(x, sections[sectionIndex].dest, sections[sectionIndex].mods);
//	}

	public abstract class Section {
		public abstract boolean ready();
		
		public Vec dest;
		public EvenedModulation[] mods;
	}
}
