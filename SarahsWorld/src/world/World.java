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
import world.render.WorldWindow;
import world.window.GeneratingWorldWindow;
import world.window.RealWorldWindow;

public class World {

	public static Random rand = new Random();
	public static World world;
	
	public WorldData data;
	WorldEditor editor;
	public GeneratorInterface generator;
	
	GeneratingWorldWindow genWindow;
	RealWorldWindow updateWindow;
	RealWorldWindow drawWindow;
	
	WorldEngine engine;
	public WorldWindow window;
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
		int drawRadius = (int)((Main.SIZE.w + 400)/Column.COLUMN_WIDTH);
		genWindow = new GeneratingWorldWindow(anchor, drawRadius + 20, generator);
		updateWindow    = new RealWorldWindow(anchor, drawRadius + 2);
		drawWindow      = new RealWorldWindow(anchor, drawRadius);
		
		engine = new WorldEngine(data, editor, genWindow, updateWindow);
		//generation happens here, because avatar and generator can't be accessed statically yet. might change..
		generator.borders(avatar.pos.x - Settings.GENERATION_RADIUS, avatar.pos.x + Settings.GENERATION_RADIUS);

		window = new WorldWindow(data, anchor, avatar.pos.x, (int)((Main.SIZE.w + 400)/Column.COLUMN_WIDTH));
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
