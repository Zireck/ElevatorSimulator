package com.andres.elevator;

public class Person extends Entity implements Runnable {
	
	private Thread mThread;
	
	private Elevator mAscensor;

	private String mNombre;
	private int mPlantaOrigen;
	private int mPlantaDestino;
	
	private boolean mHaLlegado = false;
	
	public Person(Elevator ascensor, String nombre) {
		mAscensor = ascensor;
		mNombre = nombre;
		
		mPlantaOrigen = 5;
		mPlantaDestino = 2;
		
		mThread = new Thread(this);
		mThread.start();
	}
	
	public String getNombre() {
		return mNombre;
	}
	
	public int getPlantaOrigen() {
		return mPlantaOrigen;
	}
	
	public int getPlantaDestino() {
		return mPlantaDestino;
	}
	
	public void haLlegado() {
		mHaLlegado = true;
	}
	
	@Override
	public void run() {
		mAscensor.solicitar(this);
		while (!mHaLlegado) {
			// no-op
		}
		
		System.out.println("Persona hilo muere.");
		Thread.currentThread().interrupt();
	}
	
}
