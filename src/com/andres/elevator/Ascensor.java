package com.andres.elevator;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;

public class Ascensor extends Entity implements Runnable {
	
	private Edificio mEdificio;
	
	private Thread mThread;

	private BufferedImage mSprite;
	
	private boolean mOcupado = false;
	private Persona mPersonaSolicitante;
	private Persona mPersonaMontada;
	
	private int mPlantaActual = 0;
	
	private boolean mDetener = false;
	
	private final int mMovimiento = 5;
	
	public Ascensor(Edificio edificio) {
		mEdificio = edificio;
		
		mSpeed = 100;
		
		mWidth = 40;
		mHeight = 50;
		mX = (mEdificio.getWidth() / 3) - (mWidth / 2);
		mY = Utils.SUELO - mHeight;
		
		URL spriteUrl = getClass().getResource("/resources/elevator.png");
		try {
			mSprite = ImageIO.read(spriteUrl);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		mThread = new Thread(this);
		mThread.start();
	}
	
	public synchronized void solicitar(Persona personaSolicitante) {
		System.out.println("Nuevo solicitante: " + personaSolicitante.getNombre());
		while (mOcupado) {
			try {
				wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		mOcupado = true;
		mPersonaSolicitante = personaSolicitante;
		System.out.println("Atendiendo a persona " + mPersonaSolicitante.getNombre());
	}

	@Override
	public void run() {
		while (!mDetener) {
			if (!mOcupado) {
				
				// Despertar al siguiente hilo persona
				synchronized (this) {
					notify();
				}
				
				//System.out.println("Ascensor en estado de espera en planta: " + mPlantaActual);
				// no-op
			} else {
				
				if (mPersonaMontada != null) {
					// llevar a planta destino
					transportarPersona();
				} else if (mPersonaSolicitante != null) {
					// ir a recoger
					buscarPersona();
				}
			}
			
			mEdificio.repaint();
			pause();
		}
	}
	
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
	
	private void recogerPersona() {
		mPersonaMontada = mPersonaSolicitante;
		System.out.println("Montando al usuario solicitante en el ascensor: " + mPersonaMontada.getNombre());
		mPersonaMontada.setX(mPersonaMontada.getX() + getWidth() / 2 + mPersonaMontada.getWidth() / 2);
	}
	
	private void transportarPersona() {
		if (mPlantaActual == mPersonaMontada.getPlantaDestino()) {
			soltarPersona();
			mOcupado = false;
		} else {
			if (mPlantaActual < mPersonaMontada.getPlantaDestino()) {
				mover(mPlantaActual + 1);
			} else {
				mover(mPlantaActual - 1);
			}
		}
	}
	
	private void soltarPersona() {
		mPersonaMontada.setX(mPersonaMontada.getX() + 10);
		mPersonaMontada.haLlegado();
		mPersonaSolicitante = null;
		mPersonaMontada = null;
		System.out.println("El usuario se baja del ascensor porque llega a su destino");
	}
	
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
	
	private void calcularPlantaActual() {
		int ascensorBaseline = mY + mHeight;
		if (ascensorBaseline == Utils.SUELO) {
			mPlantaActual = 0;
		} else if (ascensorBaseline == Utils.SUELO - Utils.PLANTA_ALTURA*1) {
			mPlantaActual = 1;
		} else if (ascensorBaseline == Utils.SUELO - Utils.PLANTA_ALTURA*2) {
			mPlantaActual = 2;
		} else if (ascensorBaseline == Utils.SUELO - Utils.PLANTA_ALTURA*3) {
			mPlantaActual = 3;
		} else if (ascensorBaseline == Utils.SUELO - Utils.PLANTA_ALTURA*4) {
			mPlantaActual = 4;
		} else if (ascensorBaseline == Utils.SUELO - Utils.PLANTA_ALTURA*5) {
			mPlantaActual = 5;
		}	
	}

	public void detener() {
		mDetener = true;
	}

	@Override
	public void draw(Graphics graphics) {
		graphics.setColor(Color.GRAY);
		graphics.fillRect(mX, mY, mWidth, mHeight);
		
		//graphics.drawImage(mSprite, mX, mY, null);
	}
}
