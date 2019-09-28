package world;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Random;

import basis.Savable;
import basis.exceptions.WorldCreationException;
import basis.exceptions.WorldTooSmallException;
import extra.Main;
import extra.items.ItemType;
import extra.things.Thing;
import extra.things.ThingType;
import input.PollData;
import menu.MenuManager.MenuType;
import menu.Settings;
import moveToLWJGLCore.Dir;
import util.math.Vec;
import world.data.Column;
import world.data.Vertex;
import world.data.WorldData;
import world.data.WorldEditor;
import world.generation.Generator;
import world.generation.GeneratorInterface;
import world.render.WorldPainter;
import world.window.ArrayWorldWindow;
import world.window.BackgroundWindow;
import world.window.GeneratingWorldWindow;
import world.window.TerrainWindow;
import world.window.ThingWindow;

public class World implements Savable {

	public static Random rand = new Random();
	public static World world;
	
	
	public WorldData data;
	public WorldEditor editor;
	public GeneratorInterface generator;
	
	private GeneratingWorldWindow genWindow;
	private BackgroundWindow backgroundRenderingWindow;
	
	public TerrainWindow landscapeWindow;
	public ThingWindow thingWindow;
	
	public WorldEngine engine;
	public WorldPainter window;
	public WorldListener listener;
	public Thing avatar;
	
	public World(PollData inputData){
		world = this;
		Main.game().world = this;

		data = new WorldData(this);
		editor = new WorldEditor(data);
		generator = new Generator(data);

		Vertex v = data.getRightColumn().getTopSolidVertex();
		Vec pos = new Vec(Settings.getVec("AVATAR_START_OFFSET").x, v.y() + Settings.getVec("AVATAR_START_OFFSET").y);
		avatar = new Thing(ThingType.SARAH, v.getParent(), pos);
		avatar.avatar.setAvatar(null);
		avatar.invPlug.addItem( ItemType.UNICORN_HORN, 1);
		
		init(inputData);
	}

	public World(DataInputStream input, PollData inputData) throws IOException {
		world = this;
		Main.game().world = this;
		
		data = new WorldData(input);
		editor = new WorldEditor(data);
		generator = new Generator(data, input);//				8.	generator

		init(inputData);
	}
	
	public void init(PollData inputData){

		Column anchor = data.getRightColumn();

		//define ranges
		int windowRadius = (int)((Main.game().SIZE_HALF.w )/Column.COLUMN_WIDTH);
		genWindow = new GeneratingWorldWindow(anchor, windowRadius + 28, generator);
		
		//generation happens here, because avatar and generator can't be accessed statically yet. might change..
		int startX = (int)(avatar.pos.x/Column.COLUMN_WIDTH);
		genWindow.moveToColumn(startX);
		while(anchor.getIndex() < startX && anchor.right() != null) anchor = anchor.right();
		while(anchor.getIndex() > startX && anchor.left() != null) anchor = anchor.left();

		thingWindow 	= new ThingWindow(anchor, windowRadius + 28, windowRadius + 24, windowRadius + 4, windowRadius + 0);
		thingWindow.moveToColumn(startX);
		thingWindow.loadCenter();
		try {
			landscapeWindow 			 = new TerrainWindow(   anchor, windowRadius + 6);
			backgroundRenderingWindow    = new BackgroundWindow(anchor, windowRadius + 6);
		} catch (WorldTooSmallException e) {
			throw new WorldCreationException("World data is not large enough yet : (" + genWindow.getEnd(Dir.l).getIndex() + " <-> " + genWindow.getEnd(Dir.r).getIndex() + ")", e);
		}
		engine = new WorldEngine(data, editor, thingWindow, genWindow, landscapeWindow, backgroundRenderingWindow, thingWindow);
		window = new WorldPainter(data, thingWindow, landscapeWindow, backgroundRenderingWindow);
		listener = new WorldListener(this, inputData);
	}
	
	public void gameOver() {
		data.setGameOver();
		Main.game().menu.setMenu(MenuType.EMPTY);
		Main.game().sound.playFuneralMarch();
	}
	
	public boolean isGameOver() {
		return data.isGameOver();
	}
	
	@Override
	public void save(DataOutputStream output) {
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

	public BackgroundWindow getBackgroundWindow() {
		return backgroundRenderingWindow;
	}
}
