package edu.ou.machinelearning.algorithms;

import java.io.*;

public class ModularSarsa extends TDAlgorithm<float[][]> implements
		RLAlgorithm<float[][], float[]> {

	private Sarsa[] modules;

	public ModularSarsa(Sarsa[] modules, float epsilonGreedy) {
		super(epsilonGreedy);
		this.modules = modules;
	}

	public float[] getActionValues(float[][] state) {
		float[] moduleActionValues = this.modules[0].getActionValues(state[0]);
		float[] actionValues = new float[moduleActionValues.length];
		for (int i = 0; i < actionValues.length; i++)
			actionValues[i] = moduleActionValues[i] * 1;

		for (int i = 1; i < this.modules.length; i++) {
			moduleActionValues = this.modules[i].getActionValues(state[i]);
			for (int j = 0; j < actionValues.length; j++)
				actionValues[j] += moduleActionValues[j] * 1;
		}

		return actionValues;
	}

	public void selectAction(int action) {
		for (int i = 0; i < this.modules.length; i++)
			this.modules[i].selectAction(action);
	}

	public void endAction(float[] reward) {
		for (int i = 0; i < this.modules.length; i++)
			this.modules[i].endAction(reward[i]);
	}

	public void finish() {
		for (int i = 0; i < this.modules.length; i++)
			this.modules[i].finish();
	}

	public void saveResults(String baseFilename) throws IOException {
		for (int i = 0; i < this.modules.length; i++)
			this.modules[i].saveResults(baseFilename + "Module" + i);
	}

	public boolean saveKnowledge(String filename) throws IOException {
		return false;
	}

	public boolean loadKnowledge(String filename) throws IOException {
		return false;
	}
}
