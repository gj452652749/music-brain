package org.gj.app.net;

import java.util.Arrays;

import org.neuroph.core.data.DataSet;
import org.neuroph.core.data.DataSetRow;
import org.neuroph.core.events.LearningEvent;
import org.neuroph.core.events.LearningEventListener;
import org.neuroph.nnet.MultiLayerPerceptron;
import org.neuroph.nnet.learning.BackPropagation;

public class BackPropagationNet {
	MultiLayerPerceptron neuralNet;
	DataSet dataSet;

	public void init() {
		// create MultiLayerPerceptron neural network
		neuralNet = new MultiLayerPerceptron(170, 16, 8);
		// create training set from file
		dataSet = new DataSet(170, 8);
		// train the network with training set

		neuralNet.getLearningRule().addListener(new LearningListener());
		neuralNet.getLearningRule().setLearningRate(0.2);
		neuralNet.getLearningRule().setMaxError(0.01);
		neuralNet.getLearningRule().setMaxIterations(30000);
	}

	public DataSet getDataSet() {
		return dataSet;
	}

	public void train() {
		neuralNet.learn(dataSet);
		neuralNet.save("irisNet.nnet");
		System.out.println("Done training.");
		System.out.println("Testing network...");
	}
	public void test(DataSet testSet) {
		for(DataSetRow testSetRow : testSet.getRows()) {
            neuralNet.setInput(testSetRow.getInput());
            neuralNet.calculate();
            double[] networkOutput = neuralNet.getOutput();
            System.out.print("Input: " + Arrays.toString( testSetRow.getInput() ) );
            System.out.println(" Output: " + Arrays.toString( networkOutput) );
        }
	}

	static class LearningListener implements LearningEventListener {
		// 每次迭代都会调用此方法
		@Override
		public void handleLearningEvent(LearningEvent event) {
			BackPropagation bp = (BackPropagation) event.getSource();
			System.out.println("Current iteration: " + bp.getCurrentIteration());
			System.out.println("Error: " + bp.getTotalNetworkError());
		}

	}
}
