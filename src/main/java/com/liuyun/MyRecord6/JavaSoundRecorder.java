package com.liuyun.MyRecord6;
import javax.sound.sampled.*;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.io.*;
 
/**
 * A sample program is to demonstrate how to record sound in Java
 * author: www.codejava.net
 */
public class JavaSoundRecorder {
    // record duration, in milliseconds
    static final long RECORD_TIME = 60000;  // 1 minute
 
    // path of the wav file
    File wavFile = new File("C:\\workplace\\study\\ai\\audio\\1969.wav");
 
    // format of audio file
    AudioFileFormat.Type fileType = AudioFileFormat.Type.WAVE;
 
    // the line from which audio data is captured
    TargetDataLine line;
 
    /**
     * Defines an audio format
     */
    AudioFormat getAudioFormat() {
        float sampleRate = 16000;
        int sampleSizeInBits = 8;
        int channels = 2;
        boolean signed = true;
        boolean bigEndian = true;
        AudioFormat format = new AudioFormat(sampleRate, sampleSizeInBits,
                                             channels, signed, bigEndian);
        return format;
    }
 
    /**
     * Captures the sound and record into a WAV file
     * @throws IOException 
     * @throws UnsupportedAudioFileException 
     */
    void start() throws UnsupportedAudioFileException, IOException {
    	ByteArrayOutputStream baos = null;
    	byte audioDataBuffer[] = null;
    	ByteArrayInputStream bais = null;
	// 定义源数据行,源数据行是可以写入数据的数据行。它充当其混频器的源。应用程序将音频字节写入源数据行，这样可处理字节缓冲并将它们传递给混频器。
	SourceDataLine sd = null;
	// 定义录音格式
	AudioFormat af = getAudioFormat();
	// 定义音频输入流
	AudioInputStream ais = AudioSystem.getAudioInputStream(new File("C:\\workplace\\study\\ai\\audio\\1969.wav"));

	// 定义存放录音的字节数组,作为缓冲区
	byte bts[] = new byte[10000];
	// 定义音频波形每次显示的字节数
	int intBytes = 0;
	int cnt=1;
	try {
		while (cnt>0) {
			// 开始从音频流中读取字节数
			byte copyBts[] = bts;
			bais = new ByteArrayInputStream(copyBts);
			//ais = new AudioInputStream(bais, af, copyBts.length / af.getFrameSize());
			try {
				// 从音频流中读取
				int Buffer_Size = 10000;
				audioDataBuffer = new byte[Buffer_Size];
				intBytes = ais.read(audioDataBuffer, 0, audioDataBuffer.length);

				// 不写到混频器中这样就不会播放
				// if (intBytes >= 0) {
				// outBytes = sd.write(audioDataBuffer, 0,audioDataBuffer.length);
				// }
				break;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	} catch (Exception e) {
		e.printStackTrace();
	} finally {
		try {
			// intBytes = -1;
			// 关闭打开的字节数组流
			if (baos != null) {
				baos.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			// 下面这句td.drain()不能要，这样如果不播放数据就阻塞再次录音会出现其他程序访问错误
			// td.drain();
		}
	}
	}
    public void Test(){
    	  JFrame jf = new JFrame();
    	  jf.setBounds(200, 200, 1100, 1100);
    	  jf.setVisible(true);
    	  jf.setLayout(null);
    	  jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);   	  
    	  JPanel panel1 = new JPanel();
    	  panel1.setSize(800, 700);
    	  panel1.setLayout(null);
    	  panel1.setVisible(true);   	  
    	  JScrollPane jp = new JScrollPane();
    	  jp.setSize(800, 700);  	  
    	  JPanel panel2 = new JPanel();
    	  panel2.setPreferredSize(new Dimension(900, 600));
    	  panel2.setVisible(true);   	  
    	  jp.getViewport().add(panel2);
    	  jp.validate();
    	  panel1.add(jp, BorderLayout.CENTER);
    	  jf.add(panel1, BorderLayout.CENTER);   	  
    	  jf.setVisible(true);
    	 }
    /**
     * Entry to run the program
     * @throws IOException 
     * @throws UnsupportedAudioFileException 
     */
    public static void main(String[] args) throws UnsupportedAudioFileException, IOException {
        JavaSoundRecorder recorder = new JavaSoundRecorder();
        recorder.Test();
        // start recording
        //recorder.start();
    }
}