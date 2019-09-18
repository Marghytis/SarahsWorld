package basis.exceptions;

public class WorldCreationException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public WorldCreationException(String text, Exception parent) {
		super(text, parent);
	}
}
