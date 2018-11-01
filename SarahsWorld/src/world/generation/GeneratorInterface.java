package world.generation;

import java.io.DataOutputStream;

public interface GeneratorInterface {

	/**
	 * Generate terrain up to the specified borders, if not done already.
	 * @param d left border (lower x value)
	 * @param e right border (higher x value)
	 */
	public boolean borders(double d, double e);

	public boolean extendRight();
	
	public boolean extendLeft();
	
	public boolean extend(int iDir);
	/**
	 * Writes the generators current state to a file, so it can be reconstructed from that later.
	 * @param output
	 */
	public void save(DataOutputStream output);
	
}
