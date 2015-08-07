package world.things;

import item.ItemType;
import render.Animation;
import util.math.Vec;
import world.WorldData;
import world.WorldData.Column;
import world.WorldData.Vertex;
import world.things.aiPlugins.Animating;
import world.things.aiPlugins.Fruits;
import world.things.aiPlugins.Position;

public enum SuperTypes {
	TREE(ThingType.TREE_NORMAL, ThingType.TREE_FIR, ThingType.TREE_FIR_SNOW, 
		 ThingType.TREE_CANDY, ThingType.TREE_GRAVE, ThingType.TREE_PALM){
		
		public Thing create(WorldData world, Vertex field, Vec pos, ThingType type, Object... extraData){
			
			Thing t = new Thing(type, world.random);
			t.pos = new Position(t, pos);
			t.ani = new Animating(t, new Animation(type.file, 0, t.rand.nextInt(type.file.partsY), 0), type.file.createBox().scale(0.5 + world.random.nextDouble()), t.rand.nextInt(2) < 1 ? 1 : -1);

			t.fruits = new Fruits(t, new ItemType[]{ItemType.STICK}, new int[]{t.rand.nextInt(6)+3});
			
			
			
			return create(t, world, field.parent);
		}
	},
	BUSH(ThingType.BUSH_NORMAL, ThingType.BUSH_JUNGLE, ThingType.BUSH_CANDY){
		
		public Thing create(WorldData world, Vertex field, Vec pos, ThingType type, Object... extraData){
			
			Thing t = new Thing(type, world.random);
			t.pos = new Position(t, pos);
			t.ani = new Animating(t, new Animation(type.file, 0, t.rand.nextInt(type.file.partsY), 0), type.file.createBox().scale(0.5 + world.random.nextDouble()), t.rand.nextInt(100) < 30 ? 1 : -1);
			
			if(type == ThingType.BUSH_NORMAL && t.ani.animator.ani.y == 1){
				t.fruits = new Fruits(t, new ItemType[]{ItemType.BERRY}, new int[]{1 + t.rand.nextInt(2)});
			}
			if(t.ani.box.size.y > 80){
				t.ani.behind = -1;
			}
			
			
			return create(t, world, field.parent);
		}
	},
	FLOWER(ThingType.FLOWER_NORMAL, ThingType.FLOWER_CANDY, ThingType.FLOWER_JUNGLE){
		
		public Thing create(WorldData world, Vertex field, Vec pos, ThingType type, Object... extraData){
			
			Thing t = new Thing(type, world.random);
			t.pos = new Position(t, pos);
			t.ani = new Animating(t, new Animation(type.file, 0, t.rand.nextInt(type.file.partsY), 0), type.file.createBox().scale(0.5 + world.random.nextDouble()), t.rand.nextInt(100) < 30 ? 1 : -1);
			
			return create(t, world, field.parent);
		}
	},
	BIRD(ThingType.BIRD_NORMAL, ThingType.BIRD_RAINBOW, ThingType.BIRD_BLACK){
		public Thing create(WorldData world, Vertex field, Vec pos, ThingType type, Object... extraData){
			Thing t = new Thing(type, world.random);
			t.pos = new Position(t, pos);
			t.ani = new Animating(t, new Animation(type.file, 0, ((int)extraData[0]) + t.rand.nextInt((int)extraData[1]+1), 0), type.file.createBox().scale(0.5 + world.random.nextDouble()), t.rand.nextInt(100) < 30 ? 1 : -1);
			
			return create(t, world, field.parent);
		}
	};
	
	ThingType[] subTypes;
	
	SuperTypes(ThingType... subTypes){
		this.subTypes = subTypes;
	}

	public abstract Thing create(WorldData world, Vertex field, Vec pos, ThingType thingType, Object... extraData);
	public Thing create(Thing t, WorldData world, Column c){
		t.createAi();
		c.add(t);
		return t;
	}
}
