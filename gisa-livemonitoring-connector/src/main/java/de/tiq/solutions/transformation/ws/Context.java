package de.tiq.solutions.transformation.ws;

public class Context {
	private final TransformationStrategy strategy;

	public Context(TransformationStrategy strategy) {
		this.strategy = strategy;
	}

	public String executeStrategy(Object input, long t) throws TransformationException {
		return strategy.transformate(input, t);
	}
}
