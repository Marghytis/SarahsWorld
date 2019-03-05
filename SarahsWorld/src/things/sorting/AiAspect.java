package things.sorting;

import things.Thing;
import things.ThingType;
import things.aiPlugins.Attachement.AttachementPlugin;
import world.data.ColumnListElement;

public interface AiAspect<T extends ThingAspect<T>> {
	
	public T get(ThingID id);

	public static void test(ColumnListElement c) {
		
		for(int ttype = 0; ttype < ThingType.types.length; ttype++)
			if(ThingType.types[ttype].ani == null && ThingType.types[ttype].attachment != null)
				for(Thing t = c.column().firstThing(ttype); t != null; t = t.next())
					t.attachment.onVisibilityChange(false);
		
		//    |  |  |  |
		//    V  V  V  V
		
		for(ThingType type : ThingType.types)
			if(type.ani == null && type.attachment != null)
				for(ThingPluginTest attachment = type.test.get(c.column().firstThingID(type)); attachment != null; attachment = attachment.next())
					attachment.onVisibilityChange(false);
		
	}
	
	public static class ThingID {
		
	}
	
}
