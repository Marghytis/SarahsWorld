package world.data;

import world.generation.Material;

public class MaterialVertex {
	
	private static final double emptyAlphaThreshold = 0.7;

	private Material[] mats;
	private double[] alphas;
	private int lastMatIndex, firstMatIndex;
	
	private double averageSolidity, averageDeceleration, averageBouyancy;
	
	/**
	 * Empty vertex with only AIR
	 * @param yIndex
	 */
	public MaterialVertex(int yIndex) {
		mats = new Material[Vertex.maxMatCount];
		for(int i = 0; i < mats.length; i++) {
			mats[i] = Material.AIR;
		}

		firstMatIndex = 0;
		lastMatIndex = 0;
		
		alphas = new double[mats.length];
		alphas[firstMatIndex] = 1;
		
		updateAverages();
	}
	
	public MaterialVertex(Material[] mats, double[] alphas, int firstMatIndex, int lastMatIndex) {
		
	}
	
	/**
	 * Add a new material to this vertex.
	 * @param mat The Material to add
	 * @param alpha The corresponding opaqueness of the material
	 * @param below Whether it should be added below or above the others
	 */
	public void enqueueMat(Material mat, double alpha, boolean below){
		if(!below){
			lastMatIndex = (lastMatIndex+1)%Vertex.maxMatCount;
			mats[lastMatIndex] = mat;
			alphas[lastMatIndex] = alpha;
		} else {
			firstMatIndex = (firstMatIndex+Vertex.maxMatCount-1)%Vertex.maxMatCount;
			mats[firstMatIndex] = mat;
			alphas[firstMatIndex] = alpha;
		}
		updateAverages();
	}
	
	public boolean isEmpty() {
		for(int i = 0; i < Vertex.maxMatCount; i++)
			if(alphas[i] != 0 && mats[i].tranparency < emptyAlphaThreshold)
				return false;
		return true;
	}
	
	/**
	 * Always call this method after the contents of mats or alphas changed
	 */
	private void updateAverages() {
		averageSolidity = 0;
		averageDeceleration = 0;
		averageBouyancy = 0;
		double totalWeight = 0;
		for(int i = 0; i < mats.length; i++){
			if(mats[i] != Material.AIR){
				totalWeight += alphas[i];
				averageSolidity += alphas[i]*mats[i].solidity;
				averageDeceleration += alphas[i]*mats[i].deceleration;
				averageBouyancy += alphas[i]*mats[i].bouyancy;
			}
		}
		if(totalWeight != 0){
			averageSolidity /= totalWeight;
			averageDeceleration /= totalWeight;
			averageBouyancy /= totalWeight;
		}
	}

	public double getAlpha(int indexAfterFirst) {				return alphas[indexAfterFirst];		}
	public Material getMaterial(int indexAfterFirst) {			return mats[indexAfterFirst];		}
}
