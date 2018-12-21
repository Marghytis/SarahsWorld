package exceptions;

public class WrongThingException extends RuntimeException {
	public WrongThingException(String string) {
		super(string);
	}

	private static final long serialVersionUID = 1L;

}
