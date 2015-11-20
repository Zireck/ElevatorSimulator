package com.andres.elevator.entities;

import java.awt.Color;
import java.util.Random;

import com.andres.elevator.applet.Edificio;
import com.andres.elevator.utils.Utils;

public class PersonaFactory {

	public static Persona newInstance(Edificio edificio, Ascensor ascensor) {
		Persona persona = new Persona(edificio, ascensor);
		
		persona.setColor(generateRandomColor());
		persona.setPlantaOrigen(generateRandomPlanta(edificio));
		persona.setPlantaDestino(generateRandomPlantaDifferentFrom(persona.getPlantaOrigen()));
		
		String[] nombres = { "Carlos", "Sandra", "Maria", "Juan", "Cristina", "Merce", "Bea", "Raul", "Felipe", "Marta", "Santi", "Marcos", "Andres", "Luisa" };
		int nnombre = Utils.getRandomValue(0, nombres.length-1);
		persona.setNombre(nombres[nnombre]);
		
		
		return persona;
	}
	
	private static Color generateRandomColor() {
		Random random = new Random();
		int r = random.nextInt(255);
		int g = random.nextInt(255);
		int b = random.nextInt(255);
		
		return new Color(r, g, b);
	}
	
	private static int generateRandomPlanta() {
		Random random = new Random();
		return random.nextInt((Utils.PLANTA_MAX-1 - Utils.PLANTA_MIN) + 1) + Utils.PLANTA_MIN;
	}
	
	private static int generateRandomPlanta(Edificio edificio) {
		int planta;
		boolean plantaValida = true;
		
		do {
			plantaValida = true;
			planta = generateRandomPlanta();
			if (!edificio.isPlantaAvailable(planta)) {
				plantaValida = false;
			}
		} while (!plantaValida);
		
		return planta;
	}
	
	private static int generateRandomPlantaDifferentFrom(int planta) {
		int nuevaPlanta;
		
		do {
			nuevaPlanta = generateRandomPlanta();
		} while (nuevaPlanta == planta);

		return nuevaPlanta;
	}
}
