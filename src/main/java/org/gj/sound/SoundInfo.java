package org.gj.sound;

public class SoundInfo implements Comparable<SoundInfo>{
	int pitch;
	float amplitude;
	public SoundInfo(int pitch, float amplitude) {
		super();
		this.pitch = pitch;
		this.amplitude = amplitude;
	}
	public int getPitch() {
		return pitch;
	}
	public void setPitch(int pitch) {
		this.pitch = pitch;
	}
	public float getAmplitude() {
		return amplitude;
	}
	public void setAmplitude(float amplitude) {
		this.amplitude = amplitude;
	}
	@Override
	public int compareTo(SoundInfo o) {
		// TODO Auto-generated method stub
		return o.getAmplitude()>this.getAmplitude()?1:-1;
	}
	

}
