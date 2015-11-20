package com.andres.elevator.entities;

import java.awt.Graphics;

public abstract class Entity {

	protected int mWidth;
	protected int mHeight;
	protected int mX;
	protected int mY;
	protected int mSpeed = 100;
	
	public abstract void draw(Graphics graphics);
	
	protected void pause() {
		try {
			Thread.sleep(mSpeed);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public int getWidth() {
		return mWidth;
	}
	
	public void setWidth(int width) {
		mWidth = width;
	}
	
	public int getHeight() {
		return mHeight;
	}
	
	public void setHeight(int height) {
		mHeight = height;
	}
	
	public int getX() {
		return mX;
	}
	public void setX(int x) {
		mX = x;
	}
	
	public int getY() {
		return mY;
	}
	
	public void setY(int y) {
		mY = y;
	}
}
