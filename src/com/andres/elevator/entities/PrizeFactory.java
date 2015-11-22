package com.andres.elevator.entities;

import java.util.ArrayList;
import java.util.List;

import com.andres.elevator.applet.Edificio;
import com.andres.elevator.utils.Utils;

public class PrizeFactory {

	public static Prize newInstance(Edificio edificio, int ascensorWidth) {
		Prize prize;
		
		int randomPrize = Utils.getRandomValue(0, 2);
		if (randomPrize == 0) {
			prize = new Mushroom(edificio.getWidth(), ascensorWidth);
		} else if (randomPrize == 1) {
			prize = new Flower(edificio.getWidth(), ascensorWidth);
		} else {
			prize = new Enemy(edificio.getWidth(), ascensorWidth);
		}
		
		prize.setPlanta(generateRandomPlanta(edificio));
		
		return prize;
	}
	
	private static int generateRandomPlanta(Edificio edificio) {
		List<Integer> plantasLibres = new ArrayList<>();
		for (int i=0; i<Utils.PLANTA_MAX; i++) {
			if (edificio.isPlantaSinPremio(i)) {
				plantasLibres.add(i);
			}
		}
		
		if (plantasLibres.size() <= 0) {
			throw new IllegalStateException("No hay plantas libres para generar un nuevo premio.");
		}
		
		int n = Utils.getRandomValue(0, plantasLibres.size()-1);
		return plantasLibres.get(n);
	}
	
}
