package com.andres.elevator;

import java.applet.Applet;
import java.awt.Color;
import java.awt.Graphics;

@SuppressWarnings("serial")
public class Building extends Applet {
	
	private Elevator mAscensor;
	private Person mPersona1, mPersona2, mPersona3;
	
	@Override
	public void init() {
		setSize(800, 400);
		setVisible(true);
		
		mAscensor = new Elevator(this);
		mPersona1 = new Person(mAscensor, "Fulanito");
		mPersona2 = new Person(mAscensor, "Menganito");
		
		try {
			Thread.sleep(25000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		mPersona3 = new Person(mAscensor, "Nuevo");
	}
	
	@Override
	public void start() {
	}
	
	@Override
	public void stop() {
		mAscensor.detener();
	}
	
	@Override
	public void paint(Graphics graphics) {
		graphics.setColor(Color.CYAN);
		graphics.fillRect(0, 0, getWidth(), Utils.SUELO);
		
		graphics.setColor(Color.DARK_GRAY);
		graphics.fillRect(0, Utils.SUELO, getWidth(), getHeight() - Utils.SUELO);
		
		graphics.setColor(Color.BLACK);
		
		mAscensor.draw(graphics);
	}
}
