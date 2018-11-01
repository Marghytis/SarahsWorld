package world.generation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import util.Color;
import util.math.Function;
import util.math.UsefulF;
import util.math.Vec;
import world.World;
import world.data.Column;
import world.data.Vertex;
import world.data.WorldData;

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
		ants = new Ant[Biome.layerCount];
		for(int yIndx = 0; yIndx < ants.length; yIndx++){
			ants[yIndx] = new Ant(normal.stratums[yIndx]);
		}
		this.biome = normal;
		setTop(biome.topColor);
		setLow(biome.lowColor);
		last = biome;
	}
	public List<Spawner> extraSpawns = new ArrayList<>();
	Vec posField = new Vec();
	/**
	 * This should be called from the Generator with an offset of two steps (because the things may only be spawned after the World is there (don't know exaclty why :D))
	 * @param c
	 */
	public void spawnThings(Column c){
		c.biome.spawnThings(world, c);
		for(Spawner sp : extraSpawns){
			sp.spawn(world, c.getRandomTopLocation(World.rand, posField), posField.copy());
		}
		extraSpawns.clear();
	}
	
	boolean first = true;
	Biome last; int dx;
	static int colorTransition = 40;
	private Color top;
	private Color low;
	//Idea of the day: Just go back the Line, if the biome has to change!!! TODO
	/**
	 * If the stratum in an ant stays the same, it does nothing there
	 * modify 0 <= dx <= colorTransition/2 column colors
	 * @param newBiome
	 */
	public void switchToBiome(Biome newBiome){
		dx = colorTransition/2;
		if(!first){
			for(Column c = lastColumn;dx >= 0 && c != null; dx--, c = left? c.right : c.left){
				c.topColor = newBiome.topColor.minus(biome.topColor).scale((float)dx/colorTransition).add(biome.topColor);
				c.lowColor = newBiome.lowColor.minus(biome.lowColor).scale((float)dx/colorTransition).add(biome.lowColor);
			}
			setTop(newBiome.topColor.minus(biome.topColor).scale(0.5f).add(biome.topColor));
			setLow(newBiome.lowColor.minus(biome.lowColor).scale(0.5f).add(biome.lowColor));
			dx = colorTransition/2;
			this.last = biome;
			this.biome = newBiome;
	//		each new stratum gets attached
			int yIndex = 0;
			for(Stratum stratum : newBiome.stratums){
				if(ants[yIndex].stratum != stratum){
					ants[yIndex].switchTo(stratum, yIndex);
				}
				yIndex++;
			}
		} else {
			first = false;
			last = this.biome;
		}
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
	 * Update the lastColumn later!!!
	 */
	public void step(){
		if(dx <= colorTransition){
			setTop(biome.topColor.minus(last.topColor).scale((float)dx/colorTransition).add(last.topColor));
			setLow(biome.lowColor.minus(last.lowColor).scale((float)dx/colorTransition).add(last.lowColor));
			dx++;
		}
		for(Ant a : ants){
			a.step();
		}
	}
	
	public Color getTop() {
		return top;
	}

	public void setTop(Color top) {
		this.top = top;
	}

	public Color getLow() {
		return low;
	}

	public void setLow(Color low) {
		this.low = low;
	}

	public class Ant {
		public State state;
		public Material[] mats; Indices index;//all the mats active in this layer
		public boolean[] matDisappeared;
		public double[] realAlphas;//these have to add up to 1
		AlphaFunc transitions[];
		
		public double thickness;//thickness of the layer
		public double aimThickness;//aim thickness of the layer
		public double transitionHeight;//transition height downwards of the layer
		public double aimTransitionHeight;//the transition height has to transition too. (linked to thickness transition)
		public double sizingSpeed;//speed with which the layer gets to the aim thickness
		public boolean reachedSize;//if the layer has reached the aim thickness
		public Stratum stratum;
		
		int x;
		int iX;
		Function resizer;//old: f(x) = startThickness + x*sizingSpeed
		Function transitionHeightResizer;
//		ThicknessFunc thicknessF;
		
		public Ant(Stratum stratum){
			//initialize index object and arrays
			this.index = new Indices(Vertex.maxMatCount, left);
			this.mats = new Material[Vertex.maxMatCount]; Arrays.fill(mats, Material.AIR);
			this.matDisappeared = new boolean[mats.length];
			this.transitions = new AlphaFunc[Vertex.maxMatCount]; for(int i = 0; i < Vertex.maxMatCount; i++) transitions[i] = new AlphaFunc();
			this.realAlphas = new double[Vertex.maxMatCount];
			
			//if stratum is not empty, initialize the ant to it
			if(stratum != null && stratum.material != Material.AIR){
				setToStratumInstantly(stratum);
			} else {
				this.state = State.NOTHING;
			}
			
//			thicknessF.stayAt(0, 0, 0);
			this.reachedSize = true;
			for(int i = 0; i < Vertex.maxMatCount; i++) transitions[i].updateAlpha(iX);
			setRealAlphas();
		}
		public void setToStratumInstantly(Stratum stratum){
			transitionToMaterialInSteps(stratum.material, 0);
			
			//ants get created only once per generator and layer and then they should be already full size
			this.stratum = stratum;
			this.thickness = stratum.thickness;
			this.transitionHeight = stratum.transitionHeight;
			this.sizingSpeed = stratum.sizingSpeed;
//			thicknessF.stayAt(stratum.thickness, stratum.transitionHeight, stratum.sizingSpeed);
			this.state = State.VISIBLE;
		}
		public void switchTo(Stratum stratum, int yIndex){
			switchTo(stratum, yIndex, Math.min(this.stratum != null ? this.stratum.sizingSpeed : stratum.sizingSpeed, stratum.sizingSpeed));
		}
		public void switchTo(Stratum stratum, int yIndex, double speed){
			if(stratum != null && stratum.material != Material.AIR){
				
				//IF MATERIAL CHANGES -> ALPHA TRANSITION
				if(stratum.material != mats[index.current]){//If the material changes, apply the new material to this ant and the old vertices
					int transition;
					if(state == State.NOTHING){//empty: new layer starts in between others/width = 0: instant appearing
						transition = 0;
					} else {
						transition = Math.min(this.stratum.transitionWidth, stratum.transitionWidth);//choose the faster transition
					}
					transitionToMaterialInSteps(stratum.material, transition);
					
					//TODO change old vertices thickness here (left in your thinking)
					Column c = lastColumn;
					for(int x = -1; x >= -transition; x--){
						
						if(left){//if left, add mat below and lower the other mats alphas
							for(int i = 0; i < index.size; i++){
								if(i != index.current){
									c.vertices[yIndex].alphas[i] *= 0.5f*(1 - (float)x/transition);//x is in [-transition, -1] < 0
								}
							}
							c.vertices[yIndex].enqueueMat(stratum.material, 1, left);
						} else {//if right, add mat on top and let its alpha rise
							float alpha = transitions[index.current].get(x + iX);
							c.vertices[yIndex].enqueueMat(stratum.material, alpha, left);
						}
						c = left ? c.right : c.left;
					}
				}
				this.stratum = stratum;
				//THICKNESS TRANSITION
				resize(stratum.thickness, stratum.transitionHeight, speed);
				//PATCH APPEARS FROM NOTHINGNESS
				if(state == State.NOTHING || state == State.ZERO_SIZE)
					state = State.VISIBLE;

			} else {//just get smaller until its at 0 size and becomes ZERO_SIZE and then NOTHING
				resize(0, 0, sizingSpeed);
				//don't make the stratum null without thinking, because in createVertices() stratum == null? is used
			}
		}
		public void transitionToMaterialInSteps(Material mat, int stepCountHalf){
			if(stepCountHalf != 0){
				if(!left){
					transitions[index.next].setAlpha(0.5f);
					transitions[index.next].appearIn(stepCountHalf);
				} else {
					transitions[index.next].stayAt(1);
					for(int i = 0; i < index.size; i++){
						if(i != index.next){
							transitions[i].setAlpha(0.5f*transitions[i].alpha);
							transitions[i].disappearIn(stepCountHalf);
						}
					}
				}
				mats[index.next] = mat;
			} else if(mat != Material.AIR){//fully visible instantly
				transitions[index.next].stayAt(1);
				mats[index.next] = mat;
			}
			index.step();
			setRealAlphas();
		}

		public void step(){
			iX++;
			if(state == State.VISIBLE){
				//HORIZONTAL ALPHA TRANSITION  |||| REMOVE INVISIBLE MATS (that should've been rendered once with alpha=0
				for(int i = 0; i < Vertex.maxMatCount; i++){
					transitions[i].updateAlpha(iX);
				}
				//for each active material its real alpha value gets determined.
				setRealAlphas();
				
				//HOZONTAL THICKNESS TRANSITION
//				thicknessF.updateThicknessAndTransHeight(iX);
				if(!reachedSize){
					if(thickness > aimThickness){
						
						thickness = resizer.f(x);
						transitionHeight = transitionHeightResizer.f(x);
						
						if(thickness <= aimThickness){
							reachedSize = true;
							thickness = aimThickness;//Just to be sure...
							transitionHeight = aimTransitionHeight;
						}
					} else if(thickness < aimThickness){
	
						thickness = resizer.f(x);
						transitionHeight = transitionHeightResizer.f(x);
						
						if(thickness >= aimThickness){
							reachedSize = true;
							thickness = aimThickness;
							transitionHeight = aimTransitionHeight;
						}
					} else if(thickness == aimThickness){//happens at zero for example
						reachedSize = true;
					}
					if(reachedSize && aimThickness == 0){//This means the layer is not visible anymore
						state = State.ZERO_SIZE;
						stratum = null;
					}
				}
				x++;
			} else {//Patch shrunk to zero.
				
				for(int i = 0; i < Vertex.maxMatCount; i++){
					transitions[i].stayAt(0);
					realAlphas[i] = 0;
					mats[i] = Material.AIR;
				}
				state = State.NOTHING;
			}
		}
		
		public void setRealAlphas(){
			int i = index.last;
			boolean covered = false;
			do {
				if(transitions[i].alpha > 0 && !covered){
					realAlphas[i] = transitions[i].alpha;
					if(realAlphas[i] == 1) covered = true;
					matDisappeared[i] = false;
				} else if(mats[i] != Material.AIR){//remove invisible materials from the list.
					if(matDisappeared[i]){
						mats[i] = Material.AIR;
						realAlphas[i] = 0;
						transitions[i].stayAt(0);
						matDisappeared[i] = false;
						if(i == index.first){
							do {
								index.stepTail();
							} while(mats[index.first] == Material.AIR && index.first != i);
						}
					} else {
						matDisappeared[i] = true;
					}
				}
				i = index.priorTo(i);
			} while(i != index.priorToFirst);
		}
		
		public void resize(Stratum aimStratum, double speed){
			resize(aimStratum.thickness, aimStratum.transitionHeight, speed);
		}
		
		public void resize(double aimThickness, double aimTransitionHeight, double speed){
			this.aimThickness = aimThickness;
			this.aimTransitionHeight = aimTransitionHeight;
			this.sizingSpeed = speed;
			this.reachedSize = false;
			double start = thickness;
			double startTransitionHeight = transitionHeight;
			x = 1;
			final double dy = aimThickness - start;
			final double transitionDy = aimTransitionHeight - startTransitionHeight;
			final double dx = Math.abs(dy/sizingSpeed);
			resizer = (x) -> {
				if(x > dx){
					return aimThickness;//there were problems with the generation steps jumping over the maximum of the cubic function
				} else {
					return start + (UsefulF.cubicUnit.f(x/dx)*dy);
				}
			};
			transitionHeightResizer = (x) -> {
				if(x > dx){
					return aimTransitionHeight;
				} else {
					return startTransitionHeight + (x/dx)*transitionDy;
				}
			};
		}
		
		public class Indices {
			public int first, last, size, next, afterLast, priorToFirst, current;
			public boolean left, empty;
			public Indices(int size, boolean left){
				this.size = size;
				this.left = left;
				
				empty = true;
				
				next = 0;
				if(!left){
					first = next;
					last = priorTo(last);
				} else {
					last = next;
					first = after(last);
				}
			}
			public void step(){
				current = next;
				if(left){
					first = priorToFirst;
					priorToFirst = priorTo(priorToFirst);
					next = priorToFirst;
					if(first == last && !empty)
						last = priorTo(last);
					empty = false;
				} else {
					last = afterLast;
					afterLast = after(afterLast);
					next = afterLast;
					if(last == first && !empty)
						first = after(first);
					empty = false;
				}
			}
			public void stepTail(){
				if(left){
					if(!empty){
						afterLast = last;
						last = priorTo(last);
						if(last == priorToFirst)
							empty = true;
					}
				} else {
					if(!empty){
						priorToFirst = first;
						first = after(first);
						if(first == afterLast)
							empty = true;
					}
				}
			}
			public int after(int i){	return (i+1)%size;		}
			public int priorTo(int i){ 	return (i+size-1)%size;	}
		}
	
		public class AlphaFunc {
			float aPerX;
			int start;
			float alpha0;
			public float alpha;
			public void setAlpha(float alpha){
				this.alpha = alpha;
			}
			public void disappearIn(int dx){
				alpha0 = alpha;
				aPerX = -alpha0/dx;
				start = iX;
			}
			public void appearIn(int dx){
				alpha0 = alpha;
				aPerX = (1-alpha0)/dx;
				start = iX;
			}
			public void stay(){
				alpha0 = alpha;
				aPerX = 0;
				start = iX;
			}
			public void stayAt(float alpha){
				this.alpha = alpha;
				stay();
			}
			public void disappearInFrom(int dx, float alpha){
				this.alpha = alpha;
				disappearIn(dx);
			}
			public void appearInFrom(int dx, float alpha){
				this.alpha = alpha;
				appearIn(dx);
			}
			public float get(int x){
				//calculate alpha
				float alpha = aPerX*(x-start) + alpha0;
				if(alpha >= 1) alpha = 1;
				else if(alpha <= 0) alpha = 0;
				return alpha;
			}
			public void updateAlpha(int x){
				alpha = get(x);
				//detect if it has finished
				if(aPerX > 0 && alpha == 1){
					stay();
				} else if(aPerX < 0 && alpha == 0){
					stay();
				}
			}
		}
	}
	
	public enum State {//A patch of material may not blend into AIR!!
		VISIBLE,   //MATERIAL, ALPHA, SIZE:	It has at least one material with a positive alpha value and the size is positive too;
//		ZERO_ALPHA,//MATERIAL, SIZE:		It still has a material (for the renderer to track it) and a positive size, but its alpha is 0.(is contained in VISIBLE)
		ZERO_SIZE, //MATERIAL, ALPHA:		It still has a visible material, but the size is zero. It should go to NOTHING next, if it doesn't grow again.
		NOTHING    //:						It is neither at the beginning, the end nor in the middle of a patch of any material.
	}
	
//	public class ThicknessFunc {
//		int start;
//		double thickness0,
//			transHeight0,
//			dX,
//			dY,
//			transSlope;
//		
//		public double thickness, transHeight;
//		
//		public void resize(double aimThickness, double aimTransitionHeight, double sizingSpeed){
//			thickness0 = thickness;
//			transHeight0 = transHeight;
//		}
//		
//		public void stay(){
//			thickness0 = thickness;
//			transHeight0 = transHeight;
//			dY = 0;
//			transSlope = 0;
//			start = iX;
//		}
//		
//		public void stayAt(double thickness, double transHeight, double sizingSpeed){
//			this.thickness = thickness;
//			this.transHeight = transHeight;
//			stay();
//		}
//		
//		public void updateThicknessAndTransHeight(int x){
//			x -= start;
//			
//			if(x > dX){
//				thickness = thickness0 + dY;
//				transHeight = transHeight0 + dX*transSlope;
//			} else if(x >= 0){
//				thickness = thickness0 + UsefulF.cubicUnit.f(x/dX)*dY;
//				transHeight = transHeight0 + x*(transSlope);
//			} else {
//				thickness = thickness0;
//				transHeight = transHeight0;
//			}
//		}
//	}
}
