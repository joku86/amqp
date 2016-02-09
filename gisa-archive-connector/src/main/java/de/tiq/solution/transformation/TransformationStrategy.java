package de.tiq.solution.transformation;

public interface TransformationStrategy {
	public String transformate(Object input) throws TransformationException;

}
