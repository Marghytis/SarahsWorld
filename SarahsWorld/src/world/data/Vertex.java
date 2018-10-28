package world.data;

import world.Material;

public class Vertex {
		public static final int maxMatCount = 4;
		public double y;
		public Material[] mats;
		public double averageSolidity, averageDeceleration, averageBouyancy;
		public int lastMatIndex, firstMatIndex;
		public double transitionHeight;
		public double[] alphas;
		public Column parent;
		public int yIndex;
		
		public boolean prepared;
		public float[] texCoordsPrepared = new float[4];
		
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
				if(alphas[i] != 0)
					return false;
			return true;
		}
		
	}