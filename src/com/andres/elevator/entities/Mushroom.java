package com.andres.elevator.entities;

import java.awt.image.BufferedImage;

public class Mushroom extends Prize {
	
	public Mushroom(int parentWidth, int ascensorWidth) {
		super(parentWidth, ascensorWidth);
	}

	private BufferedImage mSprite;
	
	@Override
	protected void loadSprite() {
		mSprite = getSpriteSheet().getSubimage(71, 43, 16, 16);
	}

	@Override
	protected BufferedImage getPrizeSprite() {
		return mSprite;
	}
}
