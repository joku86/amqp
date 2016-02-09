package de.tiq.solution.transformation;

public interface TransformationStrategy {
	public String transformate(Object input, Long t) throws TransformationException;

}
