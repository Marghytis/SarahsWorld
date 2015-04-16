package world.worldGeneration;

import java.util.Arrays;

import world.Material;
import world.worldGeneration.WorldData.Column;
import world.worldGeneration.WorldData.Vertex;
import data.IndexBuffer;

public class BiomeManager {

	public Ant[] ants;
	public Column lastColumn;//for the old vertices to change
	public World world;
	/**
	 * 
	 * @param start : necessary to prevent errors later
	 */
	public BiomeManager(World world, Biome start){
		this.world = world;
		ants = new Ant[World.layerCount];
		for(int yIndx = 0; yIndx < ants.length; yIndx++){
			ants[yIndx] = new Ant(start.stratums[yIndx]);
		}
	}
	
	//Idea of the day: Just go back the Line, if the biome has to change!!! TODO
	public void switchToBiome(Biome newBiome){
//		each new stratum gets attached
		for(Stratum stratum : newBiome.stratums){
			
			int yIndex = stratum.layerIndex;
			
			//it shoudn't happen that there's not already a material
			int transition = Math.min(stratum.transitionWidth, ants[yIndex].transitions.write.previous.data.width);
			if(transition <= 0){//This means, the new material will be fully visible instantly,
								//so we can firstly clear the buffers
				ants[yIndex].mats.clear();
				ants[yIndex].transitions.clear();	
			}

			//add another material to the vertex
			ants[yIndex].mats.enqueue(stratum.material);
			//add the smallest transition width to the vertex for this material
			ants[yIndex].transitions.enqueue(new Alpha(transition));

			ants[yIndex].resetAlphas();
			ants[yIndex].resize(stratum.thickness, stratum.sizingSpeed);
			//TODO change old vertices here (left in your thinking)
		}
	}
	
	public Vertex[] createVertices(double yTop){
		
		Vertex[] out = new Vertex[World.layerCount];
		
		double y = yTop;
		for(int i = 0; i < out.length; i++){
			out[i] = world.data.new Vertex();
			out[i].mats = ants[i].mats.copy();
			out[i].alphas = Arrays.copyOf(ants[i].alphas, Vertex.maxMatCount);
			out[i].y = y;
			
			y -= ants[i].thickness;
		}
		return out;
	}
	
	public void step(){
		for(Ant a : ants){
			a.step();
		}
		
	}
	
	public class Ant {
		public IndexBuffer<Material> mats;
		public double[] alphas;
		public IndexBuffer<Alpha> transitions;
		
		public double thickness;
		public double aimThickness;
		public double sizingSpeed;
		public boolean reachedSize;
		
		public Ant(Stratum stratum){
			mats = new IndexBuffer<>(Vertex.maxMatCount);
			transitions = new IndexBuffer<>(Vertex.maxMatCount);
			alphas = new double[Vertex.maxMatCount];
			if(stratum != null){
				mats.enqueue(stratum.material);
				Alpha alpha = new Alpha(stratum.transitionWidth);
				alpha.finished = true;
				transitions.enqueue(alpha);
				this.thickness = stratum.thickness;
				this.reachedSize = true;
			} else {
				mats.enqueue(Material.NO);
				transitions.enqueue(new Alpha(0));
				this.reachedSize = true;
			}
			resetAlphas();
		}

		public void resize(double thickness, double sizingSpeed) {
			this.aimThickness = thickness;
			this.sizingSpeed = sizingSpeed;
			this.reachedSize = false;
		}

		public void resetAlphas(){
			//for each active material its alpha value get determined. Also a single material is safe
			for(IndexBuffer<Alpha>.Node cursor = transitions.write.previous; cursor != transitions.read.previous; cursor = cursor.previous){
				
				alphas[cursor.index] = cursor.data.getAlpha();
				//if you wouldn't see the other mats either, you can safely remove them
				if(alphas[cursor.index] >= 1){
					while(transitions.read != cursor){
						alphas[transitions.read.index] = 0;
						transitions.dequeue();
					}
					break;
				}
			}
		}
		
		public void step(){
			for(IndexBuffer<Alpha>.Node cursor = transitions.read; cursor != transitions.write; cursor = cursor.next){
				cursor.data.step();
			}
			if(!reachedSize){
				if(thickness > aimThickness && thickness - sizingSpeed > aimThickness){
					thickness -= sizingSpeed;
				} else if(thickness < aimThickness && thickness + sizingSpeed < aimThickness){
					thickness += sizingSpeed;
				} else {
					reachedSize = true;
				}
			}
		}
	}
	public class Alpha {
		public int width;
		public int x;
		public boolean finished;
		
		public Alpha(int width){
			this.width = width;
			if(width == 0) finished = true;
		}
		
		public double getAlpha(){
			return finished ? 1 : (double)x/width;
		}
		
		public void step(){
			if(!finished){
				x++;
				finished = finished();
			}
		}
		
		public boolean finished(){
			return x > width;
		}
	}
}
