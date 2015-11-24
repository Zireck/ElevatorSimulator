package com.andres.elevator.applet;

import java.applet.Applet;
import java.awt.Graphics;
import java.awt.Image;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.andres.elevator.entities.Ascensor;
import com.andres.elevator.entities.Character;
import com.andres.elevator.entities.CharacterFactory;
import com.andres.elevator.entities.Prize;
import com.andres.elevator.entities.PrizeFactory;
import com.andres.elevator.utils.GameUtils;

@SuppressWarnings("serial")
public class Game extends Applet implements Runnable {
	
	private static final int MAX_PLANTAS = 5;
	private static final int MAX_PERSONAJES = 5;
	private static final int MAX_PRIZES = 5;
	
	private Thread mThread;
	
	private Background mBackground;
	private Ascensor mAscensor;
	private List<Character> mPersonajes = new ArrayList<Character>(MAX_PERSONAJES);
	private List<Boolean> mPlantasSinPersonajes = new ArrayList<Boolean>(MAX_PLANTAS);
	
	private List<Prize> mPrizes = new ArrayList<Prize>(MAX_PRIZES);
	private List<Boolean> mPlantasSinPremios = new ArrayList<Boolean>(MAX_PLANTAS);
	
	private Image mDoubleBufferImage;
	private Graphics mDoubleBufferGraphics;
	
	public Game() {
		super();
	}
	
	@Override
	public void init() {
		setSize(720, 480);
		setVisible(true);

		for (int i=0; i<MAX_PLANTAS; i++) {
			mPlantasSinPersonajes.add(true);
			mPlantasSinPremios.add(true);
		}
		
		mBackground = new Background();
		mAscensor = new Ascensor(this);
		
		// Iniciamos el double buffer
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
		
	}
	
	/**
	 * Es necesario redibujar el juego cada 100 milsegundos.
	 * Pero debemos controlar que se crean nuevos personajes y premios únicamente cada 2 o 3 segundos.
	 */
	@Override
	public void run() {
		long initTime = System.currentTimeMillis();
		long delta = GameUtils.getRandomValue(2000, 3000);
		while (true) {
			if (System.currentTimeMillis() - initTime > delta) {
				initTime = System.currentTimeMillis();
				delta = GameUtils.getRandomValue(2000, 3000);
				eliminarPersonajesFinalizados();
				crearNuevoPersonajeSiFueraPosible();
				eliminarPremiosConsumidos();
				crearNuevoPremioSiFueraPosible();
			}
			
			repaint();
			
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Crea un nuevo personaje si fuera posible dependiendo de las condiciones actuales.
	 */
	private void crearNuevoPersonajeSiFueraPosible() {
		boolean algunaPlantaLibre = false;
		for (int i=0; i<mPlantasSinPersonajes.size(); i++) {
			if (mPlantasSinPersonajes.get(i)) {
				algunaPlantaLibre = true;
			}
		}
		
		// El personaje se crea si no excede la capacidad máxima de personajes permitidos simultáneamente
		// y si existe alguna planta libre donde poder crearlo.
		if (mPersonajes.size() < MAX_PERSONAJES && algunaPlantaLibre) {
			Character personaje = CharacterFactory.newInstance(this, mAscensor);
			mPlantasSinPersonajes.set(personaje.getPlantaOrigen(), false);
			personaje.executeThread();
			mPersonajes.add(personaje);
		}
	}
	
	/**
	 * Elimina los personajes que ya han finalizado su trayecto al completo.
	 */
	private void eliminarPersonajesFinalizados() {
		Iterator<Character> iteratorPersonajes = mPersonajes.iterator();
		while (iteratorPersonajes.hasNext()) {
			Character personaje = iteratorPersonajes.next();
			if (personaje.haFinalizado()) {
				personaje = null;
				iteratorPersonajes.remove();
			}
		}
	}
	
	/**
	 * Crea un nuevo premio si fuera posible dependiendo de las condiciones actuales.
	 */
	private void crearNuevoPremioSiFueraPosible() {
		boolean algunaPlantaLibre = false;
		for (int i=0; i<mPlantasSinPremios.size(); i++) {
			if (mPlantasSinPremios.get(i)) {
				algunaPlantaLibre = true;
			}
		}
		
		// El premio se crea si no excede la capacidad máxima de premios permitidos simultáneamente
		// y si existe alguna planta libre donde poder crearlo.
		if (mPrizes.size() < MAX_PRIZES && algunaPlantaLibre) {
			Prize prize = PrizeFactory.newInstance(this, mAscensor.getWidth());
			mPlantasSinPremios.set(prize.getPlanta(), false);
			mPrizes.add(prize);
		}
	}
	
	/**
	 * Elimina los premios que ya han sido consumidos previamente por algún personaje.
	 */
	private void eliminarPremiosConsumidos() {
		Iterator<Prize> iteratorPrizes = mPrizes.iterator();
		while (iteratorPrizes.hasNext()) {
			Prize prize = iteratorPrizes.next();
			if (prize.isConsumed()) {
				prize = null;
				iteratorPrizes.remove();
			}
		}
	}
	
	/**
	 * Se dibujará el juego en pantalla siguiendo un orden específico:
	 * 1. Los elementos del fondo que irán detrás de los personajes.
	 * 2. Los premios.
	 * 3. Los personajes vivos.
	 * 4. Los elementos del fondo que irán sobre los personajes.
	 * 5. Los personajes muertos.
	 * 6. El ascensor.
	 * 7. Por último, se dibuja el double buffer.
	 */
	@Override
	public void update(Graphics graphics) {
		if (mAscensor == null || mDoubleBufferGraphics == null) {
			return;
		}
		
		mBackground.drawBehindCharacter(mDoubleBufferGraphics, getWidth(), getHeight(), mAscensor.getWidth());
		
		for (Prize prize : mPrizes) {
			if (prize != null) {
				prize.draw(mDoubleBufferGraphics);
			}
		}
		
		for (Character personaje : mPersonajes) {
			if (personaje != null && !personaje.isDead()) {
				for (int i=0; i<mPrizes.size(); i++) {
					Prize prize = mPrizes.get(i);
					if (personaje.isCollidingWith(prize)) {
						personaje.consumePrize(prize);
						prize.consume();
						mPlantasSinPremios.set(prize.getPlanta(), true);
					}
				}
				
				personaje.draw(mDoubleBufferGraphics);
			}
		}
		
		mBackground.drawInFrontOfCharacter(mDoubleBufferGraphics, getWidth(), getHeight(), mAscensor.getWidth());
		
		for (Character personaje : mPersonajes) {
			if (personaje.isDead()) {
				personaje.draw(mDoubleBufferGraphics);
			}
		}
		
		mAscensor.draw(mDoubleBufferGraphics);
		
		graphics.drawImage(mDoubleBufferImage, 0, 0, this);
	}
	
	/**
	 * Establece el indicador de la planta de origen con la planta de destino.
	 * NOTA: El indicador es el bloque donde se muestra el número que indica la planta a la que desea dirigirse el personaje.
	 * 
	 * @param plantaOrigen Planta del indicador que cambiará.
	 * @param plantaDestino Número que se establecerá en el indicador.
	 */
	public void setIndicator(int plantaOrigen, int plantaDestino) {
		mBackground.setIndicator(plantaOrigen, plantaDestino);
	}
	
	public boolean isPlantaSinPersonajes(int planta) {
		return mPlantasSinPersonajes.get(planta);
	}
	
	public void setPlantaSinPersonajes(int planta, boolean isAvailable) {
		mPlantasSinPersonajes.set(planta, isAvailable);
	}
	
	public boolean isPlantaSinPremio(int planta) {
		return mPlantasSinPremios.get(planta);
	}
	
	public void setPlantaSinPremio(int planta, boolean sinPremio) {
		mPlantasSinPremios.set(planta, sinPremio);
	}
}
