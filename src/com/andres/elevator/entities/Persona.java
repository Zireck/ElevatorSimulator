package com.andres.elevator.entities;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.List;

import com.andres.elevator.applet.Edificio;
import com.andres.elevator.utils.Utils;

public abstract class Persona extends Entity implements Runnable {
	
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
	
	private Edificio mEdificio;
	private Ascensor mAscensor;
	
	private int mPlantaOrigen;
	private int mPlantaDestino;
	private int mPlantaActual;
	
	private boolean mHaEntradoEnAscensor = false;
	private boolean mHaLlegado = false;
	private boolean mHaFinalizado = false;
	
	private final int mMovimiento = 1;
	
	Persona(Edificio edificio, Ascensor ascensor) {
		mEdificio = edificio;
		mAscensor = ascensor;
		
		//mSpeed = 10;
		mSpeed = Utils.getRandomValue(8, 15);
		
		loadSprites();
		mAnimation = new Animation();
		mCurrentAction = WALKING_TINY;
		mCurrentState = STATE_TINY;
		mAnimation.setFrames(mSprites.get(WALKING_TINY));
		mAnimation.setDelay(mSpeed);
		startWalking();
		
		//mWidth = 20;
		//mHeight = 16;
		mWidth = mAnimation.getImage().getWidth();
		mHeight = mAnimation.getImage().getHeight();
		
		mX = 0;
		
		mThread = new Thread(this);
	}
	
	protected abstract String getNombre();
	protected abstract void loadSprites();
	
	public void executeThread() {
		mThread.start();
	}
	
	public int getPlantaOrigen() {
		return mPlantaOrigen;
	}
	
	public void setPlantaOrigen(int plantaOrigen) {
		mPlantaOrigen = plantaOrigen;
		//mY = Utils.SUELO_PX - Utils.PLANTA_ALTURA_PX*mPlantaOrigen - mHeight;
		mY = Utils.SUELO_PX - Utils.PLANTA_ALTURA_PX*mPlantaOrigen - mAnimation.getImage().getHeight();
	}
	
	public int getPlantaDestino() {
		return mPlantaDestino;
	}
	
	public void setPlantaDestino(int plantaDestino) {
		mPlantaDestino = plantaDestino;
	}
	
	public void haLlegado() {
		mHaLlegado = true;
	}
	
	@Override
	public void run() {
		mPlantaActual = mPlantaOrigen;
		startWalking();
		while (mX + getWidth() < mAscensor.getX()) {
			mX += mMovimiento;
			mAnimation.update();
			pause();			
		}
		
		synchronized (mAscensor) {
			mAscensor.notify();
		}

		mEdificio.setIndicator(mPlantaOrigen, mPlantaDestino);
		
		stopWalking();
		
		mAscensor.solicitar(this);
		
		while (!mHaLlegado) {
			pause();
		}
		
		mPlantaActual = mPlantaDestino;
		mEdificio.setIndicator(mPlantaOrigen, -1);
		mEdificio.setPlantaAvailable(mPlantaOrigen, true);
		
		startWalking();
		
		while (mX < mEdificio.getWidth() && mY < mEdificio.getHeight()) {
			if (mCurrentState == STATE_DEAD) {
				mY += mMovimiento;
			} else {
				mX += mMovimiento;
				if (mX > mEdificio.getWidth()/3 + mAscensor.getWidth()/2 + mEdificio.getWidth()/4 - 28 - 6) {
					entrarEnTuberia();
				}
			}
			pause();
		}
		
		mHaFinalizado = true;
		
		mSpriteSheet = null;
		mSprites = null;
		mAnimation = null;
		
		Thread.currentThread().interrupt();
	}
	
	private void entrarEnTuberia() {
		mY = Utils.SUELO_PX - mAnimation.getImage().getHeight();
	}
	
	@Override
	public void draw(Graphics graphics) {
		/*graphics.setColor(mColor);
		graphics.fillRect(mX, mY, mWidth, mHeight);*/
		if (mAnimation != null) {
			graphics.drawImage(mAnimation.getImage(), mX, mY, null);
		}
	}
	
	public void entrarEnAscensor() {
		while (mX < mAscensor.getX() + mAscensor.getWidth() / 2) {
			mX += mMovimiento;
		}
	}
	
	public boolean haEntradoEnAscensor() {
		return mHaEntradoEnAscensor;
	}
	
	public boolean haFinalizado() {
		return mHaFinalizado;
	}
	
	private void stopWalking() {
		if (mCurrentState == STATE_TINY) {
			mCurrentAction = IDLE_TINY;
		} else if (mCurrentState == STATE_REGULAR) {
			mCurrentAction = IDLE_REGULAR;
		}
		mAnimation.setFrames(mSprites.get(mCurrentAction));
	}
	
	private void startWalking() {
		if (mCurrentState == STATE_TINY) {
			mCurrentAction = WALKING_TINY;
		} else if (mCurrentState == STATE_REGULAR) {
			mCurrentAction = WALKING_REGULAR;
		}
		mAnimation.setFrames(mSprites.get(mCurrentAction));
	}
	
	private void eatMushroom() {
		mCurrentState = STATE_REGULAR;
		mCurrentAction = WALKING_REGULAR;
		mAnimation.setFrames(mSprites.get(mCurrentAction));
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
	
	public void consumePrize(Prize prize) {
		if (prize instanceof Mushroom) {
			eatMushroom();
		} else if (prize instanceof Flower) {
			eatFlower();
		} else if (prize instanceof Enemy) {
			die();
		}
		mHeight = mAnimation.getImage().getHeight();
		mY = Utils.SUELO_PX - mHeight - mPlantaActual*Utils.PLANTA_ALTURA_PX;
	}
	
	public int getPlantaActual() {
		return mPlantaActual;
	}
	
	public boolean isDead() {
		return mCurrentState == STATE_DEAD;
	}
}
