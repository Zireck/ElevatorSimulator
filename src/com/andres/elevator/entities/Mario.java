package com.andres.elevator.entities;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import com.andres.elevator.applet.Game;

public class Mario extends Character {

	Mario(Game game, Ascensor ascensor) {
		super(game, ascensor);
	}

	@Override
	protected void loadSprites() {
		try {
			mSpriteSheet = ImageIO.read(getClass().getResource("/resources/spritesheetcharacters.png"));
			mSprites = new ArrayList<BufferedImage[]>();
			BufferedImage[] spritesIdleTiny = new BufferedImage[NUM_FRAMES_PER_SPRITE_ARRAY[IDLE_TINY]];
			BufferedImage[] spritesWalkingTiny = new BufferedImage[NUM_FRAMES_PER_SPRITE_ARRAY[WALKING_TINY]];
			BufferedImage[] spritesIdleRegular = new BufferedImage[NUM_FRAMES_PER_SPRITE_ARRAY[IDLE_REGULAR]];
			BufferedImage[] spritesWalkingRegular = new BufferedImage[NUM_FRAMES_PER_SPRITE_ARRAY[WALKING_REGULAR]];
			BufferedImage[] spritesWalkingRegularFire = new BufferedImage[NUM_FRAMES_PER_SPRITE_ARRAY[WALKING_REGULAR_FIRE]];
			BufferedImage[] spritesDeadTiny= new BufferedImage[NUM_FRAMES_PER_SPRITE_ARRAY[DEAD_TINY]];
			spritesIdleTiny[0] = mSpriteSheet.getSubimage(80, 32, 16, 16);
			spritesWalkingTiny[0] = mSpriteSheet.getSubimage(96, 32, 16, 16);
			spritesWalkingTiny[1] = mSpriteSheet.getSubimage(112, 32, 16, 16);
			spritesWalkingTiny[2] = mSpriteSheet.getSubimage(128, 32, 16, 16);
			spritesIdleRegular[0] = mSpriteSheet.getSubimage(80, 0, 16, 32);
			spritesWalkingRegular[0] = mSpriteSheet.getSubimage(96, 0, 16, 32);
			spritesWalkingRegular[1] = mSpriteSheet.getSubimage(112, 0, 16, 32);
			spritesWalkingRegular[2] = mSpriteSheet.getSubimage(128, 0, 16, 32);
			spritesWalkingRegularFire[0] = mSpriteSheet.getSubimage(96, 96, 16, 32);
			spritesWalkingRegularFire[1] = mSpriteSheet.getSubimage(112, 96, 16, 32);
			spritesWalkingRegularFire[2] = mSpriteSheet.getSubimage(128, 96, 16, 32);
			spritesDeadTiny[0] = mSpriteSheet.getSubimage(176, 32, 16, 16);
			mSprites.add(spritesIdleTiny);
			mSprites.add(spritesWalkingTiny);
			mSprites.add(spritesIdleRegular);
			mSprites.add(spritesWalkingRegular);
			mSprites.add(spritesWalkingRegularFire);
			mSprites.add(spritesDeadTiny);
		} catch (IOException e) {
			e.printStackTrace();
		}		
	}

	@Override
	protected String getNombre() {
		return getClass().getSimpleName();
	}
}
