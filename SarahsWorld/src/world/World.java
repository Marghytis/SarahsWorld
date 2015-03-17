package world;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.util.function.Consumer;

import main.Avatar;
import main.Main;
import main.Savable;

import org.lwjgl.opengl.GL11;

import util.Color;
import util.math.Vec;
import world.WorldContainer.WorldColumn;
import world.WorldContainer.WorldField;
import world.generation.Generator;
import world.objects.Thing;
import world.objects.ThingType;
import core.Renderer;
import core.Updater;
import core.Window;

public class World implements Updater, Renderer {

	public String name;
	public Random random = new Random();
	
	//WORLD
	public WorldContainer container;
	public Columns columns;
	
	//OBJECTS
	public Avatar avatar;
	public List<Thing> deletionRequested;
	public List<Thing>[] objects;
	
	@SuppressWarnings("unchecked")
	public World(String worldName, boolean load){
		this.name = worldName;
		
		objects = (List<Thing>[]) new ArrayList<?>[ThingType.values().length];
		for(int i = 0; i < objects.length; i++){
			objects[i] = new ArrayList<>();
		}
		deletionRequested = new ArrayList<>();

		if(load) {
			try {
				load();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		} else {
			container = new WorldContainer(this);
			container.generator = new Generator(this);
			container.generator.createStartZones();

			ThingType.SARAH.create(this, null, new Vec(0, 50));
		}
		
		avatar = new Avatar(objects[ThingType.SARAH.ordinal()].get(0));
		
		//WORLD
		int width = (Window.WIDTH/WorldField.width)/2*2+24;
		container.expandTo(-width/2);
		container.expandTo(width/2);
		columns = new Columns(width);
	}

	public boolean update(double delta) {
		//WORLD
		int avatarPosIndex = (int)avatar.pos.x/WorldField.width;
		
		container.expandTo(avatarPosIndex - (columns.count/2));
		container.expandTo(avatarPosIndex + (columns.count/2));
		
		columns.update(avatarPosIndex);
		
		//OBJECTS
		double xL = columns.left.data.topLeft.p.x;
		double xR = columns.right.data.topRight.p.x;
		
		deletionRequested.forEach((o) -> remove(o));
		deletionRequested.clear();
		forEach((t) -> {
			if(t.pos.p.x < xR && t.pos.p.x > xL) t.update(delta);
		});
		return false;
	}
	
	Color clear = new Color(0.5f, 0.7f, 1);
	public void draw() {
		GL11.glLoadIdentity();
		GL11.glClearColor(clear.r, clear.g, clear.b, clear.a);
		
		GL11.glTranslated(Window.WIDTH/2-avatar.pos.x, Window.HEIGHT/2-avatar.pos.y, 0);
		
		//Things in the back
		draw((t) -> t.ani.behind == -1);
		//The materials
		columns.draw();
		//The things in the middle
		draw((t) -> t.ani.behind == 0, 1);
		//The things in front
		draw((t) -> t.ani.behind == 1);
		//The living things in the middle again. This is to make them shine through the things in front.
		draw((t) -> t.life != null, 0.1f);
	}
	
	public void add(Thing t){
		objects[t.type.ordinal()].add(t);
	}
	
	public void remove(Thing t){
		objects[t.type.ordinal()].remove(t);
	}
	
	public interface Decider { public boolean decide(Thing t);}
	public void draw(Decider d){draw(d, 1);}
	public void draw(Decider decider, float opacity){
		for(List<Thing> list : objects){
			if(list.size() > 0){
				list.get(0).ani.animator.getAnimation().file.bind();
				Color.WHITE.bind(opacity);
				GL11.glBegin(GL11.GL_QUADS);
					for(Thing t : list){
						if(decider.decide(t)){
							t.render();
						}
					}
				GL11.glEnd();
			}
		}
	}
	
	public void forEach(Consumer<Thing> cons){
		for(List<Thing> list : objects)
			for(Thing t : list)
				cons.accept(t);
	}
	
	public void forEach(Runnable beforeGroup, Consumer<Thing> cons){
		for(List<Thing> list : objects){
			beforeGroup.run();
			for(Thing t : list){
				cons.accept(t);
			}
		}
	}
	
	public Thing[] livingsAt(Vec loc){
		List<Thing> things = new ArrayList<>();
		for(List<Thing> list : Main.world.objects) for(Thing t : list){
			if(t.life != null && !t.equals(this) && loc.containedBy(t.ani.box.pos.x + t.pos.p.x, t.ani.box.pos.y + t.pos.p.y, t.ani.box.size.x, t.ani.box.size.y)){
				things.add(t);
			}
		}
		things.sort((t1, t2) -> t1.ani.behind > t2.ani.behind ? 1 : t1.ani.behind < t2.ani.behind ?  -1 : 0);
		return things.toArray(new Thing[things.size()]);
	}
	
	public class Columns {

		private SingleColumn left;
		private SingleColumn right;
		private List<WorldField>[] allFields;//sorted by material
		int count;
		int x;
		
		public Columns(int count){
			this.count = count;
			this.x = -(count/2);
			left = new SingleColumn(container.getColumn(this.x));
			right = left;
			for(int x = this.x+1; x <= (count/2); x++){
				right.setNext(new SingleColumn(container.getColumn(x)));
				right = right.next;
			}
			right.setNext(left);
		}
		
		public void update(int x){
			x -= (columns.count/2);
			while(this.x > x){
				this.x--;
				add(true, container.getColumn(this.x));
			}
			while(this.x < x){
				this.x++;
				add(false, container.getColumn(this.x + count));
			}
		}
		
		public void draw(){
			forEach((c) -> c.draw());
		}
		
		public void forEach(Consumer<WorldColumn> c){
			SingleColumn helper = left;
			while(!helper.equals(right)){
				c.accept(helper.data);
				helper = helper.next;
			}
		}
		
		public void add(boolean left, WorldColumn data){
			if(left){
				this.left = this.left.last;
				this.right = this.right.last;
				
				this.left.data = data;
			} else {
				this.left = this.left.next;
				this.right = this.right.next;
				
				this.right.data = data;
			}
		}
		
		public class SingleColumn {
			WorldColumn data;
			SingleColumn next, last;
			
			public SingleColumn(WorldColumn data){
				this.data = data;
			}
			
			public void setNext(SingleColumn node){
				next = node;
				node.last = this;
			}
		}
		
	}
	
	public void save() throws IOException{
		String save = container.save() + Savable.world;
		for(List<Thing> list : objects){
			String str = "";
			for(Thing t : list){
				str += t.save() + Savable.objectList;
			}
			save += str + Savable.objects;
		}
		Files.write(Paths.get("./worlds/" + name + ".txt"), save.toString().getBytes());
	}

	public void load() throws FileNotFoundException{
		@SuppressWarnings("resource")
		String[] args  = new Scanner(new File("./worlds/" + name + ".txt")).useDelimiter("\\Z").next().split(Savable.world, -1);
		
		container = new WorldContainer(this);
		container.load(args[0]);
		
		Random rand = new Random();
		
		String[] lists = args[0].split(Savable.objects, -1);
		for(int i = 0; i < lists.length-1; i++){
			String[] infos = lists[i].split(Savable.objectList, -1); 
			
			objects[i] = new ArrayList<>();
			for(int j = 0; j < infos.length-1; j++){
				
				objects[i].add(Thing.load(ThingType.values()[i], infos[j], rand));
			}
		}
	}
}
