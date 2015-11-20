package com.andres.elevator.entities;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.LinkedList;

import javax.imageio.ImageIO;

import com.andres.elevator.applet.Edificio;
import com.andres.elevator.utils.Utils;

public class Ascensor extends Entity implements Runnable {
	
	private Edificio mEdificio;
	
	private Thread mThread;

	private BufferedImage mSprite;
	
	private LinkedList<Persona> mColaDeEspera;
	private boolean mOcupado = false;
	private Persona mPersonaSolicitante;
	private Persona mPersonaMontada;
	
	private int mPlantaActual = 0;
	
	private boolean mDetener = false;
	
	private final int mMovimiento = 5;
	
	public Ascensor(Edificio edificio) {
		mEdificio = edificio;
		
		mColaDeEspera = new LinkedList<Persona>();
		
		mSpeed = 100;
		
		mWidth = 40;
		mHeight = 50;
		mX = (mEdificio.getWidth() / 3) - (mWidth / 2);
		mY = Utils.SUELO_PX - mHeight;
		
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
		mColaDeEspera.add(personaSolicitante);
		while (mOcupado) {
			try {
				wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		mOcupado = true;
		mPersonaSolicitante = mColaDeEspera.removeFirst();
		System.out.println("Atendiendo a persona " + mPersonaSolicitante.getNombre());
	}

	@Override
	public void run() {
		while (!mDetener) {
			if (!mOcupado) {
				
				// Despertar al siguiente hilo persona
				synchronized (this) {
					notify();
					
					try {
						//System.out.println("Ascensor en estado de espera en planta: " + mPlantaActual);
						// no-op
						if (mColaDeEspera.size() <= 0)  {
							mOcupado = false;
							wait();
						}
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
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
		int incremento = getWidth() / 2 + mPersonaMontada.getWidth() / 2;
		mPersonaMontada.setX(mPersonaMontada.getX() + incremento);
		
		String nombrePersona = mPersonaMontada.getNombre();
		mPersonaMontada.haLlegado();
		mPersonaSolicitante = null;
		mPersonaMontada = null;
		System.out.println("El usuario se baja del ascensor porque llega a su destino --------->" + nombrePersona);
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
		graphics.setColor(Color.GRAY);
		graphics.fillRect(mX, mY, mWidth, mHeight);
		
		//graphics.drawImage(mSprite, mX, mY, null);
	}
}
