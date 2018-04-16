package com.liuyun.MyRecord6;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.TargetDataLine;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.JFrame;
import javax.swing.JPanel;

import org.gj.file.FileHandler;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

public class WaveViewer extends JFrame {
	List<Integer> xValue=new ArrayList<Integer>();
	//画多少个点
	final static int paintPoints=680;
	//采集率，每16个点采集一次
	final static int rate=1;
	public static void main(String[] args) {

		FileHandler fileHandler=new FileHandler();
		// 创造一个实例
		WaveViewer mr = new WaveViewer();
		try {
			mr.parseWave();
			fileHandler.saveText(mr.save(), new File("C:\\workplace\\java\\data\\wave.js"));
			mr.paint();
		} catch (UnsupportedAudioFileException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (LineUnavailableException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public WaveViewer() {
		super();
		jp = new JPanel();
		this.add(jp, BorderLayout.CENTER);
		// 设置窗口的属性
		this.setSize(1600, 900);
		this.setTitle("录音机");
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		// 设置窗口居中
		this.setLocationRelativeTo(null);
		// 将窗口的边框去掉
		this.setUndecorated(true);
		this.setVisible(true);
		//this.setAlwaysOnTop(true);

	}
	// 帧大小，太大了只能画部分，只能画
	int Buffer_Size = 4;
	byte audioDataBuffer[] = new byte[Buffer_Size];
	private List<Short> waveformGraph = new LinkedList<Short>();
	JPanel jp ;
	public void parseWave() throws UnsupportedAudioFileException, IOException, LineUnavailableException {
		// 定义音频输入流
		AudioInputStream ais = AudioSystem.getAudioInputStream(new File("C:\\workplace\\study\\ai\\audio\\a.wav"));
		// 定义音频波形每次显示的字节数
		int intBytes = 1;
		int count=0;
		try {
			while ((intBytes=ais.read(audioDataBuffer, 0, audioDataBuffer.length))!=-1) {
				if(ais.getFormat().getChannels() == 2) {
				    if(ais.getFormat().getSampleSizeInBits() == 16) {
				     waveformGraph.add((short) ((audioDataBuffer[1] << 8) | audioDataBuffer[0]&0xff));//左声道
//				     waveformGraph.put((short) ((buf[3] << 8) | buf[2]));//右声道
				    } else {
				     waveformGraph.add((short) audioDataBuffer[1]);//左声道
				     waveformGraph.add((short) audioDataBuffer[3]);//左声道
				 
//				     waveformGraph.put(buf[2]);//右声道
//				     waveformGraph.put(buf[4]);//右声道
				    }
				   } else {
				    if(ais.getFormat().getSampleSizeInBits() == 16) {
				     waveformGraph.add((short) ((audioDataBuffer[1] << 8) | audioDataBuffer[0]&0xff));
				     waveformGraph.add((short) ((audioDataBuffer[3] << 8) | audioDataBuffer[2]&0xff));
				    } else {
				     waveformGraph.add((short) audioDataBuffer[0]);
				     waveformGraph.add((short) audioDataBuffer[1]);
				     waveformGraph.add((short) audioDataBuffer[2]);
				     waveformGraph.add((short) audioDataBuffer[3]);
				    }
			}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
 
	public String save() {
		JSONObject data=new JSONObject();
		JSONArray price=new JSONArray();
		JSONArray axises=new JSONArray();
		JSONArray values=new JSONArray();
		for(int i=0;i<=waveformGraph.size()-1;i++) {
			axises.add(i);
			values.add(waveformGraph.get(i));
		}
		price.add(axises);
		price.add(values);
		data.put("price", price);
		return "var financeData = "+data.toJSONString();
	}
	// 用画笔画出波形
	public void paint() {
		Graphics g=jp.getGraphics();
		super.paint(g);
		g.fillRect(jp.getX(), jp.getY(), 1600, 880);
		if (audioDataBuffer != null) {
			int step=jp.getWidth() / paintPoints;
			int index=0;
			System.out.println(waveformGraph.size());
			for (int i = 0; i < paintPoints; ++i) {
				g.setColor(Color.RED);
				g.drawLine(i * step, (int) waveformGraph.get(index+71182)/10+200, (i + 1)

						* step, (int) waveformGraph.get(index+71182 + rate)/10+200 );
				index=index+rate;
			}
		}
	}
}
