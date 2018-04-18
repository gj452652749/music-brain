package org.gj.app.dao;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.TargetDataLine;
import javax.sound.sampled.UnsupportedAudioFileException;

import org.gj.app.net.BackPropagationNet;
import org.gj.sound.SoundInfo;
import org.neuroph.core.data.DataSet;
import org.neuroph.core.data.DataSetRow;

import be.tarsos.dsp.AudioDispatcher;
import be.tarsos.dsp.AudioEvent;
import be.tarsos.dsp.AudioProcessor;
import be.tarsos.dsp.io.jvm.JVMAudioInputStream;
import be.tarsos.dsp.util.fft.FFT;

public class SpectrogramDao {
	BackPropagationNet net =new BackPropagationNet();
	// 1 train、2 test、3 initing
	volatile int netMode = 3;
	volatile char pitch = 'a';
	boolean isReverberating=false;
	private AudioDispatcher dispatcher;
	private float sampleRate = 44100;
	private int bufferSize = 1024 * 4;
	private int overlap = 768 * 4;
	float MIN_AMPLITUDE = 0.5f;
	int PITCH_NUM = 8;
	double[] inputBucket = new double[170];
	int step = 10;
	DataSet dataSet= new DataSet(170, 8);
	private void init() throws LineUnavailableException, UnsupportedAudioFileException {
		net.init();
		if (dispatcher != null) {
			dispatcher.stop();
		}
		final AudioFormat format = new AudioFormat(sampleRate, 16, 1, true, false);
		final DataLine.Info dataLineInfo = new DataLine.Info(TargetDataLine.class, format);
		TargetDataLine line;
		line = (TargetDataLine) AudioSystem.getLine(dataLineInfo);
		final int numberOfSamples = bufferSize;
		line.open(format, numberOfSamples);
		line.start();
		final AudioInputStream stream = new AudioInputStream(line);
		JVMAudioInputStream audioStream = new JVMAudioInputStream(stream);
		// create a new dispatcher
		dispatcher = new AudioDispatcher(audioStream, bufferSize, overlap);
		// add a processor, handle pitch event.
		// 每个处理器会不断被循环处理，调用process
		dispatcher.addAudioProcessor(fftProcessor);
		// run the dispatcher (on a new thread).
		new Thread(dispatcher, "Audio dispatching").start();
	}

	public void loop() {
		while (true) {
			Scanner scan = new Scanner(System.in);
			netMode = scan.nextInt();
			System.out.println(netMode);
			//模式切换时清空数据
			dataSet.clear();
			while (true) {
				pitch = scan.next().charAt(0);
				System.out.println(pitch);
				// 退出循环
				if (pitch == 'q')
					break;
				generateOutputVector(pitch);
			}
		}
	}
	/**
	 * 将音高转换成输出向量
	 * @param pitch
	 * @return
	 */
	public double[] generateOutputVector(char pitch) {
		double[] output = new double[PITCH_NUM];
		output[pitch - 'a'] = 1;
		for (double ele : output)
			System.out.println(ele);
		return output;
	}

	/**
	 * 将输入输出转换为net向量
	 */
	public DataSetRow generateDataSetRow(double[] input,char pitch) {
		double[] output=generateOutputVector(pitch);
		return new DataSetRow(input,output);
	}

	public void addTrainRow(double[] input,char pitch) {
		dataSet.add(generateDataSetRow(input,pitch));
	}

	public void addTestRow(double[] input,char pitch) {
		dataSet.add(generateDataSetRow(input,pitch));
	}

	public static void main(String[] args) {
		SpectrogramDao dao = new SpectrogramDao();
		dao.loop();
	}

	AudioProcessor fftProcessor = new AudioProcessor() {

		FFT fft = new FFT(bufferSize);
		float[] amplitudes = new float[bufferSize / 2];

		@Override
		public void processingFinished() {
			// TODO Auto-generated method stub
		}

		public boolean isStepSignalDetected(List<SoundInfo> pitchList) {
			if (pitchList.get(0).getAmplitude() > MIN_AMPLITUDE)
				return true;
			return false;
		}

		public List<SoundInfo> parseSignal() {
			List<SoundInfo> pitchList = new ArrayList<>();
			for (int i = amplitudes.length / 800; i < amplitudes.length; i++) {
				if (i > 100 && i < 1800) {
					pitchList.add(new SoundInfo(i, amplitudes[i]));
					//映射到桶
					inputBucket[i / 10] += amplitudes[i];
				}
			}
			Collections.sort(pitchList);
			return pitchList;
		}
		/**
		 * 音频信号标准成数字向量
		 */
		public void DataStandardization() {
			
		}
		@Override
		public boolean process(AudioEvent audioEvent) {
			// 此处获取音频采样数据，可见WaveViewer
			float[] audioFloatBuffer = audioEvent.getFloatBuffer();
			float[] transformbuffer = new float[bufferSize * 2];
			System.arraycopy(audioFloatBuffer, 0, transformbuffer, 0, audioFloatBuffer.length);
			fft.forwardTransform(transformbuffer);
			fft.modulus(transformbuffer, amplitudes);
			List<SoundInfo> pitchList = parseSignal();
			if (isStepSignalDetected(pitchList)) {
				isReverberating=true;
				// 训练模式 or 测试模式
				switch (netMode) {
				case 1:
					addTrainRow(inputBucket,pitch);
					break;
				case 2:
					addTestRow(inputBucket,pitch);
					break;
				default:
					break;
				}
			}
			//如果余音结束
			else if(isReverberating) {
				//标志结束
				isReverberating=false;
				switch (netMode) {
				case 1:
					net.train(dataSet);
					break;
				case 2:
					net.test(dataSet);
					break;
				default:
					break;
				}
			}
			return true;
		}

	};
}
