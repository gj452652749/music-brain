/*
*      _______                       _____   _____ _____  
*     |__   __|                     |  __ \ / ____|  __ \ 
*        | | __ _ _ __ ___  ___  ___| |  | | (___ | |__) |
*        | |/ _` | '__/ __|/ _ \/ __| |  | |\___ \|  ___/ 
*        | | (_| | |  \__ \ (_) \__ \ |__| |____) | |     
*        |_|\__,_|_|  |___/\___/|___/_____/|_____/|_|     
*                                                         
* -------------------------------------------------------------
*
* TarsosDSP is developed by Joren Six at IPEM, University Ghent
*  
* -------------------------------------------------------------
*
*  Info: http://0110.be/tag/TarsosDSP
*  Github: https://github.com/JorenSix/TarsosDSP
*  Releases: http://0110.be/releases/TarsosDSP/
*  
*  TarsosDSP includes modified source code by various authors,
*  for credits and info, see README.
* 
*/


package be.tarsos.dsp.example;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.swing.JComponent;

import org.gj.sound.SoundBean;
import org.gj.sound.SoundInfo;

import be.tarsos.dsp.util.PitchConverter;
import be.tarsos.dsp.util.fft.FFT;

public class SpectrogramPanel extends JComponent implements ComponentListener{
	/**
	 * 
	 */
	private static final long serialVersionUID = -3729805747119272534L;
	
	private BufferedImage bufferedImage;
	private Graphics2D bufferedGraphics;
	
	private int position;
	    
	public SpectrogramPanel(){
		bufferedImage = new BufferedImage(640*4,480*4, BufferedImage.TYPE_INT_RGB);
		bufferedGraphics = bufferedImage.createGraphics();
		this.addComponentListener(this);
	}
	
	 private int frequencyToBin(final double frequency) {
	        final double minFrequency = 50; // Hz
	        final double maxFrequency = 11000; // Hz
	        int bin = 0;
	        final boolean logaritmic = true;
	        if (frequency != 0 && frequency > minFrequency && frequency < maxFrequency) {
	            double binEstimate = 0;
	            if (logaritmic) {
	                final double minCent = PitchConverter.hertzToAbsoluteCent(minFrequency);
	                final double maxCent = PitchConverter.hertzToAbsoluteCent(maxFrequency);
	                final double absCent = PitchConverter.hertzToAbsoluteCent(frequency * 2);
	                binEstimate = (absCent - minCent) / maxCent * getHeight();
	            } else {
	                binEstimate = (frequency - minFrequency) / maxFrequency * getHeight();
	            }
	            if (binEstimate > 700) {
	                System.out.println(binEstimate + "");
	            }
	            bin = getHeight() - 1 - (int) binEstimate;
	        }
	        return bin;
	    }
	
	public void paintComponent(final Graphics g) {
        g.drawImage(bufferedImage, 0, 0, null);
    }


	String currentPitch = "";
	

	public void drawFFT(double pitch,float[] amplitudes,FFT fft){
		double maxAmplitude=0;
		//for every pixel calculate an amplitude
		float[] pixeledAmplitudes = new float[getHeight()];
		//iterate the lage arrray and map to pixels
		List<SoundInfo> pitchList=new ArrayList<>();
		 for (int i = amplitudes.length/800; i < amplitudes.length; i++) {
			 //此为连续转离散公式https://dsp.stackexchange.com/questions/26927/what-is-a-frequency-bin
			 //映射到坐标轴(50-190)，相当于桶化
             int pixelY = frequencyToBin(i * 44100 / (amplitudes.length * 8));
             //第i个分量的亮度值，作桶化处理
             pixeledAmplitudes[pixelY] += amplitudes[i];
             if(i>100 && i<1800)
            	 pitchList.add(new SoundInfo(i,amplitudes[i]));
             //防止为负
             maxAmplitude = Math.max(pixeledAmplitudes[pixelY], maxAmplitude);
         }
		 //draw the pixels 
		 for (int i = 0; i < pixeledAmplitudes.length; i++) {
    		 Color color = Color.black;
    		 //幅度转亮度
             if (maxAmplitude != 0) {          	  
            	 final int greyValue = (int) (Math.log1p(pixeledAmplitudes[i] / maxAmplitude) / Math.log1p(1.0000001) * 255);
             	 color = new Color(greyValue, greyValue, greyValue);
             	 if(i==0)
             		color = new Color(255, 255, 255);
             	 //此处i为bin，为了将fre转成图像显示
            	 //pitchList.add(new SoundInfo(i,greyValue));
             }
             //笔触颜色亮度
             bufferedGraphics.setColor(color);
        	 bufferedGraphics.fillRect(position, i, 3, 1);
         }
		Collections.sort(pitchList);
		for(int i=0;i<=10;i++) {
			System.out.print(pitchList.get(i).getPitch()+":"+pitchList.get(i).getAmplitude()+"	");
		}
		//训练模式 OR 测试模式
		System.out.println();
		if (pitch != -1) {
			  //pitch离散化
              int pitchIndex = frequencyToBin(pitch);
              bufferedGraphics.setColor(Color.RED);
              bufferedGraphics.fillRect(position, pitchIndex, 1, 1);
              currentPitch = new StringBuilder("Current frequency: ").append((int) pitch).append("Hz").toString();
        }
		
		
		
		bufferedGraphics.clearRect(0,0, 190,30);
        bufferedGraphics.setColor(Color.WHITE);
        bufferedGraphics.drawString(currentPitch, 20, 20);
        //画刻度
        for(int i = 0 ; i < 100; i += 10){
        	int bin = frequencyToBin(i);
			bufferedGraphics.drawLine(0, bin, 5, bin);
		}
        for(int i = 100 ; i < 500; i += 100){
        	int bin = frequencyToBin(i);
			bufferedGraphics.drawLine(0, bin, 5, bin);
		}
		
        for(int i = 500 ; i <= 20000; i += 500){
			int bin = frequencyToBin(i);
			bufferedGraphics.drawLine(0, bin, 5, bin);
		}
        
        for(int i = 100 ; i <= 20000; i*=10){
			int bin = frequencyToBin(i);
			bufferedGraphics.drawString(String.valueOf(i), 10, bin);
		}
        
		repaint();
		position+=3;
		position = position % getWidth();
	}


	@Override
	public void componentHidden(ComponentEvent e) {		
	}
	@Override
	public void componentMoved(ComponentEvent e) {
	}

	@Override
	public void componentResized(ComponentEvent e) {	
		bufferedImage = new BufferedImage(getWidth(),getHeight(), BufferedImage.TYPE_INT_RGB);
		bufferedGraphics = bufferedImage.createGraphics();
		position = 0;
	}

	@Override
	public void componentShown(ComponentEvent e) {		
	}

}
