package things;

import util.math.Vec;

@FunctionalInterface
public interface OnInteraction {
	public void run(Thing src, Vec worldPos, Thing dest);
}
