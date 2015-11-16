package com.andres.elevator;

import java.awt.Color;
import java.awt.Graphics;

public class Persona extends Entity implements Runnable {
	
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
	
	public Persona(Edificio edificio, Ascensor ascensor) {
		mEdificio = edificio;
		mAscensor = ascensor;
		
		mSpeed = 10;
		
		mWidth = mHeight = 20;
		
		mX = 0;
		
		mThread = new Thread(this);
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
		mY = Utils.SUELO - Utils.PLANTA_ALTURA*mPlantaOrigen - mHeight;
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
			pause();			
		}
		
		/*
		while (mX <= mEdificio.getWidth() / 3 - mAscensor.getWidth()) {
			mX += mMovimiento;
			pause();
		}*/
		
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
		while (mX < mEdificio.getWidth()) {
			mX += mMovimiento;
			pause();
		}
		
		mHaFinalizado = true;
		
		System.out.println("Persona hilo muere.");
		Thread.currentThread().interrupt();
	}
	
	@Override
	public void draw(Graphics graphics) {
		graphics.setColor(mColor);
		graphics.fillRect(mX, mY, mWidth, mHeight);		
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
