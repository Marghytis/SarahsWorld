package extra.exceptions;

public class WrongEntityStateException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public WrongEntityStateException(String message) {
		super(message);
	}
	
	public WrongEntityStateException() {}
}
