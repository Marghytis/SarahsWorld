package basis.effects;

import world.window.TerrainWindow;

public interface WorldEffect extends Effect {

	public int spawn(double x, double y);
	
	/**
	 * Should accept the same int it returned during spawn and delete all there created particles.
	 * @param ticket
	 */
	public void despawn(int ticket);
	
	public void checkInside(TerrainWindow lw);
}
