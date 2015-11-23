package com.andres.elevator.entities;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.LinkedList;

import javax.imageio.ImageIO;

import com.andres.elevator.applet.Game;
import com.andres.elevator.utils.GameUtils;

/**
 * Clase que representa el ascensor, encargado de atender ordenadamente
 * a todos los hilos de ejecución que le van llegando.
 * 
 * @author Andrés Hernández Jiménez
 *
 */
public class Ascensor extends Entity implements Runnable {
	
	private Game mGame;
	
	private BufferedImage mSpriteSheet2;
	private BufferedImage mElevatorCloud;
	
	private Thread mThread;

	private LinkedList<Character> mColaDeEspera = new LinkedList<Character>();
	private Character mPersonaSolicitante;
	private Character mPersonaMontada;
	
	private int mPlantaActual = 0;
	
	private boolean mDetener = false;
	
	private final int mMovimiento = 8;
	
	public Ascensor(Game game) {
		mGame = game;
		
		loadElevatorSprite();
		
		mWidth = mElevatorCloud.getWidth();
		mHeight = mElevatorCloud.getHeight();
		mX = (mGame.getWidth() / 3) - (mWidth / 2);
		mY = GameUtils.SUELO_PX;
		
		mThread = new Thread(this);
		mThread.start();
	}
	
	/**
	 * Carga la imagen del ascensor.
	 */
	private void loadElevatorSprite() {
		try {
			mSpriteSheet2 = ImageIO.read(getClass().getResource("/resources/spritesheet2.png"));
			mElevatorCloud = mSpriteSheet2.getSubimage(128, 320, 48, 16);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Método sincronizado que los hilos personaje van llamando para solicitar el ascensor.
	 * @param personaSolicitante
	 */
	public synchronized void solicitar(Character personaSolicitante) {
		// Se añade el hilo a la cola de espera.
		mColaDeEspera.add(personaSolicitante);
		
		// Mientras el hilo se encuentre en la cola de espera, ponerse a dormir.
		while (mColaDeEspera.contains(personaSolicitante)) {
			try {
				wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		// Finalmente se atiende al hilo.
		mPersonaSolicitante = personaSolicitante;
	}

	@Override
	public void run() {
		while (!mDetener) {
			if (mPersonaMontada != null) {
				transportarPersona();
			} else if (mPersonaSolicitante != null) {
				buscarPersona();
			} else {
				// El hilo ascensor se echa a dormir en caso de que no haya ningún hilo en la cola de espera.
				if (mColaDeEspera.size() <= 0) {
					try {
						synchronized (this) {
							this.wait();
						}
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				} else {
					// Eliminar el primer hilo de la cola de espera, para poder atenderlo a continuación.
					mColaDeEspera.removeFirst();
					synchronized (this) {
						// Despertar a todos los hilos.
						notifyAll();
					}
				}
			}
			
			pause();
		}
	}
	
	/**
	 * El ascensor se mueve para buscar un personaje.
	 * - En caso de encontrarse en la misma planta del personaje al que debe recoger, lo recoge.
	 * - En caso contrario se mueve hacia la planta donde se encuentra el personaje esperando.
	 */
	private void buscarPersona() {
		if (mPlantaActual == mPersonaSolicitante.getPlantaOrigen()) {
			recogerPersona();
		} else {
			if (mPlantaActual < mPersonaSolicitante.getPlantaOrigen()) {
				mover(mPlantaActual + 1);
			} else {
				mover(mPlantaActual - 1);
			}
		}
	}
	
	/**
	 * Recoge un personaje y lo monta en el ascensor.
	 */
	private void recogerPersona() {
		mPersonaMontada = mPersonaSolicitante;
		mPersonaMontada.setX(mPersonaMontada.getX() + getWidth() / 2 + mPersonaMontada.getWidth() / 2);
	}
	
	/**
	 * Transporta el personaje a su planta de destino.
	 * - En caso de haber llegado, lo suelta.
	 * - En caso contrario, el ascensor se mueve hacia la planta de destino.
	 */
	private void transportarPersona() {
		if (mPlantaActual == mPersonaMontada.getPlantaDestino()) {
			soltarPersona();
		} else {
			if (mPlantaActual < mPersonaMontada.getPlantaDestino()) {
				mover(mPlantaActual + 1);
			} else {
				mover(mPlantaActual - 1);
			}
		}
	}
	
	/**
	 * Saca al personaje del ascensor y lo colca en el suelo.
	 */
	private void soltarPersona() {
		int movimientoHorizontal = getWidth() / 2 + mPersonaMontada.getWidth() / 2;
		mPersonaMontada.setX(mPersonaMontada.getX() + movimientoHorizontal);
		
		mPersonaMontada.haLlegado();
		mPersonaSolicitante = null;
		mPersonaMontada = null;
	}
	
	/**
	 * El ascensor se mueve verticalmente en dirección a la planta de destino.
	 * @param plantaDestino Planta a la que el ascensor se dirige.
	 */
	private void mover(int plantaDestino) {
		if (mPlantaActual < plantaDestino) {
			mY -= mMovimiento;
		} else {
			mY += mMovimiento;
		}
		
		if (mPersonaMontada != null) {
			if (mPlantaActual < plantaDestino) {
				mPersonaMontada.setY(mPersonaMontada.getY() - mMovimiento);
			} else {
				mPersonaMontada.setY(mPersonaMontada.getY() + mMovimiento);
			}
		}
		
		calcularPlantaActual();
	}
	
	/**
	 * Determina la planta en la que el ascensor se encuentra actualmente.
	 */
	private void calcularPlantaActual() {
		//int ascensorBaseline = mY + mHeight;
		int ascensorBaseline = mY;
		if (ascensorBaseline == GameUtils.SUELO_PX) {
			mPlantaActual = 0;
		} else if (ascensorBaseline == GameUtils.SUELO_PX - GameUtils.PLANTA_ALTURA_PX*1) {
			mPlantaActual = 1;
		} else if (ascensorBaseline == GameUtils.SUELO_PX - GameUtils.PLANTA_ALTURA_PX*2) {
			mPlantaActual = 2;
		} else if (ascensorBaseline == GameUtils.SUELO_PX - GameUtils.PLANTA_ALTURA_PX*3) {
			mPlantaActual = 3;
		} else if (ascensorBaseline == GameUtils.SUELO_PX - GameUtils.PLANTA_ALTURA_PX*4) {
			mPlantaActual = 4;
		} else if (ascensorBaseline == GameUtils.SUELO_PX - GameUtils.PLANTA_ALTURA_PX*5) {
			mPlantaActual = 5;
		}
	}

	/**
	 * Detiene el ascensor.
	 */
	public void detener() {
		mDetener = true;
	}

	@Override
	public void draw(Graphics graphics) {
		mX = (mGame.getWidth() / 3) - (mWidth / 2);
		graphics.drawImage(mElevatorCloud, mX, mY, null);
	}
}
