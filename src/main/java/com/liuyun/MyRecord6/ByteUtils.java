package com.liuyun.MyRecord6;

public class ByteUtils {
	public static float getSignificantFigures(float num, int n) {
	    if(num == 0) {
	        return 0;
	    }
	    final double d = Math.ceil(Math.log10(num < 0 ? -num: num));
	    final int power = n - (int) d;
	    final double magnitude = Math.pow(10, power);
	    final long shifted = Math.round(num*magnitude);
	    return (float) (shifted/magnitude);		
	}
	public static float getFloat(byte[] b) { 
        int accum = 0; 
        accum = accum|(b[0] & 0xff) << 0;
        accum = accum|(b[1] & 0xff) << 8; 
        System.out.println(accum);
        return Float.intBitsToFloat(accum); 
    }
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		byte[] data= {104, -2, -123, -2};
		float value=getFloat(data)/65536;
		value=getSignificantFigures(value,1);
		System.out.println(value);
	}

}
