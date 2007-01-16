package edu.ou.machinelearning.algorithms;

import edu.ou.machinelearning.functions.NeuralNet;

import java.io.*;
import java.util.Arrays;
import java.util.HashMap;

/**
 * Created by IntelliJ IDEA. User: jason Date: Mar 16, 2006 Time: 12:48:09 PM To
 * change this template use File | Settings | File Templates.
 */
public class Olgarb implements RLAlgorithm<float[], Float> {
	/**
	 * Inner class keeps track of a neural net and the baseline value and
	 * current time associated with that net.
	 */
	private class NNBundle implements Serializable {
		private static final long serialVersionUID = 0L;

		NeuralNet net;

		float base;

		long time;

		public NNBundle(NeuralNet net) {
			this.net = net;
			this.base = 0;
			this.time = 1;
		}
	}

	// keep track of all the nets for the learner
	private HashMap<Integer, NNBundle> nnets;

	// keep track of the net currently being used and updated
	private NNBundle currNet;

	// gamma scales the baseline reward difference to act as the learning
	// rate for error backpropagation.
	private float gamma;

	// temp array for storing the calculated probabilities of each action.
	private float[] actionprobs = new float[0];

	// the indx in actionprobs of the last selected action.
	private int slctdindx;

	// indicates if the agent is currently learning or not.
	private boolean learning;

	/**
	 * Construct a new Olgarb learner with the given gamma value and the
	 * 
	 * @param gamma
	 * @param nets
	 */
	public Olgarb(float gamma, NeuralNet[] nets) {
		this.gamma = gamma;

		this.nnets = new HashMap<Integer, NNBundle>();
		for (NeuralNet n : nets) {
			this.nnets.put(n.getInput().length, new NNBundle(n));
		}
	}

	public void addNeuralNet(NeuralNet net) {
		this.nnets.put(net.getInput().length, new NNBundle(net));
	}

	public int findAction(float[] state) {
		return findAction(state, null);
	}

	public int findAction(float[] state, int[] actions) {
		if (nnets.get(state.length) != currNet) {
			if (currNet != null) {
				currNet.net.clearTraces();
			}
			currNet = nnets.get(state.length);
		}

		currNet.net.setInput(state);
		currNet.net.propagate();

		float[] actionvals = currNet.net.getOutput();
		if (actionprobs.length != actionvals.length) {
			actionprobs = new float[actionvals.length];
		}

		// find the selection probability for each output node
		float outsum = 0.0f;

		if (actions == null) {
			for (int i = 0; i < actionvals.length; i++) {
				actionprobs[i] = (float) Math.exp(actionvals[i]);
				outsum += actionprobs[i];
			}
		} else {
			Arrays.fill(actionprobs, 0);
			for (int action : actions) {
				actionprobs[action] += (float) Math.exp(actionvals[action]);
				outsum += actionprobs[action];
			}
		}

		for (int i = 0; i < actionprobs.length; i++) {
			actionprobs[i] /= outsum;
		}

		this.slctdindx = -1;

		// select an output node according to the probability distribution
		float dice = (float) Math.random();
		float base = 0;
		for (int i = 0; i < actionprobs.length; i++) {
			if ((base += actionprobs[i]) > dice) {
				slctdindx = i;
				break;
			}
		}

		if (slctdindx == -1) {
			// could happen if dice roll is higher than sum (very unlikely)
			// or if outsum is NaN or 0 (if this occurs, go on bug hunt).
			System.out
					.println("Error in OLGARBLearner.findAction() - no index selected, defaulting to 0");
			slctdindx = 0;
		}

		return slctdindx;
	}

	public void endAction(Float reward) {
		currNet.base += (reward - currNet.base) / currNet.time;

		float lr = gamma * (reward - currNet.base);

		for (int i = 0; i < actionprobs.length; i++) {
			if (i == slctdindx) {
				actionprobs[i] = 1.0f - actionprobs[i];
			} else {
				actionprobs[i] = -actionprobs[i];
			}
		}

		currNet.net.backpropagateErrors(lr, actionprobs);
		currNet.time++;
	}

	public void finish() {
		for (NNBundle fnb : nnets.values()) {
			fnb.net.clearTraces();
		}
	}

	public boolean isLearning() {
		return learning;
	}

	public void setLearning(boolean learning) {
		this.learning = learning;
	}

	public void saveResults(String baseFilename) throws IOException {
		// TODO: implement this
	}

	public boolean saveKnowledge(String filename) throws IOException {
		ObjectOutputStream stream = new ObjectOutputStream(
				new FileOutputStream(filename));
		try {
			stream.writeInt(this.nnets.size());
			for (Integer i : nnets.keySet()) {
				NNBundle fb = nnets.get(i);
				stream.writeInt(i);
				stream.writeObject(fb.net);
				stream.writeFloat(fb.base);
				stream.writeLong(fb.time);
			}
			stream.writeFloat(this.gamma);
			return true;
		} finally {
			stream.close();
		}
	}

	public boolean loadKnowledge(String filename) throws IOException {
		ObjectInputStream stream = new ObjectInputStream(new FileInputStream(
				filename));
		try {
			this.nnets.clear();
			int len = stream.readInt();
			for (int i = 0; i < len; i++) {
				int key = stream.readInt();
				NNBundle fb = new NNBundle((NeuralNet) stream.readObject());
				fb.base = stream.readFloat();
				fb.time = stream.readLong();
				this.nnets.put(key, fb);
			}
			this.gamma = stream.readFloat();
			return true;
		} catch (ClassNotFoundException e) {
			return false;
		} finally {
			stream.close();
		}
	}
}
