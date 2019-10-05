package world.data;

import moveToLWJGLCore.Loop;
import world.generation.Material;

/**
 * A point in the grid that makes up the terrain of the world. Since it belongs to a Column, only the y coordinate is stored here.
 * Also at each Vertex up to 'maxMatCount' different Materials with different ratios of mixture may be present and are contained in this class.
 * @author Mario
 *
 */
public class Vertex {
		public static final int maxMatCount = 4;
		public static final Material[] emptyMats = {Material.AIR, Material.AIR, Material.AIR, Material.AIR};
		public static final double[] emptyAlphas = {1, 0, 0, 0};
		
		private double y;
		private Loop<Material> materials = new Loop<>(4, Material.AIR);
		private Loop<Double> alphs = new Loop<>(4);//TODO use these Loops?
		private Material[] mats;
		private double averageSolidity, averageDeceleration, averageBouyancy;
		private int lastMatIndex, firstMatIndex;
		private double transitionHeight;
		private double[] alphas;
		private Column parent;
		private int yIndex;
		
		/**
		 * This boolean says whether the texture coordinates 'texCoordsPrepared' fit to the vertex's real coordinates. If not, the vertex has to be prepared before being put into a vbo.
		 */
		private boolean prepared;
		private float[] texCoordsPrepared = new float[4];
		
		//empty Vertex
		public Vertex(int yIndex) {
			this.yIndex = yIndex;
			this.firstMatIndex = 0;
			this.lastMatIndex = 0;
			this.transitionHeight = 0;
			this.y = 0;
			this.mats = emptyMats;
			this.alphas = emptyAlphas;
			prepared = false;
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
		
		/**
		 * Set the tex coords. This function flags this Vertex as prepared
		 * @param texCoords
		 */
		public void prepare(float... texCoords) {
			for(int i = 0; i < texCoords.length; i++) {
				texCoordsPrepared[i] = texCoords[i];
			}
			prepared = true;
		}
		
		public void setUnprepared() {
			prepared = false;
		}
		
		/**
		 * Add a new material to this vertex.
		 * @param mat The Material to add
		 * @param alpha The corresponding opaqueness of the material
		 * @param below Whether it should be added below or above the others
		 */
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
		}
		
		/**
		 * Calculates material alpha-weighed averages of solidity, deceleration and buoyancy and saves them in this instance.
		 */
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
		
		/**
		 * 
		 * @return Whether there are any visible vertices with a transparency below 0.7 
		 */
		public boolean empty(){
			for(int i = 0; i < maxMatCount; i++)
				if(alphas[i] != 0 && mats[i].tranparency < 0.7)
					return false;
			return true;
		}

		/**
		 * 
		 * @param index index of the material to change
		 * @param factor Factor to scale alpha value with
		 */
		public void scaleAlpha(int index, double factor) {
			alphas[index] *= factor;
		}
		
		//Getters
		public float getPreparedTexCoord(int index) {			return texCoordsPrepared[index];		}
		public boolean isPrepared() {							return prepared;		}
		public Column getParent() {								return parent;		}
		public int getYIndex() {								return yIndex;		}
		public double getTransitionHeight() {					return transitionHeight;		}
		public int getFirstMatIndex() {							return firstMatIndex;		}
		public double getAverageSolidity() {					return averageSolidity;		}
		public double getAverageDeceleration() {				return averageDeceleration;		}
		public double getAverageBouyancy() {					return averageBouyancy;		}
		public double getX() {									return parent.getX();		}
		public double getY() {									return y;		}
		public double y() {										return getY();		}
		public Material mats(int index) {						return mats[index];}
		public Material[] mats(){								return mats;}
		public double alpha(int index) {						return alphas[index];	}
		
		//Setters
		public void setNewY(double y) {							this.y = y;		prepared = false;}
		public void setParent(Column c) {						this.parent = c;		}
		public void setTransitionHeight(int th) {				this.transitionHeight = th;	prepared = false;}
}