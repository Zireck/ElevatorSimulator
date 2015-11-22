package com.andres.elevator.entities;

import java.awt.Graphics;
import java.awt.Rectangle;

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
	
	public boolean isCollidingWith(Entity otherEntity) {
		Rectangle rectangle1 = new Rectangle(mX, mY, mWidth, mHeight);
		Rectangle rectangle2 = new Rectangle(otherEntity.getX(), otherEntity.getY(), otherEntity.getWidth(), otherEntity.getHeight());

		return rectangle1.intersects(rectangle2) ? true : false;
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
