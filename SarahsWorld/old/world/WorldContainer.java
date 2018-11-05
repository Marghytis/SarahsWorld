package world;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import main.Savable;
import main.Settings;

import org.lwjgl.opengl.GL11;

import render.TexFile;
import util.Color;
import util.math.Rect;
import util.math.UsefulF;
import util.math.Vec;
import world.generation.Generator;

public class WorldContainer implements Savable {

	public static int arrayLength = 1000;//debug only, default about 10000 (has to be divisible by 4)
	public int globalPointIndex;
	int fillingIndex;
	
	public List<WorldPoint[]> arrays;
	public WorldPoint[] currentArray;
	List<ByteBuffer> buffers;//_______/__/_/_///_/_/_/_/_/_/_/_/
	/*		__________
	 * 	   /          \
	 * 	   |  o    o  |
	 *     |     l    |
	 *     \  \____/  /
	 *      ----------
	 *///TODO

	World world;
	public Generator generator;
	
	public List<WorldColumn> columnsL;
	public List<WorldColumn> columnsR;
	
	/**
	 * you have to create the generator afterwards
	 */
	public WorldContainer(World world){
		this.world = world;
		arrays = new ArrayList<>();
		currentArray = new WorldPoint[arrayLength];
		arrays.add(currentArray);
		columnsL = new ArrayList<>();
		columnsR = new ArrayList<>();
	}
	
	public void expandTo(int xIndex){
		if(xIndex < 0){
			int index = UsefulF.abs(xIndex)-1;
			while(index >= columnsL.size()){
				columnsL.add(generator.shift(true));
			}
		} else {
			while(xIndex >= columnsR.size()){
				columnsR.add(generator.shift(false));
			}
		}
	}
	
	public WorldColumn getColumn(int xIndex){
		WorldColumn out = null;
		if(xIndex < 0){
			out =  columnsL.get(UsefulF.abs(xIndex)-1);
		} else {
			out = columnsR.get(xIndex);
		}
		return out;
	}
	
	public WorldPoint getPoint(int index){
		return arrays.get(index/arrayLength)[index%arrayLength];
	}
	
	//WORLD POINTS
	public class WorldPoint implements Savable{
		
		public Vec p;
		public int i;
		
		
		public WorldPoint(Vec p){
			this.p = p;
			this.i = globalPointIndex++;
			currentArray[fillingIndex++] = this;
			if(fillingIndex >= arrayLength){
				fillingIndex = 0;
				currentArray = new WorldPoint[arrayLength];
				arrays.add(currentArray);
			}
		}
		public String toString(){return "P(" + p.x + "|" + p.y + ")";}
		public String save() {
			return p.x + point + p.y + point;
		}
		public void load(String save) {}
	}
	
	//WORLD FIELDS
	public class WorldField implements Savable{
		
		public static final int width = 15;
		
		public WorldPoint p3,		p4,

			p1,		p2;
		
		public Material mat;
		
		public WorldField(WorldPoint p1, WorldPoint p2, WorldPoint p3, WorldPoint p4, Material mat){
			this.p1 = p1;
			this.p2 = p2;
			this.p3 = p3;
			this.p4 = p4;
			this.mat = mat;
		}
		
		public Rect getTopLine(){
			return new Rect(p3.p, p4.p);
		}

		public String save() {
			return 	  p1.i + field
					+ p2.i + field
					+ p3.i + field
					+ p4.i + field
					+ mat.name() + field; 
		}

		public void load(String save) {
			
		}
	}
	
	//WORLD COLUMNS
	public class WorldColumn implements Savable{
		public WorldPoint topLeft, topRight;
		public WorldField[] fields;
		
		public WorldColumn(WorldField... fields){
			this.fields = fields;
			topLeft = fields[0].p4;
			topRight = fields[0].p3;
		}
		
		public void draw(){
			GL11.glEnable(GL11.GL_TEXTURE_2D);
			for(WorldField field : fields){
				
				WorldPoint p1 = field.p1;
				WorldPoint p2 = field.p2;
				WorldPoint p3 = field.p3;
				WorldPoint p4 = field.p4;
				
				if(field.mat.tex != null){
					Color.WHITE.bind();
					field.mat.tex.file.bind();
					GL11.glBegin(GL11.GL_QUADS);
						GL11.glTexCoord2f((float)(p1.p.x/field.mat.tex.file.pixelBox.size.x), (float)(p1.p.y/field.mat.tex.file.pixelBox.size.y));
						GL11.glVertex2d(p1.p.x, p1.p.y);
						GL11.glTexCoord2f((float)(p2.p.x/field.mat.tex.file.pixelBox.size.x), (float)(p2.p.y/field.mat.tex.file.pixelBox.size.y));
						GL11.glVertex2d(p2.p.x, p2.p.y);
						GL11.glTexCoord2f((float)(p3.p.x/field.mat.tex.file.pixelBox.size.x), (float)(p3.p.y/field.mat.tex.file.pixelBox.size.y));
						GL11.glVertex2d(p3.p.x, p3.p.y);
						GL11.glTexCoord2f((float)(p4.p.x/field.mat.tex.file.pixelBox.size.x), (float)(p4.p.y/field.mat.tex.file.pixelBox.size.y));
						GL11.glVertex2d(p4.p.x, p4.p.y);
					GL11.glEnd();
				} else {
					TexFile.bindNone();
					field.mat.color.bind();
					GL11.glBegin(GL11.GL_QUADS);
						GL11.glVertex2d(p1.p.x, p1.p.y);
						GL11.glVertex2d(p2.p.x, p2.p.y);
						GL11.glVertex2d(p3.p.x, p3.p.y);
						GL11.glVertex2d(p4.p.x, p4.p.y);
					GL11.glEnd();
				}
				
				if(Settings.get("SHOW_WORLD_FIELD_BORDERS")){
					Color.WHITE.bind();
	
					GL11.glBegin(GL11.GL_LINE_LOOP);
						GL11.glVertex2d(p1.p.x, p1.p.y);
						GL11.glVertex2d(p2.p.x, p2.p.y);
						GL11.glVertex2d(p3.p.x, p3.p.y);
						GL11.glVertex2d(p4.p.x, p4.p.y);
					GL11.glEnd();
				}
			}
			if(Settings.get("SHOW_WORLD_FIELD_BORDERS")){
				Color.bind(1, 0, 0, 1);

				GL11.glBegin(GL11.GL_LINES);
					GL11.glVertex2d(topLeft.p.x, topLeft.p.y);
					GL11.glVertex2d(topRight.p.x, topRight.p.y);
					GL11.glVertex2d(topLeft.p.x, topLeft.p.y-1);
					GL11.glVertex2d(topRight.p.x, topRight.p.y-1);
				GL11.glEnd();
				
				Color.WHITE.bind();
			}
		}

		public String save() {
			String save = "";
			for(WorldField field : fields){
				save += field.save() + column;
			}
			return save;
		}

		public void load(String save) {
			
		}
	}

	public String save() {
		String save = "";
		//Save all the world points
		for(WorldPoint[] array : arrays){
			for(WorldPoint p : array){
				if(p != null){
					save += p.save() + allPoints; //TODO
				}
			}
		}
		save += container;
		//Save all the world columns
		for(WorldColumn column : columnsL){
			save += column.save() + columns;
		}
		save += container;
		for(WorldColumn column : columnsR){
			save += column.save() + columns;
		}
		//Save the generators
		save += container + generator.save() + container;
		return save;
	}

	public void load(String save) {
		String[] args = save.split(container, -1); int i = 0;
		String[] points = args[i++].split(allPoints, -1);
		for(int j = 0; j < points.length-1; j++){
			String[] info = points[j].split(point, -1); int k = 0;
			world.container.new WorldPoint(new Vec(Double.parseDouble(info[k++]), Double.parseDouble(info[k++])));
		}
		
		columnsL = new ArrayList<>();
		loadColumns(columnsL, args[i++]);
		
		columnsR = new ArrayList<>();
		loadColumns(columnsR, args[i++]);
		
		generator = new Generator(world);
		generator.load(args[i++]);
	}
	
	public void loadColumns(List<WorldColumn> columns, String save){
		String[] list = save.split(Savable.columns, -1);
		for(int j = 0; j < list.length-1; j++){
			String[] fields = list[j].split(column, -1);
			
			WorldField[] out = new WorldField[fields.length-1];
			for(int k = 0; k < fields.length-1; k++){
				String[] infos = fields[k].split(field, -1); int l = 0;

				out[k] =  new WorldField(
						getPoint(Integer.parseInt(infos[l++])),
						getPoint(Integer.parseInt(infos[l++])),
						getPoint(Integer.parseInt(infos[l++])),
						getPoint(Integer.parseInt(infos[l++])),
						Material.valueOf(infos[l++])
						);
			}
			columns.add(new WorldColumn(out));
		}
	}
}
