package de.tiq.solution.transformation;

public interface TransformationStrategy {
	public String transformate(String message) throws TransformationException;

}
