package com.andres.elevator.entities;

import java.util.ArrayList;
import java.util.List;

import com.andres.elevator.applet.Game;
import com.andres.elevator.utils.GameUtils;

/**
 * Clase factor�a encargada de generar personajes con propiedades aleatorias.
 * 
 * @author Andr�s Hern�ndez Jim�nez
 *
 */
public class CharacterFactory {

	/**
	 * Crea una nueva instancia de la clase Character.
	 * @param game
	 * @param ascensor
	 * @return Personaje creado.
	 */
	public static Character newInstance(Game game, Ascensor ascensor) {
		Character personaje;
		
		int characterRandom = GameUtils.getRandomValue(0, 1);
		if (characterRandom == 0) {
			personaje = new Mario(game, ascensor);
		} else {
			personaje = new Luigi(game, ascensor);
		}
		
		personaje.setPlantaOrigen(generarPlantaDeOrigen(game));
		personaje.setPlantaDestino(generarPlantaDeDestino(game, personaje.getPlantaOrigen()));
		
		return personaje;
	}
	
	/**
	 * Genera una planta de origen teniendo en cuenta que no debe estar ocupada por otro personaje.
	 * @param game
	 * @return Planta de origen.
	 */
	private static int generarPlantaDeOrigen(Game game) {
		List<Integer> plantasLibres = new ArrayList<>();
		for (int i=0; i<GameUtils.PLANTA_MAX; i++) {
			if (game.isPlantaSinPersonajes(i)) {
				plantasLibres.add(i);
			}
		}
		
		if (plantasLibres.size() <= 0) {
			throw new IllegalStateException("No hay plantas libres para generar un nuevo personaje.");
		}
		
		int n = GameUtils.getRandomValue(0, plantasLibres.size()-1);
		return plantasLibres.get(n);
	}
	
	/**
	 * Genera una planta de destino teniendo en cuenta que debe ser distinta a la planta de origen.
	 * @param game
	 * @param planta Planta de origen.
	 * @return Planta de destino.
	 */
	private static int generarPlantaDeDestino(Game game, int planta) {
		List<Integer> plantasDestino = new ArrayList<>();
		for (int i=0; i<GameUtils.PLANTA_MAX; i++) {
			if (i != planta) {
				plantasDestino.add(i);
			}
		}
		
		int n = GameUtils.getRandomValue(0, plantasDestino.size()-1);
		return plantasDestino.get(n);
	}
}
