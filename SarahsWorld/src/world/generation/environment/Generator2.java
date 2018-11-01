package world.generation.environment;

import java.io.DataOutputStream;

import world.data.Column;
import world.data.WorldData;
import world.generation.GeneratorInterface;

public class Generator2 implements GeneratorInterface {

	Environment[] environments = new Environment[2];//one left, one right
	int[] directions = {-1, 1};
	double startPosition;
	
	WorldData worldData;
	
	public Generator2(WorldData worldData) {
		this.worldData = worldData;
	}

	public boolean borders(double d, double e) {
		
		double[] borders = {d, e};
		
		for(int iEnv = 0; iEnv < environments.length; iEnv++) {
			while(environments[iEnv].getDistanceGenerated() < (borders[iEnv] - startPosition)*directions[iEnv]) {
				if(!extend(iEnv)) return false;
			}
		}
		return true;
	}

	public void save(DataOutputStream output) {
		
	}
	
	public boolean extend(int iEnv) {
		environments[iEnv].step();
		
		Column column = environments[iEnv].createColumn();
		
		//attach the column to the world
		//returns the column to the worldWindow, where quests are tried to be started
		worldData.processNewColumn(column, directions[iEnv], environments[iEnv].getDescription());
		
		return true;
	}

	public boolean extendRight() {
		return extend(1);
	}

	public boolean extendLeft() {
		return extend(0);
	}
	
	
	
}
