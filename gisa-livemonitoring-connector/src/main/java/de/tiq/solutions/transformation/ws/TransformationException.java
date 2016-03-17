package de.tiq.solutions.transformation.ws;

public class TransformationException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public TransformationException(String errorDescription, Throwable e) {
		super(errorDescription, e);
	}

}
