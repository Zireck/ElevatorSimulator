package com.andres.elevator.applet;

import java.applet.Applet;
import java.awt.Graphics;
import java.awt.Image;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.andres.elevator.entities.Ascensor;
import com.andres.elevator.entities.Persona;
import com.andres.elevator.entities.PersonaFactory;
import com.andres.elevator.entities.Prize;
import com.andres.elevator.entities.PrizeFactory;
import com.andres.elevator.utils.Utils;

@SuppressWarnings("serial")
public class Edificio extends Applet implements Runnable {
	
	private static final int MAX_PLANTAS = 5;
	private static final int MAX_PERSONAS = 5;
	private static final int MAX_PRIZES = 5;
	
	private Thread mThread;
	
	private Background mBackground;
	private Ascensor mAscensor;
	private List<Persona> mPersonas = new ArrayList<Persona>(MAX_PERSONAS);
	private List<Boolean> mPlantasLibres = new ArrayList<Boolean>(MAX_PLANTAS);
	
	private List<Prize> mPrizes = new ArrayList<Prize>(MAX_PRIZES);
	private List<Boolean> mPlantasSinPremios = new ArrayList<Boolean>(MAX_PLANTAS);
	
	private Image mDoubleBufferImage;
	private Graphics mDoubleBufferGraphics;
	
	public Edificio() {
		super();
	}
	
	@Override
	public void init() {
		setSize(720, 480);
		setVisible(true);

		for (int i=0; i<MAX_PLANTAS; i++) {
			mPlantasLibres.add(true);
			mPlantasSinPremios.add(true);
		}
		
		mBackground = new Background();
		mAscensor = new Ascensor(this);
		
		// init double buffer
		mDoubleBufferImage = createImage(getSize().width, getSize().height);
		mDoubleBufferGraphics = mDoubleBufferImage.getGraphics();
		
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
		long delta = Utils.getRandomValue(2000, 3000);
		while (true) {
			
			if (System.currentTimeMillis() - initTime > delta) {
				initTime = System.currentTimeMillis();
				delta = Utils.getRandomValue(3000, 3000);
				eliminarPersonasFinalizadas();
				crearNuevaPersonaSiFueraNecesario();
				eliminarPremiosConsumidos();
				crearNuevoPremioSiFueraNecesario();
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
				//mPlantasLibres.set(persona.getPlantaDestino(), true);
				persona = null;
				iteratorPersonas.remove();
			}
		}
	}
	
	private void crearNuevaPersonaSiFueraNecesario() {
		boolean algunaPlantaLibre = false;
		for (int i=0; i<mPlantasLibres.size(); i++) {
			if (mPlantasLibres.get(i)) {
				algunaPlantaLibre = true;
			}
		}
		
		if (mPersonas.size() < MAX_PERSONAS && algunaPlantaLibre) {
			Persona persona = PersonaFactory.newInstance(this, mAscensor);
			mPlantasLibres.set(persona.getPlantaOrigen(), false);
			persona.executeThread();
			mPersonas.add(persona);
		}
	}
	
	private void eliminarPremiosConsumidos() {
		Iterator<Prize> iteratorPrizes = mPrizes.iterator();
		while (iteratorPrizes.hasNext()) {
			Prize prize = iteratorPrizes.next();
			if (prize.isConsumed()) {
				//mPlantasLibres.set(persona.getPlantaDestino(), true);
				prize = null;
				iteratorPrizes.remove();
			}
		}
	}
	
	private void crearNuevoPremioSiFueraNecesario() {
		boolean algunaPlantaLibre = false;
		for (int i=0; i<mPlantasSinPremios.size(); i++) {
			if (mPlantasSinPremios.get(i)) {
				algunaPlantaLibre = true;
			}
		}
		
		if (mPrizes.size() < MAX_PRIZES && algunaPlantaLibre) {
			Prize prize = PrizeFactory.newInstance(this, mAscensor.getWidth());
			mPlantasSinPremios.set(prize.getPlanta(), false);
			mPrizes.add(prize);
		}
	}
	
	public boolean isPlantaAvailable(int planta) {
		return mPlantasLibres.get(planta);
	}
	
	public void setPlantaAvailable(int planta, boolean isAvailable) {
		mPlantasLibres.set(planta, isAvailable);
	}
	
	public boolean isPlantaSinPremio(int planta) {
		return mPlantasSinPremios.get(planta);
	}
	
	public void setPlantaSinPremio(int planta, boolean sinPremio) {
		mPlantasSinPremios.set(planta, sinPremio);
	}
	/*
	@Override
	public void update(Graphics graphics) {
	    if (mDoubleBuffer && mBufferGraphics != null) {
	    	mBufferGraphics.clearRect(0, 0, mBufferWidth, mBufferHeight);
	    	paintGame(mBufferGraphics);
	    	graphics.drawImage(mBufferImage, 0, 0, this);
	    } else {
	    	paintGame(graphics);
	    }
	}*/
	/*
	@Override
	public void paint(Graphics graphics) {
		if (mAscensor == null) {
			return;
		}
		
	    if (mDoubleBuffer && mBufferGraphics != null) {
	    	mBufferGraphics.clearRect(0, 0, mBufferWidth, mBufferHeight);
	    	paintGame(mBufferGraphics);
	    	graphics.drawImage(mBufferImage, 0, 0, this);
	    } else {
	    	paintGame(graphics);
	    }
	}*/
	
	/*
	private void paintGame(Graphics graphics) {
	    if(mBufferWidth!=getSize().width || mBufferImage==null || mBufferGraphics==null) {
	    	resetBuffer();
	    }
		
		mBackground.drawBehindCharacter(graphics, getWidth(), getHeight(), mAscensor.getWidth());
		
		for (Prize prize : mPrizes) {
			if (prize != null) {
				prize.draw(graphics);
			}
		}
		
		for (Persona persona : mPersonas) {
			if (persona != null && !persona.isDead()) {
				for (int i=0; i<mPrizes.size(); i++) {
					Prize prize = mPrizes.get(i);
					if (persona.isCollidingWith(prize)) {
						persona.consumePrize(prize);
						prize.consume();
						mPlantasSinPremios.set(prize.getPlanta(), true);
					}
				}
				
				persona.draw(graphics);
			}
		}
		
		mBackground.drawInFrontOfCharacter(graphics, getWidth(), getHeight(), mAscensor.getWidth());
		
		// Dibujar personajes muertos
		for (Persona persona : mPersonas) {
			if (persona.isDead()) {
				persona.draw(graphics);
			}
		}
		
		mAscensor.draw(graphics);
	}*/
	
	
	@Override
	public void update(Graphics graphics) {
		//super.update(graphics);
		
		if (mAscensor == null) {
			return;
		}
		
		if (mDoubleBufferGraphics == null) {
			return;
		}
		
		mBackground.drawBehindCharacter(mDoubleBufferGraphics, getWidth(), getHeight(), mAscensor.getWidth());
		
		for (Prize prize : mPrizes) {
			if (prize != null) {
				prize.draw(mDoubleBufferGraphics);
			}
		}
		
		for (Persona persona : mPersonas) {
			if (persona != null && !persona.isDead()) {
				for (int i=0; i<mPrizes.size(); i++) {
					Prize prize = mPrizes.get(i);
					if (persona.isCollidingWith(prize)) {
						persona.consumePrize(prize);
						prize.consume();
						mPlantasSinPremios.set(prize.getPlanta(), true);
					}
				}
				
				persona.draw(mDoubleBufferGraphics);
			}
		}
		
		mBackground.drawInFrontOfCharacter(mDoubleBufferGraphics, getWidth(), getHeight(), mAscensor.getWidth());
		
		// Dibujar personajes muertos
		for (Persona persona : mPersonas) {
			if (persona.isDead()) {
				persona.draw(mDoubleBufferGraphics);
			}
		}
		
		mAscensor.draw(mDoubleBufferGraphics);
		
		graphics.drawImage(mDoubleBufferImage, 0, 0, this);
	}
	
	/*
	@Override
	protected void paintBuffer(Graphics graphics) {
		if (mAscensor == null) {
			return;
		}
		
		mBackground.drawBehindCharacter(graphics, getWidth(), getHeight(), mAscensor.getWidth());
		
		for (Prize prize : mPrizes) {
			if (prize != null) {
				prize.draw(graphics);
			}
		}
		
		for (Persona persona : mPersonas) {
			if (persona != null && !persona.isDead()) {
				for (int i=0; i<mPrizes.size(); i++) {
					Prize prize = mPrizes.get(i);
					if (persona.isCollidingWith(prize)) {
						persona.consumePrize(prize);
						prize.consume();
						mPlantasSinPremios.set(prize.getPlanta(), true);
					}
				}
				
				persona.draw(graphics);
			}
		}
		
		mBackground.drawInFrontOfCharacter(graphics, getWidth(), getHeight(), mAscensor.getWidth());
		
		// Dibujar personajes muertos
		for (Persona persona : mPersonas) {
			if (persona.isDead()) {
				persona.draw(graphics);
			}
		}
		
		mAscensor.draw(graphics);		
	}*/

	/*
	@Override
	public void paint(Graphics graphics) {
		if (mAscensor == null) {
			return;
		}
		
		mBackground.drawBehindCharacter(graphics, getWidth(), getHeight(), mAscensor.getWidth());
		
		for (Prize prize : mPrizes) {
			if (prize != null) {
				prize.draw(graphics);
			}
		}
		
		for (Persona persona : mPersonas) {
			if (persona != null && !persona.isDead()) {
				for (int i=0; i<mPrizes.size(); i++) {
					Prize prize = mPrizes.get(i);
					if (persona.isCollidingWith(prize)) {
						persona.consumePrize(prize);
						prize.consume();
						mPlantasSinPremios.set(prize.getPlanta(), true);
					}
				}
				
				persona.draw(graphics);
			}
		}
		
		mBackground.drawInFrontOfCharacter(graphics, getWidth(), getHeight(), mAscensor.getWidth());
		
		// Dibujar personajes muertos
		for (Persona persona : mPersonas) {
			if (persona.isDead()) {
				persona.draw(graphics);
			}
		}
		
		mAscensor.draw(graphics);
	}*/
	
	public void setIndicator(int plantaOrigen, int plantaDestino) {
		mBackground.setIndicator(plantaOrigen, plantaDestino);
	}
}
