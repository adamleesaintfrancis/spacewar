package edu.ou.machinelearning.algorithms;

import edu.ou.machinelearning.*;

import java.io.*;

public class QLearning extends TDAlgorithm<float[]> implements
		RLAlgorithm<float[], Float> {

	public enum TDResults {
		CumulativeReward, AverageReward, CumulativeTDError, AverageTDError;
	}

	private Function q;

	private float learningRate;

	private float discountRate;

	private float minReward;

	private float maxReward;

	private float reward;

	private float rewardScale;

	private float[] state;

	private float[] previousState;

	private int action = -1;

	private float lifetime = 0;

	private float cumulativeReward = 0;

	private float cumulativeTDError = 0;

	private float actionValue;

	private float[] actionValues;

	private ResultsTable<TDResults> results = new ResultsTable<TDResults>(
			TDResults.class);

	public QLearning(Function q, float learningRate, float epsilonGreedy,
			float discountRate, float rewardScale, float minReward,
			float maxReward) {
		super(epsilonGreedy);
		this.q = q;
		this.learningRate = learningRate;
		this.epsilonGreedy = epsilonGreedy;
		this.discountRate = discountRate;
		this.rewardScale = rewardScale;
		this.minReward = minReward;
		this.maxReward = maxReward;
	}

	public void setLearningRate(float learningRate) {
		this.learningRate = learningRate;
	}

	public void setEpsilonGreedy(float epsilon) {
		this.epsilonGreedy = epsilon;
	}

	public float[] getActionValues(float[] state) {
		this.previousState = this.state;
		this.state = state.clone();
		this.actionValues = this.q.compute(this.state);
		return this.actionValues;
	}

	public void selectAction(int action) {
		int previousAction = this.action;
		float previousActionValue = this.actionValue;
		this.action = action;
		this.actionValue = this.actionValues[this.action];

		if (this.previousState != null && this.learning) {
			int bestAction = findBestAction(this.actionValues);
			float bestActionValue = this.actionValues[bestAction];

			float value = bestActionValue * this.discountRate + this.reward;

			if (value > this.maxReward) {
				// System.out.println("Values increasing out of bounds.");
				value = this.maxReward;
			}
			if (value < this.minReward) {
				// System.out.println("Values decreasing out of bounds.");
				value = this.minReward;
			}

			this.cumulativeTDError += Math.abs(previousActionValue - value);

			this.q.set(this.learningRate, previousAction, value,
					this.previousState);

			// if (this.reward < 0)
			// this.q.clear();
		}

		this.lifetime++;
	}

	public void endAction(Float reward) {
		this.reward = reward * this.rewardScale;
		this.cumulativeReward += reward;
	}

	public void finish() {
		if (this.state != null) {
			this.previousState = this.state;

			// fake the selection of an action with no reward
			this.actionValues = new float[1];
			this.actionValues[0] = 0;
			selectAction(0);
		}

		this.q.clear();
		this.state = null;
		this.action = -1;
		this.actionValue = 0;

		// record stats
		this.results.addRow();
		this.results.set(TDResults.CumulativeReward, this.cumulativeReward);
		this.results.set(TDResults.AverageReward, this.cumulativeReward
				/ this.lifetime);
		this.results.set(TDResults.CumulativeTDError, this.cumulativeTDError);
		this.results.set(TDResults.AverageTDError, this.cumulativeTDError
				/ this.lifetime);

		// clear stats
		this.lifetime = 0;
		this.cumulativeReward = 0;
		this.cumulativeTDError = 0;
	}

	public void saveResults(String baseFilename) throws IOException {
		this.results.save(baseFilename);
	}

	public boolean saveKnowledge(String filename) throws IOException {
		ObjectOutputStream stream = new ObjectOutputStream(
				new FileOutputStream(filename));
		try {
			stream.writeObject(this.q);
			return true;
		} finally {
			stream.close();
		}
	}

	public boolean loadKnowledge(String filename) throws IOException {
		ObjectInputStream stream = new ObjectInputStream(new FileInputStream(
				filename));
		try {
			this.q = (Function) stream.readObject();
			return true;
		} catch (ClassNotFoundException e) {
			return false;
		} finally {
			stream.close();
		}
	}
}
