package world.window;

import world.data.StructureColumn;
import world.generation.GeneratorInterface;

public class GeneratingWorldWindow extends RealWorldWindow {

	GeneratorInterface gen;
	
	public GeneratingWorldWindow(StructureColumn anchor, int radius, GeneratorInterface gen) {
		super(anchor, radius);
		this.gen = gen;
	}

	int[] count = {0, 0};
	
	public boolean dirOkay(int index) {
		count[index]++;
		if(ends[index].next(index) == null)
			return gen.extend(index);
		else
			return true;
	}
}
