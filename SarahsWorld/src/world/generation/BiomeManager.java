package world.generation;

import java.util.ArrayList;
import java.util.List;

import util.math.Vec;
import world.World;
import world.data.Column;
import world.data.Vertex;

public class BiomeManager {

	public Biome biome;
	public Ant[] ants;
	private ColorAnt sky;
	public Column lastColumn;//for the old vertices to change
	public boolean left;
	public List<Spawner> extraSpawns = new ArrayList<>();
	Vec posField = new Vec();
	
	boolean first = true;
	Biome last;

	/**
	 * 
	 * @param normal : necessary to prevent errors later
	 */
	public BiomeManager(Biome normal, boolean left){
		this.left = left;
		this.biome = normal;
		this.last = biome;
		this.ants = new Ant[Biome.layerCount];
		for(int yIndx = 0; yIndx < ants.length; yIndx++){
			this.ants[yIndx] = new Ant(normal.stratums[yIndx], left);
		}
		this.sky = new ColorAnt(this, biome.topColor, biome.lowColor);
	}
	
	/**
	 * Update the lastColumn later!!!
	 */
	public void step(){
		sky.step();
		for(Ant a : ants){
			a.step();
		}
	}
	
	//Idea of the day: Just go back the Line, if the biome has to change!!! TODO
	/**
	 * If the stratum in an ant stays the same, it does nothing there
	 * modify 0 <= dx <= colorTransition/2 column colors
	 * @param newBiome
	 */
	public void switchToBiome(Biome newBiome){
		if(!first){
			//sky color
			switchSkyColor(newBiome);
			
			//biome
			switchBiome(newBiome);
			
			//vertices
			updateAnts(newBiome);
		} else {
			//biome
			first = false;
			switchBiome(this.biome);
		}
	}
	
	private void updateAnts(Biome newBiome) {
		//		each new stratum gets attached
		for(int yIndex = 0; yIndex < newBiome.stratums.length; yIndex++){
			if(ants[yIndex].stratum != newBiome.stratums[yIndex]){
				ants[yIndex].switchTo(newBiome.stratums[yIndex], yIndex, lastColumn);
			}
		}
	}
	
	private void switchBiome(Biome newBiome) {
		this.last = biome;
		this.biome = newBiome;
	}
	
	private void switchSkyColor(Biome newBiome) {
		sky.switchBiome(newBiome);
	}

	public Column createColumn(double height, double... collisionVecs) {
		return new Column(0, biome, sky.getTop(), sky.getLow(), createVertices(height), collisionVecs);
	}
	
	public Vertex[] createVertices(double yTop){
		
		Vertex[] out = new Vertex[Biome.layerCount];
		
		double y = yTop;
		for(int i = 0; i < out.length; i++){
			//otherwise there are ugly borders at the end of a layer
//			double transitionHeight = ants[i].stratum == null ? 0 : ants[i].transitionHeight*(ants[i].thickness/ants[i].stratum.thickness);
			
			//to prevent stripes and invisible connections between patches (lakes)
//			boolean make0 = ants[i].thickness == 0 && ants[i].reachedSize;
			
			//put water a bit deeper so the lake is not filled to the rim
			double apparentY = y;
			double transHeight = ants[i].transitionHeight;
			if(ants[i].mats[ants[i].index.current] == Material.WATER){
				if(ants[i].thickness > 10){
					apparentY = y - 10;
					transHeight *= (ants[i].thickness-10)/ants[i].thickness;
				} else {
					apparentY = y - ants[i].thickness;
//					make0 = true;
					transHeight = 0;
				}
			}
			Material[] outMat = new Material[ants[i].mats.length];
			double[] outAlpha = new double[ants[i].transitions.length];
			System.arraycopy(ants[i].mats, 0, outMat, 0, ants[i].mats.length);
			System.arraycopy(ants[i].realAlphas, 0, outAlpha, 0, ants[i].realAlphas.length);//if(!make0)
			
			out[i] = new Vertex(i, outMat, outAlpha, ants[i].index.first, ants[i].index.last, transHeight, apparentY);
			
			y -= ants[i].thickness;
		}
		return out;
	}
	
	/**
	 * This should be called from the Generator with an offset of two steps (because the things may only be spawned after the World is there (don't know exaclty why :D))
	 * @param c
	 */
	public void spawnThings(Column c){
		c.getBiome().spawnThings(c);
		for(Spawner sp : extraSpawns){
			sp.spawn(c.getRandomTopLocation(World.rand, posField), posField.copy());
		}
		extraSpawns.clear();
	}
	
	public enum State {//A patch of material may not blend into AIR!!
		VISIBLE,   //MATERIAL, ALPHA, SIZE:	It has at least one material with a positive alpha value and the size is positive too;
//		ZERO_ALPHA,//MATERIAL, SIZE:		It still has a material (for the renderer to track it) and a positive size, but its alpha is 0.(is contained in VISIBLE)
		ZERO_SIZE, //MATERIAL, ALPHA:		It still has a visible material, but the size is zero. It should go to NOTHING next, if it doesn't grow again.
		NOTHING    //:						It is neither at the beginning, the end nor in the middle of a patch of any material.
	}
	
}
