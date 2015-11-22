package com.andres.elevator.entities;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.LinkedList;

import javax.imageio.ImageIO;

import com.andres.elevator.applet.Edificio;
import com.andres.elevator.utils.Utils;

public class Ascensor extends Entity implements Runnable {
	
	private Edificio mEdificio;
	
	private BufferedImage mSpriteSheet2;
	private BufferedImage mElevatorCloud;
	
	private Thread mThread;

	private LinkedList<Persona> mColaDeEspera;
	private boolean mOcupado = false;
	private Persona mPersonaSolicitante;
	private Persona mPersonaMontada;
	private int mSolicitantes = 0;
	
	private int mPlantaActual = 0;
	
	private boolean mDetener = false;
	
	private final int mMovimiento = 8;
	
	public Ascensor(Edificio edificio) {
		mEdificio = edificio;
		
		mColaDeEspera = new LinkedList<Persona>();
		
		mSpeed = 100;
		
		try {
			mSpriteSheet2 = ImageIO.read(getClass().getResource("/resources/spritesheet2.png"));
			mElevatorCloud = mSpriteSheet2.getSubimage(128, 320, 48, 16);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		mWidth = mElevatorCloud.getWidth();
		mHeight = mElevatorCloud.getHeight();
		mX = (mEdificio.getWidth() / 3) - (mWidth / 2);
		mY = Utils.SUELO_PX;
		
		mThread = new Thread(this);
		mThread.start();
	}
	
	public synchronized void solicitar(Persona personaSolicitante) {
		System.out.println("Nuevo solicitante: " + personaSolicitante.getNombre());
		mColaDeEspera.add(personaSolicitante);
		mSolicitantes++;
		
		while (mOcupado) {
			try {
				wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		mOcupado = true;
		//mPersonaSolicitante = personaSolicitante;
		mPersonaSolicitante = mColaDeEspera.removeFirst();
		System.out.println("Atendiendo a persona " + mPersonaSolicitante.getNombre());
	}

	@Override
	public void run() {
		while (!mDetener) {
			if (!mOcupado) {
				
				synchronized (this) {
					// Despertar al siguiente hilo persona
					notify();
					
					try {
						// Si tras despertar un nuevo hilo sigue sin haber solicitantes... echarse a dormir.
						if (mSolicitantes <= 0) {
							wait();
						}
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					/*
					try {
						//System.out.println("Ascensor en estado de espera en planta: " + mPlantaActual);
						// no-op
						if (mColaDeEspera.size() <= 0)  {
							mOcupado = false;
							wait();
						}
					} catch (InterruptedException e) {
						e.printStackTrace();
					}*/
				}
			} else {
				if (mPersonaMontada != null) {
					// llevar a planta destino
					transportarPersona();
				} else if (mPersonaSolicitante != null) {
					// ir a recoger
					buscarPersona();
				}
			}
			
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
		mSolicitantes--;
		int incremento = getWidth() / 2 + mPersonaMontada.getWidth() / 2;
		mPersonaMontada.setX(mPersonaMontada.getX() + incremento);
		
		String nombrePersona = mPersonaMontada.getNombre();
		mPersonaMontada.haLlegado();
		mPersonaSolicitante = null;
		mPersonaMontada = null;
		System.out.println("El usuario se baja del ascensor porque llega a su destino ---> " + nombrePersona);
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
		ascensorBaseline = mY;
		if (ascensorBaseline == Utils.SUELO_PX) {
			mPlantaActual = 0;
		} else if (ascensorBaseline == Utils.SUELO_PX - Utils.PLANTA_ALTURA_PX*1) {
			mPlantaActual = 1;
		} else if (ascensorBaseline == Utils.SUELO_PX - Utils.PLANTA_ALTURA_PX*2) {
			mPlantaActual = 2;
		} else if (ascensorBaseline == Utils.SUELO_PX - Utils.PLANTA_ALTURA_PX*3) {
			mPlantaActual = 3;
		} else if (ascensorBaseline == Utils.SUELO_PX - Utils.PLANTA_ALTURA_PX*4) {
			mPlantaActual = 4;
		} else if (ascensorBaseline == Utils.SUELO_PX - Utils.PLANTA_ALTURA_PX*5) {
			mPlantaActual = 5;
		}
	}

	public void detener() {
		mDetener = true;
	}

	@Override
	public void draw(Graphics graphics) {
		mX = (mEdificio.getWidth() / 3) - (mWidth / 2);
		graphics.drawImage(mElevatorCloud, mX, mY, null);
	}
}
