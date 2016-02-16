package de.tiq.solutions.transformation;

public interface TransformationStrategy {
	public String transformate(Object input, Long t) throws TransformationException;

}
