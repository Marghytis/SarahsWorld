package world.generation.zones.terrain;

import java.util.Random;

import world.data.*;
import world.generation.*;

public class ColumnInWater {
	
	static double segmentHeight = 100;
	static Random random = new Random();

//	public Column[] createPyramid(BiomeManager bioM, double height){
//		Vertex[][] vertices = new Vertex[4][Biome.layerCount];
//		for(int j = 0; j < vertices.length; j++){
//			bioM.step();
//			Vertex[] column = bioM.createVertices(height);
//			for(int i = 0; i < Biome.layerCount; i++){
//				vertices[j][i] = column[i];
//			}
//		}
//	}
	public Column[] createColumnInWater(Random rand, BiomeManager bioM, double height, double width, double waterLevel, double groundLevel){
		TerrainElement element = new TerrainElement(bioM);
		
	//1. choose the amounts of segments above and below the water surface
		int segmentsBelowWater = (int)((waterLevel-groundLevel)/segmentHeight);
		int segmentsAboveWater = (int)(height/segmentHeight);
		
	//2. define all the segment heights
		double[][] levels = new double[1 + segmentsBelowWater + segmentsAboveWater][2];
		double realSegmentHeightBelowWater = (waterLevel-groundLevel)/segmentsBelowWater;
		double realSegmentHeightAboveWater = height/segmentsAboveWater;
		
		//ground level
		double y = groundLevel;
		levels[0][0] = y;
		levels[0][1] = y;
		
		//under water levels
		for(int i = 1; i < segmentsBelowWater; i++){
			y += realSegmentHeightBelowWater;
			levels[i][0] = y + random(0.3*realSegmentHeightBelowWater);
			levels[i][1] = y + random(0.3*realSegmentHeightBelowWater);
		}
		
		//water level
		levels[segmentsBelowWater][0] = waterLevel;
		levels[segmentsBelowWater][1] = waterLevel;
		
		//above water levels
		for(int i = segmentsBelowWater; i < segmentsBelowWater + segmentsAboveWater; i++){
			y += realSegmentHeightAboveWater;
			levels[i][0] = y + random(0.3*realSegmentHeightAboveWater);
			levels[i][1] = y + random(0.3*realSegmentHeightAboveWater);
		}		
		
	//3. define all the x values
		int[][] xValues = new int[levels.length][2];
		int offset = 0;
		for(int i = 0; i < levels.length; i++){
			xValues[i][0] = (int)(-width*(0.5 + (1/(i+1)) + random(0.25)));
			xValues[i][1] = (int)(+width*(0.5 + (1/(i+1)) + random(0.25)));
			if(xValues[i][0] < offset)
				offset = xValues[i][0];
		}
		for(int i = 0; i < levels.length; i++){
			xValues[i][0] -= offset;
			xValues[i][1] -= offset;
		}
		
	//4. layers
		int layerCount = 2*levels.length-1;
		Vertex[][] vertices = new Vertex[xValues[0][1] - xValues[0][0]][layerCount];
		for(int l = 0; l < levels.length-1; l++){
			
		}
		
		return element.create();
	}
	
	public double random(double amplitude){
		return (2*random.nextDouble()-1)*amplitude;
	}
}
