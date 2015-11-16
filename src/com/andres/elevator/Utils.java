package com.andres.elevator;

import java.util.Random;

public class Utils {
	
	public static final int PLANTA_MAX = 5;
	public static final int PLANTA_MIN = 0;
	
	public static final int SUELO = 350;
	public static final int PLANTA_ALTURA = 50;
	
	public static int getRandomValue(int minValue, int maxValue) {
		Random random = new Random();
		return random.nextInt((maxValue - minValue) + 1) + minValue;
	}
}
