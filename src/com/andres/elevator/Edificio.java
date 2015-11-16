package com.andres.elevator;

import java.applet.Applet;
import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
public class Edificio extends Applet implements Runnable {
	
	private static final int MAX_PERSONAS = 5;
	
	private Thread mThread;
	
	private Ascensor mAscensor;
	private List<Persona> mPersonas;
	
	@Override
	public void init() {
		setSize(640, 400);
		setVisible(true);
		
		mPersonas = new ArrayList<Persona>();
		
		
		mAscensor = new Ascensor(this);
		
		mThread = new Thread(this);
		mThread.start();
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
		
		// Plantas
		int floorWidth = 3;
		graphics.setColor(Color.DARK_GRAY);
		graphics.fillRect(0, Utils.SUELO - Utils.PLANTA_ALTURA*1, getWidth() / 3 - mAscensor.getWidth() / 2, floorWidth);
		graphics.fillRect(0, Utils.SUELO - Utils.PLANTA_ALTURA*2, getWidth() / 3 - mAscensor.getWidth() / 2, floorWidth);
		graphics.fillRect(0, Utils.SUELO - Utils.PLANTA_ALTURA*3, getWidth() / 3 - mAscensor.getWidth() / 2, floorWidth);
		graphics.fillRect(0, Utils.SUELO - Utils.PLANTA_ALTURA*4, getWidth() / 3 - mAscensor.getWidth() / 2, floorWidth);
		graphics.fillRect(0, Utils.SUELO - Utils.PLANTA_ALTURA*5, getWidth() / 3 - mAscensor.getWidth() / 2, floorWidth);
		
		graphics.fillRect(getWidth()/3 + mAscensor.getWidth()/2, Utils.SUELO - Utils.PLANTA_ALTURA*1, getWidth() / 4, floorWidth);
		graphics.fillRect(getWidth()/3 + mAscensor.getWidth()/2, Utils.SUELO - Utils.PLANTA_ALTURA*2, getWidth() / 4, floorWidth);
		graphics.fillRect(getWidth()/3 + mAscensor.getWidth()/2, Utils.SUELO - Utils.PLANTA_ALTURA*3, getWidth() / 4, floorWidth);
		graphics.fillRect(getWidth()/3 + mAscensor.getWidth()/2, Utils.SUELO - Utils.PLANTA_ALTURA*4, getWidth() / 4, floorWidth);
		graphics.fillRect(getWidth()/3 + mAscensor.getWidth()/2, Utils.SUELO - Utils.PLANTA_ALTURA*5, getWidth() / 4, floorWidth);
		
		graphics.fillRect(getWidth()/3 + mAscensor.getWidth()/2 + getWidth() / 4, Utils.SUELO - Utils.PLANTA_ALTURA*6, 5, Utils.PLANTA_ALTURA*5 + 3);
		
		mAscensor.draw(graphics);
		
		for (Persona persona : mPersonas) {
			if (persona != null) {
				persona.draw(graphics);
			}
		}
	}

	@Override
	public void run() {
		while (true) {
			repaint();
			
			for (int i=0; i<mPersonas.size(); i++) {
				if (mPersonas.get(i).haFinalizado()) {
					mPersonas.remove(i);
				}
			}
			
			if (mPersonas.size() < MAX_PERSONAS) {
				Persona persona = PersonaFactory.newInstance(this, mAscensor);
				persona.executeThread();
				mPersonas.add(persona);
			}
			
			try {
				Thread.sleep(Utils.getRandomValue(3000, 8000));
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
