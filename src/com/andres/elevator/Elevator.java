package com.andres.elevator;

import java.awt.Color;
import java.awt.Graphics;
import java.util.LinkedList;

public class Elevator extends Entity implements Runnable {
	
	private static final int PLANTA_MAX = 5;
	private static final int PLANTA_MIN = 1;
	
	private Building mEdificio;
	
	private Thread mThread;
	
	private LinkedList<Person> mPersonas;
	
	private boolean mOcupado = false;
	private Person mPersonaSolicitante;
	private Person mPersonaMontada;
	
	private int mPlantaActual = 1;
	
	private boolean mDetener = false;
	
	public Elevator(Building edificio) {
		mEdificio = edificio;
		
		mWidth = 40;
		mHeight = 50;
		mX = (mEdificio.getWidth() / 2) - (mWidth / 2);
		mY = Utils.SUELO - mHeight;
		
		mPersonas = new LinkedList<Person>();
		mThread = new Thread(this);
		mThread.start();
	}
	
	public synchronized void solicitar(Person personaSolicitante) {
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
				
				System.out.println("Ascensor en estado de espera en planta: " + mPlantaActual);
				// no-op
			} else {
				
				if (mPersonaMontada != null) {
					// llevar a planta destino
					transportarPersona();
				} else if (mPersonaSolicitante != null) {
					// ir a recoger
					recogerPersona();
				}
			}
			
			mEdificio.repaint();
			pause();
		}
	}
	
	private void recogerPersona() {
		if (mPlantaActual == mPersonaSolicitante.getPlantaOrigen()) {
			mPersonaMontada = mPersonaSolicitante;
			System.out.println("Montando al usuario solicitante en el ascensor: " + mPersonaMontada.getNombre());
		} else {
			if (mPlantaActual < mPersonaSolicitante.getPlantaOrigen()) {
				mover(mPlantaActual + 1);
			} else {
				mover(mPlantaActual - 1);
			}
		}
	}
	
	private void transportarPersona() {
		if (mPlantaActual == mPersonaMontada.getPlantaDestino()) {
			mPersonaMontada.haLlegado();
			mPersonaSolicitante = null;
			mPersonaMontada = null;
			System.out.println("El usuario se baja del ascensor porque llega a su destino");
			mOcupado = false;
		} else {
			if (mPlantaActual < mPersonaMontada.getPlantaDestino()) {
				mover(mPlantaActual + 1);
			} else {
				mover(mPlantaActual - 1);
			}
		}
	}
	
	private void mover(int planta) {
		System.out.println("El ascensor está en planta " + mPlantaActual + " y se moverá a planta: " + planta);
		mPlantaActual = planta;
		mY = (Utils.SUELO - (mPlantaActual * Utils.PLANTA_ALTURA)) - mHeight;
	}

	private void pause() {
		try {
			Thread.sleep(1500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public void detener() {
		mDetener = true;
	}
	
	public void draw(Graphics graphics) {
		graphics.setColor(Color.GRAY);
		graphics.fillRect(mX, mY, mWidth, mHeight);
	}

}
