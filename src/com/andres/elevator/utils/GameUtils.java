package com.andres.elevator.utils;

import java.util.Random;

public class GameUtils {
	
	public static final int PLANTA_MAX = 5;
	public static final int PLANTA_MIN = 0;
	
	public static final int SUELO_PX = 400;
	public static final int PLANTA_ALTURA_PX = 64;
	
	public static int getRandomValue(int minValue, int maxValue) {
		Random random = new Random();
		return random.nextInt((maxValue - minValue) + 1) + minValue;
	}
}
