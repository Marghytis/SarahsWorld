package main;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Hashtable;
import java.util.List;

import things.Thing;
import world.World;
import world.WorldContainer;
import world.WorldContainer.WorldColumn;
import world.WorldContainer.WorldField;
import world.WorldContainer.WorldPoint;
import world.generation.Layer;
import world.generation.WorldGenerator;
import world.objects.ai.AiPlugin;

public class S {
	
static String world = 		"World";
static String container = 		"Container";
static String points = 				"Points";
static String point = 					"P";

static String columns = 			"Columns";
static String column = 					"Column";
static String field = 						"F";

static String generator = 			"Generator";
static String layers = 					"Layers";
static String layer = 						"L";

static String objects = 		"Objects";
static String objectList = 			"OList";
static String object = 					"Object";
static String aiPlugin = 					"ai";
	
	
	//----------------WORLD------------------------
	public static void saveWorld(World w) throws IOException{
		String save = toStringContainer(w.container) + world + toStringObjects(w.objects) + world;
		Files.write(Paths.get("./worlds/" + w.name + ".txt"), save.getBytes());
	}
	//------------------CONTAINER---------------------
	public static String toStringContainer(WorldContainer c){
		return toStringPoints(c.arrays) + container + toStringColumns(c.columnsL) + container + toStringColumns(c.columnsR) + container + toStringGenerator(c.generator) + container;
	}
	//--------------------POINTS------------------
	public static String toStringPoints(List<WorldPoint[]> allPoints){
		String output = "";
		for(WorldPoint[] array : allPoints){
			for(WorldPoint p : array){
				if(p != null){
					output += toStringPoint(p) + points; //TODO
				}
			}
		}
		return output;
	}
	//------------------------POINT---------------
	public static String toStringPoint(WorldPoint p){
		return p.p.x + point + p.p.y + point;
	}
	//--------------------COLUMNS--------------------
	public static String toStringColumns(List<WorldColumn> columnList){
		String output = "";
		for(WorldColumn column : columnList){
			output += toStringColumn(column) + columns;
		}
		return output;
	}
	//-----------------------COLUMN----------------
	public static String toStringColumn(WorldColumn singleColumn){
		String output = "";
		for(WorldField field : singleColumn.fields){
			output += toStringField(field) + column;
		}
		return output;
	}
	//--------------------------FIELD-------------
	public static String toStringField(WorldField f){
		return 	  f.p1.i + field
				+ f.p2.i + field
				+ f.p3.i + field
				+ f.p4.i + field
				+ f.mat.name() + field; 
	}
	//-----------------OBJECTS------------
	public static String toStringObjects(Hashtable<Class<? extends Thing>, List<Thing>> table){
		StringBuilder output = new StringBuilder();
		table.forEach((classs, list) -> {
			output.append(toStringObjectList(list, classs) + objects);
		});
		return output.toString();
	}
	//-----------------OBJECT_LIST------------
	public static String toStringObjectList(List<Thing> list, Class<? extends Thing> classs){
		String output = classs.getCanonicalName() + objectList;
		for(Thing object : list){
			output += toStringObject(object) + objectList;
		}
		return output;
	}
	//-----------------OBJECT------------
	public static String toStringObject(Thing o){
		String output = "";
		for(AiPlugin plugin : o.ai){
			output += plugin.save() + object;
		}
		return output;
	}
	//---------------GENERATOR----------------
	public static String toStringGenerator(WorldGenerator g){
		return
				  g.posL + generator
				+ g.posR + generator
				+ toStringLayers(g.currentBiomeL) + generator
				+ toStringLayers(g.lastBiomeL) + generator
				+ toStringLayers(g.currentBiomeR) + generator
				+ toStringLayers(g.lastBiomeR) + generator;
	}
	//-----------------LAYERS----------------
	public static String toStringLayers(List<Layer> list){
		String output = "";
		for(Layer l : list){
			output += toStringLayer(l) + layers;
		}
		return output;
	}
	//-------------LAYER---------------
	public static String toStringLayer(Layer l){
		return    l.aim.mat.name() + layer
				+ l.aim.thickness + layer
				+ l.aim.sizingSpeed + layer
				+ l.aim.priority + layer
				+ l.thickness + layer
				+ l.lastPDown.i + layer
				+ l.lastPUp.i + layer
				+ l.reachedAim + layer
				+ l.disappeared + layer;
	}
}
