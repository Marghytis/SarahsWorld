package world.generation.environment.modules;

import java.util.function.Function;

import world.generation.zones.useful.Modulation;

public class StructureModulation extends Modulation {

	private Function<Integer, Double> structure;
	private int dx, end;
	private double offset;
	
	public StructureModulation() {
		super(null, 0, 0);
	}
	
	public void startStructure(Function<Integer, Double> structure, int end) {
		dx = 0;
		this.structure = structure;
		this.end = end;
	}
	
	public boolean active() {
		return structure != null;
	}

	public double next() {
		if(active()) {
			if(dx <= end) {
				offset = structure.apply(dx);
			} else if(structure != null){
				structure = null;
				offset = 0;
			}
		}
		dx++;
		return offset;
	}

}
