package com.andres.elevator.entities;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

import com.andres.elevator.utils.GameUtils;

/**
 * Clase que representa un premio.
 * Éstos pueden ser: una seta, una flor o un enemigo.
 * 
 * @author Andrés Hernández Jiménez
 *
 */
public abstract class Prize extends Entity {
	
	private BufferedImage mSpriteSheet;
	private int mPlanta;
	private boolean mIsConsumed = false;

	Prize(int parentWidth, int ascensorWidth) {
		loadSpriteSheet();
		loadSprite();
		setWidth(getPrizeSprite().getWidth());
		setHeight(getPrizeSprite().getHeight());
		setX((parentWidth/3 + ascensorWidth/2) + 16*4);
	}
	
	protected abstract void loadSprite();
	protected abstract BufferedImage getPrizeSprite();
	
	@Override
	public void draw(Graphics graphics) {
		if (!mIsConsumed) {
			graphics.drawImage(getPrizeSprite(), mX, mY, null);
		}
	}
	
	protected void loadSpriteSheet() {
		try {
			mSpriteSheet = ImageIO.read(getClass().getResource("/resources/spritesheet3.gif"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	protected BufferedImage getSpriteSheet() {
		return mSpriteSheet;
	}
	
	public void setPlanta(int planta) {
		mPlanta = planta;
		setY(GameUtils.SUELO_PX - getHeight() - planta*GameUtils.PLANTA_ALTURA_PX);
	}
	
	public int getPlanta() {
		return mPlanta;
	}

	/**
	 * Establece el premio como consumido.
	 */
	public void consume() {
		mIsConsumed = true;
	}
	
	public boolean isConsumed() {
		return mIsConsumed;
	}
}
