package main;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import util.geom.Vec;
import world.Material;
import world.World;
import world.WorldContainer;
import world.WorldContainer.WorldColumn;
import world.WorldContainer.WorldField;
import world.generation.Layer;
import world.generation.WorldGenerator;
import world.generation.Biome.AimLayer;
import world.objects.Thing;

public class L {
	
	static World world;
	
	@SuppressWarnings("resource")
	
	public static World loadWorld(World blankWorld) throws FileNotFoundException{
		
		world = blankWorld;
		
		String[] args  = new Scanner(new File("./worlds/" + world.name + ".txt")).useDelimiter("\\Z").next().split(S.world, -1);
		
		readContainer(args[0]);
		readObjects(args[1]);
		return null;
	}
	
	private static void readContainer(String data){
		world.container = new WorldContainer(world);
		String[] args = data.split(S.container, -1); int i = 0;
		readPoints(args[i++]);
		readColumns(args[i++], true);
		readColumns(args[i++], false);
		readGenerator(args[i++]);
	}
	
	private static void readPoints(String data){
		String[] points = data.split(S.points, -1);
		for(int i = 0; i < points.length-1; i++){
			readWorldPoint(points[i]);
		}
	}
	
	private static void readWorldPoint(String data){
		String[] info = data.split(S.point, -1); int i = 0;
		world.container.new WorldPoint(new Vec(Double.parseDouble(info[i++]), Double.parseDouble(info[i++])));
	}
	
	private static void readColumns(String data, boolean left){
		List<WorldColumn> list = new ArrayList<>();
		
		if(left) world.container.columnsL = list;
		else world.container.columnsR = list;
		
		String[] columns = data.split(S.columns, -1);
		for(int i = 0; i < columns.length-1; i++){
			list.add(readColumn(columns[i]));
		}
	}
	
	private static WorldColumn readColumn(String data){
		String[] fields = data.split(S.column, -1);
		
		WorldField[] out = new WorldField[fields.length-1];
		for(int i = 0; i < fields.length-1; i++){
			out[i] = readField(fields[i]);
		}
		return world.container.new WorldColumn(out);
	}
	
	private static WorldField readField(String data){
		String[] infos = data.split(S.field, -1); int i = 0;

		return world.container.new WorldField(
				world.container.getPoint(Integer.parseInt(infos[i++])),
				world.container.getPoint(Integer.parseInt(infos[i++])),
				world.container.getPoint(Integer.parseInt(infos[i++])),
				world.container.getPoint(Integer.parseInt(infos[i++])),
				Material.valueOf(infos[i++])
				);
	}
	
	private static void readGenerator(String data){
		String[] infos = data.split(S.generator, -1); int i = 0;
		world.container.generator = new WorldGenerator(world);
		world.container.generator.posL = Integer.parseInt(infos[i++]);
		world.container.generator.posR = Integer.parseInt(infos[i++]);
		world.container.generator.currentBiomeL = readLayers(infos[i++]);
		world.container.generator.lastBiomeL = readLayers(infos[i++]);
		world.container.generator.currentBiomeR = readLayers(infos[i++]);
		world.container.generator.lastBiomeR = readLayers(infos[i++]);
		world.container.generator.layersL = new ArrayList<>();
		world.container.generator.layersL.addAll(world.container.generator.currentBiomeL);
		world.container.generator.layersL.addAll(world.container.generator.lastBiomeL);
		world.container.generator.layersR = new ArrayList<>();
		world.container.generator.layersR.addAll(world.container.generator.currentBiomeR);
		world.container.generator.layersR.addAll(world.container.generator.lastBiomeR);
	}
	
	private static List<Layer> readLayers(String data){
		List<Layer> output = new ArrayList<>();
		String[] layers = data.split(S.layers, -1);
		for(int i = 0; i < layers.length-1; i++){
			output.add(readLayer(layers[i]));
		}
		return output;
	}
	
	private static Layer readLayer(String data){
		String[] infos = data.split(S.layer, -1); int i = 0;
		
		Layer l = new Layer(new AimLayer(
				Material.valueOf(infos[i++]),
				Double.parseDouble(infos[i++]),
				Double.parseDouble(infos[i++]),
				Integer.parseInt(infos[i++])
				));
		
		l.thickness = Double.parseDouble(infos[i++]);
		l.lastPDown = world.container.getPoint(Integer.parseInt(infos[i++]));
		l.lastPUp = world.container.getPoint(Integer.parseInt(infos[i++]));
		l.reachedAim = Boolean.parseBoolean(infos[i++]);
		l.disappeared = Boolean.parseBoolean(infos[i++]);
		
		return l;
	}
	
	private static void readObjects(String data){
		String[] lists = data.split(S.objects, -1);
		for(int i = 0; i < lists.length-1; i++){
			readObjectList(lists[i]);
		}
	}

	@SuppressWarnings("unchecked")
	private static void readObjectList(String data) {
		String[] infos = data.split(S.objectList, -1); int i = 0;
		
		Class<? extends Thing> classs = null;
		try {
			classs = (Class<? extends Thing>) Class.forName(infos[i++]);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		
		List<Thing> list = new ArrayList<>();
		
		for(; i < infos.length-1; i++){
			list.add(readObject(infos[i], classs));
		}
		world.objects.put(classs, list);
		
		if(classs.equals(Avatar.class)){
			world.avatar = (Avatar)list.get(0);
		}
	}

	private static Thing readObject(String data, Class<? extends Thing> classs) {
		String[] infos = data.split(S.object, -1); 

		Thing o = null;
		try {
			o = classs.getConstructor(Vec.class).newInstance(new Vec());
		} catch (Exception e) {e.printStackTrace();}
		
		for(int i = 0; i < infos.length-1; i++){
			o.ai[i-1].load(infos[i]);
		}
		return o;
	}
}
