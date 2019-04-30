package basis.entities;

import util.math.Vec;

public interface InteractionAttribute<T extends Entity> {

	public void onInteraction(T src, Vec location);
}
