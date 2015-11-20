package com.andres.elevator.applet;

import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.andres.elevator.entities.Ascensor;
import com.andres.elevator.entities.Persona;
import com.andres.elevator.entities.PersonaFactory;
import com.andres.elevator.utils.Utils;

@SuppressWarnings("serial")
public class Edificio extends DoubleBufferApplet implements Runnable {
	
	private static final int MAX_PLANTAS = 5;
	private static final int MAX_PERSONAS = 5;
	
	private Thread mThread;
	
	private Ascensor mAscensor;
	private List<Persona> mPersonas = new ArrayList<Persona>(MAX_PERSONAS);
	private List<Boolean> mPlantasLibres = new ArrayList<Boolean>(MAX_PLANTAS);
	
	@Override
	public void init() {
		setSize(640, 400);
		setVisible(true);

		for (int i=0; i<MAX_PLANTAS; i++) {
			mPlantasLibres.add(true);
		}
		
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
	public void run() {
		long initTime = System.currentTimeMillis();
		long delta = Utils.getRandomValue(1000, 3000);
		while (true) {
			
			if (System.currentTimeMillis() - initTime > delta) {
				initTime = System.currentTimeMillis();
				delta = Utils.getRandomValue(1000, 3000);
				eliminarPersonasFinalizadas();
				crearNuevaPersonaSiFueraNecesario();
			}
			
			repaint();
			
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			/*
			try {
				Thread.sleep(Utils.getRandomValue(2000, 4000));
			} catch (InterruptedException e) {
				e.printStackTrace();
			}*/
		}
	}
	
	private void eliminarPersonasFinalizadas() {
		Iterator<Persona> iteratorPersonas = mPersonas.iterator();
		while (iteratorPersonas.hasNext()) {
			Persona persona = iteratorPersonas.next();
			if (persona.haFinalizado()) {
				mPlantasLibres.set(persona.getPlantaDestino(), true);
				persona = null;
				iteratorPersonas.remove();
			}
		}
	}
	
	private void crearNuevaPersonaSiFueraNecesario() {
		if (mPersonas.size() < MAX_PERSONAS) {
			Persona persona = PersonaFactory.newInstance(this, mAscensor);
			mPlantasLibres.set(persona.getPlantaOrigen(), false);
			persona.executeThread();
			mPersonas.add(persona);
		}
	}
	
	public boolean isPlantaAvailable(int planta) {
		return mPlantasLibres.get(planta);
	}
	
	public void setPlantaAvailable(int planta, boolean isAvailable) {
		mPlantasLibres.set(planta, isAvailable);
	}

	@Override
	protected void paintBuffer(Graphics graphics) {
		if (mAscensor == null) {
			return;
		}
		
		graphics.setColor(Color.CYAN);
		graphics.fillRect(0, 0, getWidth(), Utils.SUELO_PX);
		
		graphics.setColor(Color.DARK_GRAY);
		graphics.fillRect(0, Utils.SUELO_PX, getWidth(), getHeight() - Utils.SUELO_PX);
		
		int floorWidth = 3;
		graphics.setColor(Color.DARK_GRAY);
		graphics.fillRect(0, Utils.SUELO_PX - Utils.PLANTA_ALTURA_PX*1, getWidth() / 3 - mAscensor.getWidth() / 2, floorWidth);
		graphics.fillRect(0, Utils.SUELO_PX - Utils.PLANTA_ALTURA_PX*2, getWidth() / 3 - mAscensor.getWidth() / 2, floorWidth);
		graphics.fillRect(0, Utils.SUELO_PX - Utils.PLANTA_ALTURA_PX*3, getWidth() / 3 - mAscensor.getWidth() / 2, floorWidth);
		graphics.fillRect(0, Utils.SUELO_PX - Utils.PLANTA_ALTURA_PX*4, getWidth() / 3 - mAscensor.getWidth() / 2, floorWidth);
		graphics.fillRect(0, Utils.SUELO_PX - Utils.PLANTA_ALTURA_PX*5, getWidth() / 3 - mAscensor.getWidth() / 2, floorWidth);
		
		graphics.fillRect(getWidth()/3 + mAscensor.getWidth()/2, Utils.SUELO_PX - Utils.PLANTA_ALTURA_PX*1, getWidth() / 4, floorWidth);
		graphics.fillRect(getWidth()/3 + mAscensor.getWidth()/2, Utils.SUELO_PX - Utils.PLANTA_ALTURA_PX*2, getWidth() / 4, floorWidth);
		graphics.fillRect(getWidth()/3 + mAscensor.getWidth()/2, Utils.SUELO_PX - Utils.PLANTA_ALTURA_PX*3, getWidth() / 4, floorWidth);
		graphics.fillRect(getWidth()/3 + mAscensor.getWidth()/2, Utils.SUELO_PX - Utils.PLANTA_ALTURA_PX*4, getWidth() / 4, floorWidth);
		graphics.fillRect(getWidth()/3 + mAscensor.getWidth()/2, Utils.SUELO_PX - Utils.PLANTA_ALTURA_PX*5, getWidth() / 4, floorWidth);
		
		graphics.fillRect(getWidth()/3 + mAscensor.getWidth()/2 + getWidth() / 4, Utils.SUELO_PX - Utils.PLANTA_ALTURA_PX*6, 5, Utils.PLANTA_ALTURA_PX*5 + 3);
		
		mAscensor.draw(graphics);
		
		for (Persona persona : mPersonas) {
			if (persona != null) {
				persona.draw(graphics);
			}
		}		
	}
}
