package things.sorting;

public interface ThingAspect<T extends ThingAspect<T>> {

	public ThingAspect next();
	
}
