package world.generation.zones.terrain;

import world.data.Column;
import world.generation.BiomeManager;
import world.generation.Material;

public class TerrainElement {

	private BiomeManager bioM;
	
	private double[] yValuesTop;
	
	public TerrainElement(BiomeManager bioM){
		this.bioM = bioM;
	}
	
	public void setTopYs(double[] yValues){
		this.yValuesTop = yValues;
	}
	
	public void addPatch(double x1, double x2, int yIndex, Material mat){
		
	}
	
	/**
	 * Turns all the data given into an array of Columns
	 * @return
	 */
	public Column[] create(){
		//TODO implement this
		return null;
	}
}
