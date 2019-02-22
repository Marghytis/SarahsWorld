package world.generation;

import util.Color;
import world.data.ColumnListElement;

public class ColorAnt {
	 int dx;
	static int colorTransition = 20;
	private Color top;
	private Color low;
	private BiomeManager manager;
	
	public ColorAnt(BiomeManager manager, Color top, Color low) {
		this.manager = manager;
		setTop(top);
		setLow(low);
	}
	
	public void switchBiome(Biome newBiome) {
		dx = colorTransition;
		{int dx = colorTransition-1;
		for(ColumnListElement c = manager.lastColumn;dx >= 0 && c != null; dx--, c = manager.left? c.right() : c.left()){
			c.column().setTopColor( getTransitionColor(manager.biome.topColor, newBiome.topColor, dx, colorTransition*2));
			c.column().setLowColor( getTransitionColor(manager.biome.lowColor, newBiome.lowColor, dx, colorTransition*2));
//			c.topColor = newBiome.topColor;
//			c.lowColor = newBiome.lowColor;
		}}
//		setTop(getTransitionColor(manager.biome.topColor, newBiome.topColor, colorTransition, colorTransition*2));
//		setLow(getTransitionColor(manager.biome.lowColor, newBiome.lowColor, colorTransition, colorTransition*2));
//		setTop(newBiome.topColor);
//		setLow(newBiome.lowColor);
	}
	
	private Color getTransitionColor(Color cOld, Color cNew, int dx, double colorTransition) {
		return cNew.minus(cOld).scale((float)(dx/colorTransition)).add(cOld);
	}

	public void step() {
		if(dx <= colorTransition*2){
			setTop(getTransitionColor(manager.last.topColor, manager.biome.topColor, dx, colorTransition*2));
			setLow(getTransitionColor(manager.last.lowColor, manager.biome.lowColor, dx, colorTransition*2));
			dx++;
		} else {
			setTop(manager.biome.topColor);
			setLow(manager.biome.lowColor);
		}
	}
	
	private void setTop(Color color) { this.top = color;	}
	public Color getTop() { return top; }
	private void setLow(Color color) { this.low = color; }
	public Color getLow() { return low; }
}
