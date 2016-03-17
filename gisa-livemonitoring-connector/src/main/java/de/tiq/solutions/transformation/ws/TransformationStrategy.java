package de.tiq.solutions.transformation.ws;

public interface TransformationStrategy {
	public String transformate(Object input, long t) throws TransformationException;

}
