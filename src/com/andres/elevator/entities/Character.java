package com.andres.elevator.entities;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.List;

import com.andres.elevator.applet.Game;
import com.andres.elevator.utils.GameUtils;

/**
 * Clase que representa un personaje que se moverá por pantalla y solicitará el ascensor.
 * 
 * @author Andrés Hernández Jiménez
 *
 */
public abstract class Character extends Entity implements Runnable {
	
	protected static final int IDLE_TINY = 0;
	protected static final int WALKING_TINY = 1;
	protected static final int IDLE_REGULAR = 2;
	protected static final int WALKING_REGULAR = 3;
	protected static final int WALKING_REGULAR_FIRE = 4;
	protected static final int DEAD_TINY = 5; 
	protected static final int[] NUM_FRAMES_PER_SPRITE_ARRAY = { 1, 3, 1, 3, 3, 1 };
	
	protected static final int STATE_TINY = 0;
	protected static final int STATE_REGULAR = 1;
	protected static final int STATE_FIRE = 2;
	protected static final int STATE_DEAD = 3;
	
	protected BufferedImage mSpriteSheet;
	protected List<BufferedImage[]> mSprites;
	protected Animation mAnimation;
	protected int mCurrentAction;
	protected int mCurrentState;
	
	private Thread mThread;
	
	private Game mGame;
	private Ascensor mAscensor;
	
	private int mPlantaOrigen;
	private int mPlantaDestino;
	private int mPlantaActual;
	
	private boolean mHaEntradoEnAscensor = false;
	private boolean mHaLlegado = false;
	private boolean mHaFinalizado = false;
	
	private final int mMovimiento = 1;
	
	Character(Game game, Ascensor ascensor) {
		mGame = game;
		mAscensor = ascensor;
		
		// Se determina una velocidad aleatoria para el personaje.
		mSpeed = GameUtils.getRandomValue(8, 15);
		
		loadSprites();
		mAnimation = new Animation();
		mCurrentAction = WALKING_TINY;
		mCurrentState = STATE_TINY;
		mAnimation.setFrames(mSprites.get(WALKING_TINY));
		mAnimation.setDelay(mSpeed);
		startWalking();
		
		mWidth = mAnimation.getImage().getWidth();
		mHeight = mAnimation.getImage().getHeight();
		
		mX = 0;
		
		mThread = new Thread(this);
	}
	
	/**
	 * Carga en memoria la imagen del jugador.
	 * La clase hijo será la encargada de cargar la imagen oportuna.
	 */
	protected abstract void loadSprites();
	
	protected abstract String getNombre();
	
	/**
	 * Inicia la ejecución del hilo.
	 */
	public void executeThread() {
		mThread.start();
	}
	
	public int getPlantaOrigen() {
		return mPlantaOrigen;
	}
	
	public void setPlantaOrigen(int plantaOrigen) {
		mPlantaOrigen = plantaOrigen;
		mY = GameUtils.SUELO_PX - GameUtils.PLANTA_ALTURA_PX*mPlantaOrigen - mAnimation.getImage().getHeight();
	}
	
	public int getPlantaDestino() {
		return mPlantaDestino;
	}
	
	public void setPlantaDestino(int plantaDestino) {
		mPlantaDestino = plantaDestino;
	}
	
	/**
	 * Notifica al personaje que ha llegado satisfactoriamente a la planta de destino.
	 */
	public void haLlegado() {
		mHaLlegado = true;
	}
	
	@Override
	public void run() {
		mPlantaActual = mPlantaOrigen;
		
		// El personaje comienza a andar, y continua haciéndolo hasta llegar al final
		// de la plataforma donde se encuentra, es decir, justo antes del ascensor.
		startWalking();
		while (mX + getWidth() < mAscensor.getX()) {
			mX += mMovimiento;
			mAnimation.update();
			pause();			
		}

		// El personaje se detiene.
		stopWalking();
		
		// Se establece el indicador con la planta a la que el personaje desea ir. 
		mGame.setIndicator(mPlantaOrigen, mPlantaDestino);
		
		// El personaje despierta al ascensor (en caso de que estuviera dormido).
		synchronized (mAscensor) {
			mAscensor.notify();
		}

		// Solicita el ascensor.
		mAscensor.solicitar(this);
		
		while (!mHaLlegado) {
			pause();
		}
		
		// Actualizamos la planta actual, limpiamos el indicador de la planta de origen y
		// notificamos que la planta de origen está disponible para que pueda entrar otro personaje.
		mPlantaActual = mPlantaDestino;
		mGame.setIndicator(mPlantaOrigen, -1);
		mGame.setPlantaSinJugadores(mPlantaOrigen, true);
		
		// El personaje retoma su marcha y lo hace hasta que sale de la pantalla.
		// Bien sea por haber terminado su trayecto o por haber muerto en el camino.
		startWalking();
		while (mX < mGame.getWidth() && mY < mGame.getHeight()) {
			if (mCurrentState == STATE_DEAD) {
				mY += mMovimiento;
			} else {
				mX += mMovimiento;
				if (mX > mGame.getWidth()/3 + mAscensor.getWidth()/2 + mGame.getWidth()/4 - 28 - 6) {
					entrarEnTuberia();
				}
			}
			
			mAnimation.update();
			
			pause();
		}
		
		mHaFinalizado = true;
		mSpriteSheet = null;
		mSprites = null;
		mAnimation = null;
		
		Thread.currentThread().interrupt();
	}
	
	/**
	 * El personaje entra en la tubería, actualizándole sus coordenadas a donde corresponde.
	 */
	private void entrarEnTuberia() {
		mY = GameUtils.SUELO_PX - mAnimation.getImage().getHeight();
	}
	
	@Override
	public void draw(Graphics graphics) {
		if (mAnimation != null) {
			graphics.drawImage(mAnimation.getImage(), mX, mY, null);
		}
	}
	
	/**
	 * El personaje entra en el ascensor, actualizándole sus coordenadas a donde corresponde.
	 */
	public void entrarEnAscensor() {
		while (mX < mAscensor.getX() + mAscensor.getWidth() / 2) {
			mX += mMovimiento;
		}
	}
	
	private void startWalking() {
		if (mCurrentState == STATE_TINY) {
			mCurrentAction = WALKING_TINY;
		} else if (mCurrentState == STATE_REGULAR) {
			mCurrentAction = WALKING_REGULAR;
		}
		mAnimation.setFrames(mSprites.get(mCurrentAction));
	}
	
	private void stopWalking() {
		if (mCurrentState == STATE_TINY) {
			mCurrentAction = IDLE_TINY;
		} else if (mCurrentState == STATE_REGULAR) {
			mCurrentAction = IDLE_REGULAR;
		}
		mAnimation.setFrames(mSprites.get(mCurrentAction));
	}
	
	/**
	 * El personaje consume un premio.
	 * 
	 * @param prize Premio a consumir.
	 */
	public void consumePrize(Prize prize) {
		if (prize instanceof Mushroom) {
			eatMushroom();
		} else if (prize instanceof Flower) {
			eatFlower();
		} else if (prize instanceof Enemy) {
			die();
		}
		mHeight = mAnimation.getImage().getHeight();
		mY = GameUtils.SUELO_PX - mHeight - mPlantaActual*GameUtils.PLANTA_ALTURA_PX;
	}
	
	private void eatMushroom() {
		mCurrentState = STATE_REGULAR;
		mCurrentAction = WALKING_REGULAR;
		mAnimation.setFrames(mSprites.get(mCurrentAction));
		System.out.println("k9d3 array sprites length: " + mSprites.get(mCurrentAction).length);
	}
	
	private void eatFlower() {
		mCurrentState = STATE_FIRE;
		mCurrentAction = WALKING_REGULAR_FIRE;
		mAnimation.setFrames(mSprites.get(mCurrentAction));
	}
	
	private void die() {
		mCurrentState = STATE_DEAD;
		mCurrentAction = DEAD_TINY;
		mAnimation.setFrames(mSprites.get(mCurrentAction));
		mSpeed = 3;
	}
	
	public boolean haEntradoEnAscensor() {
		return mHaEntradoEnAscensor;
	}
	
	public boolean haFinalizado() {
		return mHaFinalizado;
	}
	
	public int getPlantaActual() {
		return mPlantaActual;
	}
	
	public boolean isDead() {
		return mCurrentState == STATE_DEAD;
	}
}
