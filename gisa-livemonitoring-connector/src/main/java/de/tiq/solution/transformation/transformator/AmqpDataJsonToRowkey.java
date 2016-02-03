package de.tiq.solution.transformation.transformator;

import de.tiq.solution.transformation.TransformationStrategy;

public class AmqpDataJsonToRowkey implements TransformationStrategy {

	@Override
	public String transformate(String message) {
		return "transformedData";
	}

}
