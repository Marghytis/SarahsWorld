package world.window;

import world.data.Column;
import world.data.Dir;
import world.generation.GeneratorInterface;

public class GeneratingWorldWindow extends RealWorldWindow {

	GeneratorInterface gen;
	
	public GeneratingWorldWindow(Column anchor, int radius, GeneratorInterface gen) {
		super(anchor, radius);
	}

	public boolean dirOkay(int index) {
		if(ends[index].next(index) == null)
			return gen.extend(Dir.s[index]);
		else
			return true;
	}
}
