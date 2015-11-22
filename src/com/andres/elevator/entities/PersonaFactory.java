package com.andres.elevator.entities;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.andres.elevator.applet.Edificio;
import com.andres.elevator.utils.Utils;

public class PersonaFactory {

	public static Persona newInstance(Edificio edificio, Ascensor ascensor) {
		Persona persona;
		
		int characterRandom = Utils.getRandomValue(0, 1);
		if (characterRandom == 0) {
			persona = new Mario(edificio, ascensor);
		} else {
			persona = new Luigi(edificio, ascensor);
		}
		
		persona.setPlantaOrigen(generateRandomPlanta(edificio));
		persona.setPlantaDestino(generateRandomPlantaDifferentFrom(persona.getPlantaOrigen()));
		
		return persona;
	}
	
	private static int generateRandomPlanta() {
		Random random = new Random();
		return random.nextInt((Utils.PLANTA_MAX-1 - Utils.PLANTA_MIN) + 1) + Utils.PLANTA_MIN;
	}
	
	private static int generateRandomPlanta(Edificio edificio) {
		List<Integer> plantasLibres = new ArrayList<>();
		for (int i=0; i<Utils.PLANTA_MAX; i++) {
			if (edificio.isPlantaAvailable(i)) {
				plantasLibres.add(i);
			}
		}
		
		if (plantasLibres.size() <= 0) {
			throw new IllegalStateException("No hay plantas libres para generar un nuevo personaje.");
		}
		
		int n = Utils.getRandomValue(0, plantasLibres.size()-1);
		return plantasLibres.get(n);
	}
	
	private static int generateRandomPlantaDifferentFrom(int planta) {
		int nuevaPlanta;
		int i = 0;
		
		do {
			nuevaPlanta = generateRandomPlanta();
			i++;
		} while (nuevaPlanta == planta && i<=Utils.PLANTA_MAX);

		return nuevaPlanta;
	}
}
