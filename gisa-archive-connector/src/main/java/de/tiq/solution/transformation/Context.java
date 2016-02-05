package de.tiq.solution.transformation;

public class Context {
	private TransformationStrategy strategy;

	public Context(TransformationStrategy strategy) {
		this.strategy = strategy;
	}

	public String executeStrategy(Object input) throws TransformationException {
		return strategy.transformate(input);
	}
}
