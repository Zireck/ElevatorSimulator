package com.andres.elevator.entities;

import java.awt.image.BufferedImage;

/**
 * Clase encarga de animar el movimiento de los personajes.
 * 
 * @author Andrés Hernández Jiménez
 *
 */
public class Animation {

	private BufferedImage[] frames;
	private int currentFrame;
	
	private long startTime;
	private long delay;
	
	private boolean playedOnce;
	
	public Animation() {
		playedOnce = false;
	}
	
	/**
	 * Método encargado de establecer un nuevo array de frames.
	 * 
	 * @param frames
	 */
	public void setFrames(BufferedImage[] frames) {
		this.frames = frames;
		currentFrame = 0;
		startTime = System.nanoTime();
		playedOnce = false;
	}
	
	public void setDelay(long d) {
		delay = d;
	}
	
	public void setFrame(int i) {
		currentFrame = i;
	}
	
	/**
	 * Método encargado de actualizar el frame actual.
	 */
	public void update() {
		if (delay == -1) {
			return;
		}
		
		long elapsed = (System.nanoTime() - startTime) / 1000000;
		if (elapsed > delay) {
			currentFrame++;
			startTime = System.nanoTime();
		}
		if (currentFrame == frames.length) {
			currentFrame = 0;
			playedOnce = true;
		}
		
	}
	
	public int getFrame() {
		return currentFrame;
	}
	
	public BufferedImage getImage() {
		return frames[currentFrame];
	}
	
	public boolean hasPlayedOnce() {
		return playedOnce;
	}
}
