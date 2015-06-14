package world.things;

import item.ItemType;
import render.Texture;
import util.math.Vec;
import world.things.aiPlugins.Animating;
import world.things.aiPlugins.Fruits;
import world.things.aiPlugins.Position;
import world.worldGeneration.WorldData;
import world.worldGeneration.WorldData.Column;
import world.worldGeneration.WorldData.Vertex;

public enum SuperTypes {
	TREE(ThingType.TREE_NORMAL, ThingType.TREE_FIR, ThingType.TREE_FIR_SNOW, 
		 ThingType.TREE_CANDY, ThingType.TREE_GRAVE, ThingType.TREE_PALM){
		
		public Thing create(WorldData world, Vertex field, Vec pos, ThingType type, Object... extraData){
			
			Thing t = new Thing(type, world.random);
			t.pos = new Position(t, pos);
			Texture tex = type.file.tex(0, t.rand.nextInt(type.file.sectorPos[0].length));
			t.ani = new Animating(t, tex, tex.file.pixelBox.copy().scale(0.5 + world.random.nextDouble()), t.rand.nextInt(100) < 30 ? 1 : -1);

			t.fruits = new Fruits(t, new ItemType[]{ItemType.stick}, new int[]{t.rand.nextInt(6)});
			
			
			
			return create(t, world, field.parent);
		}
	},
	BUSH(ThingType.BUSH_NORMAL, ThingType.BUSH_JUNGLE, ThingType.BUSH_CANDY){
		
		public Thing create(WorldData world, Vertex field, Vec pos, ThingType type, Object... extraData){
			
			Thing t = new Thing(type, world.random);
			t.pos = new Position(t, pos);
			Texture tex = type.file.tex(0, t.rand.nextInt(type.file.sectorPos[0].length));
			t.ani = new Animating(t, tex, tex.file.pixelBox.copy().scale(0.5 + world.random.nextDouble()), t.rand.nextInt(100) < 30 ? 1 : -1);
			
			if(type == ThingType.BUSH_NORMAL && tex.y == 1){
				t.fruits = new Fruits(t, new ItemType[]{ItemType.berry}, new int[]{1 + t.rand.nextInt(2)});
			}

			
			
			return create(t, world, field.parent);
		}
	},
	FLOWER(ThingType.FLOWER_NORMAL, ThingType.FLOWER_CANDY, ThingType.FLOWER_JUNGLE){
		
		public Thing create(WorldData world, Vertex field, Vec pos, ThingType type, Object... extraData){
			
			Thing t = new Thing(type, world.random);
			t.pos = new Position(t, pos);
			Texture tex = type.file.tex(0, t.rand.nextInt(type.file.sectorPos[0].length));
			t.ani = new Animating(t, tex, tex.file.pixelBox.copy().scale(0.5 + world.random.nextDouble()), t.rand.nextInt(100) < 30 ? 1 : -1);

			
			
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
