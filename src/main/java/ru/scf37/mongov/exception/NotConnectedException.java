package ru.scf37.mongov.exception;

public class NotConnectedException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public NotConnectedException() {
		super();
	}

	public NotConnectedException(String message, Throwable cause) {
		super(message, cause);
	}

	public NotConnectedException(String message) {
		super(message);
	}

	public NotConnectedException(Throwable cause) {
		super(cause);
	}
	
	

}
