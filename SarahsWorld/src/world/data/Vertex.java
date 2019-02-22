package world.data;

import moveToLWJGLCore.Loop;
import world.generation.Material;

public class Vertex {
		public static final int maxMatCount = 4;
		public static final Material[] emptyMats = {Material.AIR, Material.AIR, Material.AIR, Material.AIR};
		public static final double[] emptyAlphas = {1, 0, 0, 0};
		public double y;
		private Loop<Material> materials = new Loop<>(4, Material.AIR);
		private Loop<Double> alphs = new Loop<>(4);
		public Material[] mats;
		public double averageSolidity, averageDeceleration, averageBouyancy;
		public int lastMatIndex, firstMatIndex;
		public double transitionHeight;
		public double[] alphas;
		public Column parent;
		public int yIndex;
		
		public boolean prepared;
		public float[] texCoordsPrepared = new float[4];
		
		//empty
		public Vertex(int yIndex) {
			this.yIndex = yIndex;
			this.firstMatIndex = 0;
			this.lastMatIndex = 0;
			this.transitionHeight = 0;
			this.y = 0;
			this.mats = emptyMats;
			this.alphas = emptyAlphas;
			calculateAverage();
		}
		
		public Vertex(int yIndex, Material[] copy, double[] alphas, int firstMatIndex, int lastMatIndex, double transitionHeight, double y) {
			this.yIndex = yIndex;
			this.firstMatIndex = firstMatIndex;
			this.lastMatIndex = lastMatIndex;
			this.alphas = alphas;
			mats = copy;
			calculateAverage();
			this.transitionHeight = transitionHeight;
			this.y = y;
		}
		public Vertex(Vertex toCopy, double newY){
			this(toCopy.yIndex, toCopy.mats, toCopy.alphas, toCopy.firstMatIndex, toCopy.lastMatIndex, toCopy.transitionHeight, newY);
		}
		public Vertex(Vertex toCopy){
			this(toCopy, toCopy.y);
		}
		public double getY() {
			return y;
		}
		public void setNewY(double y) {
			this.y = y;
		}
		public Material[] mats(){return mats;}
		public void enqueueMat(Material mat, double alpha, boolean below){
			if(!below){
				lastMatIndex = (lastMatIndex+1)%maxMatCount;
				mats[lastMatIndex] = mat;
				alphas[lastMatIndex] = alpha;
			} else {
				firstMatIndex = (firstMatIndex+maxMatCount-1)%maxMatCount;
				mats[firstMatIndex] = mat;
				alphas[firstMatIndex] = alpha;
			}
			calculateAverage();
//			alpha = 1 - alpha;
//			for(int i = 0; i < maxMatCount; i++){
//				if(i != nextMatIndex){
//					alphas[i] *= alpha;
//				}
//			}
		}
		public void calculateAverage(){
			averageSolidity = 0;
			averageDeceleration = 0;
			averageBouyancy = 0;
			double totalWeight = 0;
			for(int i = 0; i < maxMatCount; i++){
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
		public boolean empty(){
			for(int i = 0; i < maxMatCount; i++)
				if(alphas[i] != 0 && mats[i].tranparency < 0.7)
					return false;
			return true;
		}
		
	}