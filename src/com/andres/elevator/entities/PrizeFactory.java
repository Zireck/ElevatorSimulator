package com.andres.elevator.entities;

import java.util.ArrayList;
import java.util.List;

import com.andres.elevator.applet.Game;
import com.andres.elevator.utils.GameUtils;

public class PrizeFactory {

	public static Prize newInstance(Game game, int ascensorWidth) {
		Prize prize;
		
		int randomPrize = GameUtils.getRandomValue(0, 2);
		if (randomPrize == 0) {
			prize = new Mushroom(game.getWidth(), ascensorWidth);
		} else if (randomPrize == 1) {
			prize = new Flower(game.getWidth(), ascensorWidth);
		} else {
			prize = new Enemy(game.getWidth(), ascensorWidth);
		}
		
		prize.setPlanta(generateRandomPlanta(game));
		
		return prize;
	}
	
	private static int generateRandomPlanta(Game game) {
		List<Integer> plantasLibres = new ArrayList<>();
		for (int i=0; i<GameUtils.PLANTA_MAX; i++) {
			if (game.isPlantaSinPremio(i)) {
				plantasLibres.add(i);
			}
		}
		
		if (plantasLibres.size() <= 0) {
			throw new IllegalStateException("No hay plantas libres para generar un nuevo premio.");
		}
		
		int n = GameUtils.getRandomValue(0, plantasLibres.size()-1);
		return plantasLibres.get(n);
	}
	
}
