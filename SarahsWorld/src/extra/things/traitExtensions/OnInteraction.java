package extra.things.traitExtensions;

import extra.things.Thing;
import util.math.Vec;

@FunctionalInterface
public interface OnInteraction {
	public void run(Thing src, Vec worldPos, Thing dest);
}
