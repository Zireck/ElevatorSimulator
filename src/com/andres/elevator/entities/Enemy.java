package com.andres.elevator.entities;

import java.awt.image.BufferedImage;

import com.andres.elevator.utils.GameUtils;

public class Enemy extends Prize {
	
	private static final int MAX_ENEMIES = 6;
	private static final int BOWSER = 0;
	private static final int BEETLE = 1;
	private static final int GOOMBA = 2;
	private static final int SPINY = 3;
	private static final int KOOPA = 4;
	private static final int PIRANHA = 5;
	
	private BufferedImage mSprite;

	Enemy(int parentWidth, int ascensorWidth) {
		super(parentWidth, ascensorWidth);
	}

	@Override
	protected void loadSprite() {
		loadRandomSprite();
	}
	
	private void loadRandomSprite() {
		int enemy = GameUtils.getRandomValue(0, MAX_ENEMIES);
		switch (enemy) {
			case BOWSER:
				mSprite = getSpriteSheet().getSubimage(13, 896, 32, 32);
				break;
			case BEETLE:
				mSprite = getSpriteSheet().getSubimage(17, 976, 16, 17);
				break;
			case GOOMBA:
				mSprite = getSpriteSheet().getSubimage(187, 894, 16, 16);
				break;
			case SPINY:
				mSprite = getSpriteSheet().getSubimage(451, 763, 16, 15);
				break;
			case KOOPA:
				mSprite = getSpriteSheet().getSubimage(481, 816, 16, 23);
				break;
			case PIRANHA:
				mSprite = getSpriteSheet().getSubimage(587, 867, 16, 22);
				break;
			default:
				// Goomba
				mSprite = getSpriteSheet().getSubimage(187, 894, 16, 16);
				break;
		}
	}

	@Override
	protected BufferedImage getPrizeSprite() {
		return mSprite;
	}
}
