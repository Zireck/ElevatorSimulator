package com.andres.elevator.entities;

import java.awt.image.BufferedImage;

public class Flower extends Prize {
	
	private BufferedImage mSprite;

	public Flower(int parentWidth, int ascensorWidth) {
		super(parentWidth, ascensorWidth);
	}

	@Override
	protected void loadSprite() {
		mSprite = getSpriteSheet().getSubimage(52, 64, 16, 16);
	}

	@Override
	protected BufferedImage getPrizeSprite() {
		return mSprite;
	}
}
