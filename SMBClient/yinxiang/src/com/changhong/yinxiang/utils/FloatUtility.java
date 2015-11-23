package com.changhong.yinxiang.utils;

import java.math.BigDecimal;

public class FloatUtility {
	
	public static float add(float f1, float f2){
		BigDecimal mbd1 = new BigDecimal(Float.toString(f1));
		BigDecimal mbd2 = new BigDecimal(Float.toString(f2));
		return mbd1.add(mbd2).floatValue();
	}
	
	public static float mulitiply(float f1, float f2){
		BigDecimal mbd1 = new BigDecimal(Float.toString(f1));
		BigDecimal mbd2 = new BigDecimal(Float.toString(f2));
		return mbd1.multiply(mbd2).floatValue();
	}
	
	public static float divide(float f1, float f2){
		BigDecimal mbd1 = new BigDecimal(Float.toString(f1));
		BigDecimal mbd2 = new BigDecimal(Float.toString(f2));
		return mbd1.divide(mbd2,2,1).floatValue();
	}
	public static float divide(float f1, float f2,int roundingMode){
		BigDecimal mbd1 = new BigDecimal(Float.toString(f1));
		BigDecimal mbd2 = new BigDecimal(Float.toString(f2));
		return mbd1.divide(mbd2,2,roundingMode).floatValue();
	}
	
	public static float getFloatValue(String f1){
		BigDecimal mbd1 = new BigDecimal(f1);
		return mbd1.floatValue();
	}
}
