package world;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Random;

import main.Main;
import menu.Settings;
import things.Thing;
import things.ThingType;
import util.math.Vec;
import world.data.Column;
import world.data.Vertex;
import world.data.WorldData;
import world.data.WorldEditor;
import world.generation.Generator;
import world.generation.GeneratorInterface;
import world.render.LandscapeWindow;
import world.render.ThingWindow;
import world.render.WorldPainter;
import world.window.GeneratingWorldWindow;
import world.window.RealWorldWindow;

public class World {

	public static Random rand = new Random();
	public static World world;
	
	public WorldData data;
	WorldEditor editor;
	public GeneratorInterface generator;
	
	public GeneratingWorldWindow genWindow;
	public RealWorldWindow updateWindow;
	public LandscapeWindow landscapeWindow;
	public ThingWindow thingWindow;
	
	public WorldEngine engine;
	public WorldPainter window;
	public Thing avatar;
	
	public World(){
		world = this;
		Main.world = this;

		data = new WorldData(this);
		editor = new WorldEditor(data);
		generator = new Generator(data);

		
		Settings.GENERATION_RADIUS = Main.SIZE.w + 800;//TODO is this value used?

		Vertex v = data.getRightColumn().getTopSolidVertex();
		Vec pos = new Vec(Settings.AVATAR_START_OFFSET.x, v.y + Settings.AVATAR_START_OFFSET.y);
		avatar = new Thing(ThingType.SARAH, data, v.parent, pos);
		
		init();
	}

	public World(DataInputStream input) throws IOException {
		data = new WorldData(input);
		editor = new WorldEditor(data);
		generator = new Generator(data, input);//				8.	generator

		init();
	}
	
	public void init(){

		Column anchor = data.getRightColumn();

		//define ranges
		int windowRadius = (int)((Main.HALFSIZE.w )/Column.COLUMN_WIDTH);
		genWindow = new GeneratingWorldWindow(anchor, windowRadius + 10, generator);
		updateWindow    = new RealWorldWindow(anchor, windowRadius + 8);
		
		//generation happens here, because avatar and generator can't be accessed statically yet. might change..
//		generator.borders(avatar.pos.x - Settings.GENERATION_RADIUS, avatar.pos.x + Settings.GENERATION_RADIUS);
		genWindow.moveToColumn((int)(avatar.pos.x/Column.COLUMN_WIDTH));

		thingWindow 	= new ThingWindow(anchor, windowRadius + 4);
		landscapeWindow = new LandscapeWindow(anchor, windowRadius + 4);
		engine = new WorldEngine(data, editor, genWindow, updateWindow, landscapeWindow, thingWindow);
		window = new WorldPainter(data, thingWindow, landscapeWindow);
	}
	
	public void save(DataOutputStream output) throws IOException {
		data.save(output);
		generator.save(output);//								9.	generator
	}

	/**
	 * Attach a generated column to the world.
	 */
	public void attachRight(Column column) {
		data.addRight(column);
	}

	public void attachLeft(Column column) {
		data.addLeft(column);
	}
}
