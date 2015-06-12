package world.worldGeneration;

import world.Material;
import world.worldGeneration.WorldData.Column;
import world.worldGeneration.WorldData.Vertex;
import data.IndexBuffer;

public class BiomeManager {

	public Biome biome;
	public Ant[] ants;
	public Column lastColumn;//for the old vertices to change
	public WorldData world;
	public boolean left;
	/**
	 * 
	 * @param normal : necessary to prevent errors later
	 */
	public BiomeManager(WorldData world, Biome normal, boolean left){
		this.left = left;
		this.world = world;
		ants = new Ant[World.layerCount];
		for(int yIndx = 0; yIndx < ants.length; yIndx++){
			ants[yIndx] = new Ant(normal.stratums[yIndx]);
		}
		this.biome = normal;
	}
	
	boolean first = true;
	//Idea of the day: Just go back the Line, if the biome has to change!!! TODO
	public void switchToBiome(Biome newBiome){
		if(!first){
			this.biome = newBiome;
	//		each new stratum gets attached
			int yIndex = 0;
			for(Stratum stratum : newBiome.stratums){
				
				ants[yIndex].switchTo(stratum, yIndex);
				yIndex++;
			}
		} else {
			first = false;
		}
	}
	
	public Vertex[] createVertices(double yTop){
		
		Vertex[] out = new Vertex[World.layerCount];
		
		double y = yTop;
		for(int i = 0; i < out.length; i++){
			out[i] = world.new Vertex(ants[i].mats.copy(), ants[i].getAlphas(), ants[i].transitionHeight, y);
			
			y -= ants[i].thickness;
		}
		return out;
	}
	
	public void step(Column lastColumn){
		this.lastColumn = lastColumn;
		for(Ant a : ants){
			a.step();
		}
	}
	
	public class Ant {
		public IndexBuffer<Material> mats;//all the mats active in this layer
		public IndexBuffer<Transitioner> transitions;//parallel index buffer to the mats with the transitions for each material
		
		public double thickness;//thickness of the layer
		public double aimThickness;//aim thickness of the layer
		public double sizingSpeed;//speed with which the layer gets to the aim thickness
		public boolean reachedSize;//if the layer has reached the aim thickness
		
		public double transitionHeight;
		
		public Ant(Stratum stratum){
			mats = new IndexBuffer<>(Vertex.maxMatCount);
			transitions = new IndexBuffer<>(Vertex.maxMatCount);
			if(stratum != null){
				Transitioner alpha = new Transitioner(stratum.transitionWidth);
				alpha.finished = true;
				enqueueMat(stratum.material, alpha);
				this.thickness = stratum.thickness;
				this.transitionHeight = stratum.transitionHeight;
			}
			//ants get created only once per generator and layer and then they should be already full size
			this.reachedSize = true;
		}
		
		public void enqueueMat(Material mat, Transitioner trans){
			mats.enqueue(mat);
			transitions.enqueue(trans);
		}
		
		public void dequeueMat(){
			mats.dequeue();
			transitions.dequeue();
		}

		public double[] getAlphas() {
			double[] alphas = new double[Vertex.maxMatCount];
			//for each active material its alpha value gets determined. Also a single material is safe
			for(IndexBuffer<Transitioner>.Node cursor = transitions.read; cursor != transitions.write; cursor = cursor.next){
				alphas[cursor.index] = cursor.data.getAlpha();
			}
			return alphas;
		}
		
		public void removeInvisibleMats(){
			for(IndexBuffer<Transitioner>.Node cursor = transitions.write.previous; cursor != transitions.read.previous; cursor = cursor.previous){
				//if you wouldn't see the other mats either, you can safely remove them
				if(cursor.data.finished){
					while(transitions.read != cursor){
						dequeueMat();
					}
					break;
				}
			}
		}
		
		public void switchTo(Stratum stratum, int yIndex){
			if(stratum != null){
				

				//add another material to the vertex
				//add the smallest transition width to the vertex for this material
				this.aimThickness = stratum.thickness;
				this.sizingSpeed = stratum.sizingSpeed;
				this.reachedSize = false;
				
				this.transitionHeight = stratum.transitionHeight;
				
				//If its the same material there is no need to change it..
				if(stratum.material != mats.write.previous.data){
					int transition = stratum.transitionWidth;
					if(transitions.empty()) transition = 0;//New layer starts in between others
					else if(transitions.write.previous.data.width < transition) transition = transitions.write.previous.data.width;
					if(transition <= 0){//This means, the new material will be fully visible instantly,
						//so we can firstly clear the buffers
						mats.clear();
						transitions.clear();
					}
					
					enqueueMat(stratum.material, new Transitioner(transition));
					//TODO change old vertices here (left in your thinking)
					Column c = lastColumn;
					for(int x = 1; x <= transition; x++){//but if right, it goes to the left!!
						if(c != null){
							double alpha = (1-((double)x/transition))*0.5;
							
							c.vertices[yIndex].mats.enqueue(stratum.material);
							c.vertices[yIndex].alphas[mats.write.previous.index] = alpha;
							
							c = left ? c.right : c.left;//
						}
					}
				}
				
			} else {//just get smaller until its at 0 size
				this.aimThickness = 0;
				this.sizingSpeed = -sizingSpeed;
				this.reachedSize = false;
			}
		}

		public void step(){
			for(IndexBuffer<Transitioner>.Node cursor = transitions.read; cursor != transitions.write; cursor = cursor.next){
				cursor.data.step();
			}
			removeInvisibleMats();
			if(!reachedSize){
				if(thickness > aimThickness && thickness - sizingSpeed > aimThickness){
					thickness -= sizingSpeed;
				} else if(thickness < aimThickness && thickness + sizingSpeed < aimThickness){
					thickness += sizingSpeed;
				} else {
					thickness = aimThickness;
					reachedSize = true;
				}
			}
		}
	}
	public class Transitioner {//only lets the alpha rise
		public int width;
		public int x;
		public boolean finished;
		
		public Transitioner(int width){
			this.width = width;
			if(width == 0) finished = true;
		}
		
		public double getAlpha(){
			return finished ? 1 : (0.5*x)/width + 0.5;
		}
		
		public void step(){
			if(!finished){
				x++;
				finished = finished();
			}
		}
		
		public boolean finished(){
			return x >= width;
		}
	}
}
