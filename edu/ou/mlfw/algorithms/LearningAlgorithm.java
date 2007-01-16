package edu.ou.machinelearning.algorithms;

import java.io.IOException;

public interface LearningAlgorithm {
	public boolean isLearning();

	public void setLearning(boolean learning);

	public void saveResults(String baseFilename) throws IOException;

	public boolean saveKnowledge(String filename) throws IOException;

	public boolean loadKnowledge(String filename) throws IOException;
}
