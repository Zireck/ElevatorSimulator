package com.andres.elevator.entities;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import com.andres.elevator.applet.Edificio;
import com.andres.elevator.utils.Utils;

public class Persona extends Entity implements Runnable {
	
	private static final int IDLE = 0;
	private static final int WALKING = 1;
	private static final int[] NUM_FRAMES_PER_SPRITE_ARRAY = { 1, 3 };
	
	private BufferedImage mSpriteSheet;
	private List<BufferedImage[]> mSprites;
	private Animation mAnimation;
	private int mCurrentAction;
	
	private Thread mThread;
	
	private Edificio mEdificio;
	private Ascensor mAscensor;
	
	private String mNombre;
	private int mPlantaOrigen;
	private int mPlantaDestino;
	
	private boolean mHaEntradoEnAscensor = false;
	private boolean mHaLlegado = false;
	private boolean mHaFinalizado = false;
	
	private final int mMovimiento = 1;
	
	private Color mColor;
	
	Persona(Edificio edificio, Ascensor ascensor) {
		mEdificio = edificio;
		mAscensor = ascensor;
		
		mSpeed = 10;
		
		mWidth = 20;
		mHeight = 16;
		
		loadSprites();
		mAnimation = new Animation();
		mCurrentAction = WALKING;
		mAnimation.setFrames(mSprites.get(WALKING));
		mAnimation.setDelay(mSpeed);
		
		mX = 0;
		
		mThread = new Thread(this);
	}
	
	private void loadSprites() {
		try {
			mSpriteSheet = ImageIO.read(getClass().getResource("/resources/spritesheetcharacters.png"));
			mSprites = new ArrayList<BufferedImage[]>();
			BufferedImage[] spritesIdle = new BufferedImage[NUM_FRAMES_PER_SPRITE_ARRAY[IDLE]];
			BufferedImage[] spritesWalking = new BufferedImage[NUM_FRAMES_PER_SPRITE_ARRAY[WALKING]];
			spritesIdle[0] = mSpriteSheet.getSubimage(80, 32, 16, 16);
			spritesWalking[0] = mSpriteSheet.getSubimage(96, 32, 16, 16);
			spritesWalking[1] = mSpriteSheet.getSubimage(112, 32, 16, 16);
			spritesWalking[2] = mSpriteSheet.getSubimage(128, 32, 16, 16);
			mSprites.add(spritesIdle);
			mSprites.add(spritesWalking);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void executeThread() {
		mThread.start();
	}
	
	public String getNombre() {
		return mNombre;
	}
	
	public int getPlantaOrigen() {
		return mPlantaOrigen;
	}
	
	public void setPlantaOrigen(int plantaOrigen) {
		mPlantaOrigen = plantaOrigen;
		mY = Utils.SUELO_PX - Utils.PLANTA_ALTURA_PX*mPlantaOrigen - mHeight;
	}
	
	public int getPlantaDestino() {
		return mPlantaDestino;
	}
	
	public void setPlantaDestino(int plantaDestino) {
		mPlantaDestino = plantaDestino;
	}
	
	public void setNombre(String nombre) {
		mNombre = nombre;
	}
	
	public void setColor(Color color) {
		mColor = color;
	}
	
	public void haLlegado() {
		mHaLlegado = true;
	}
	
	@Override
	public void run() {
		while (mX + getWidth() <= mAscensor.getX()) {
			mX += mMovimiento;
			mAnimation.update();
			pause();			
		}
		
		/*
		while (mX <= mEdificio.getWidth() / 3 - mAscensor.getWidth()) {
			mX += mMovimiento;
			pause();
		}*/
		
		synchronized (mAscensor) {
			mAscensor.notify();
		}

		mEdificio.setIndicator(mPlantaOrigen, mPlantaDestino);
		
		mCurrentAction = IDLE;
		mAnimation.setFrames(mSprites.get(IDLE));
		
		mAscensor.solicitar(this);
		
		/*
		while (!mHaEntradoEnAscensor) {
			mX += mMovimiento;
			if (mX >= mAscensor.getX() + mAscensor.getWidth() / 2) {
				mHaEntradoEnAscensor = true;
			}
			pause();
		}*/
		/*
		do {
			mX += mMovimiento;
			pause();
		} while (mX < mAscensor.getX() + mAscensor.getWidth() / 2);
		mHaEntradoEnAscensor = true;*/
		
		while (!mHaLlegado) {
			// no-op
			pause();
		}
		
		mEdificio.setIndicator(mPlantaOrigen, -1);
		mEdificio.setPlantaAvailable(mPlantaOrigen, true);
		mCurrentAction = WALKING;
		mAnimation.setFrames(mSprites.get(WALKING));
		while (mX < mEdificio.getWidth()) {
			mX += mMovimiento;
			if (mX > mEdificio.getWidth()/3 + mAscensor.getWidth()/2 + mEdificio.getWidth()/4 - 28 - 6) {
				entrarEnTuberia();
			}
			pause();
		}
		
		mHaFinalizado = true;
		
		System.out.println("Persona hilo muere. -> " + getNombre());
		mSpriteSheet = null;
		mSprites = null;
		mAnimation = null;
		
		Thread.currentThread().interrupt();
	}
	
	private void entrarEnTuberia() {
		mY = Utils.SUELO_PX - mHeight;
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
}
