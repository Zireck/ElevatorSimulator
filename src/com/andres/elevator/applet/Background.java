package com.andres.elevator.applet;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import com.andres.elevator.utils.GameUtils;

/**
 * Clase encargada de dibujar en pantalla el fondo del juego, así como
 * los decorados y los indicadores de solicitud del ascensor.
 * 
 * @author Andrés Hernández Jiménez
 *
 */
public class Background {
	
	private static final int TILE_SIZE = 16;
	
	public static final String COLOR_BACKGROUND = "#6b8cff";
	
	private BufferedImage mSpriteSheet1;
	private BufferedImage mSpriteSheet3;
	private BufferedImage mSpriteSheet4;
	
	private BufferedImage mRegularBlock;
	private BufferedImage mPipeVertical;
	private BufferedImage mPipeFromLeft;
	private BufferedImage mPipeToRight;
	private BufferedImage mFlag;
	private BufferedImage mCastle;
	
	private BufferedImage mBlockQuestion;
	private BufferedImage mBlockNoQuestion;
	private BufferedImage[] mNumbers;
	
	private BufferedImage mMountainBig;
	private BufferedImage mBushSimple;
	private BufferedImage mBushDouble;
	private BufferedImage mTree;
	private BufferedImage mCloudSimple;
	private BufferedImage mCloudDouble;
	
	private BufferedImage mLavaUpper;
	private BufferedImage mLavaRegular;
	
	private List<Integer> mIndicators;
	
	public Background() {
		mNumbers = new BufferedImage[GameUtils.PLANTA_MAX];
		mIndicators = new ArrayList<>();
		for (int i=0; i<GameUtils.PLANTA_MAX; i++) {
			mIndicators.add(-1);
		}
		
		loadSprites();
	}
	
	private void loadSprites() {
		try {
			mSpriteSheet1 = ImageIO.read(getClass().getResource("/resources/spritesheet1.png"));
			mRegularBlock = mSpriteSheet1.getSubimage(0, 0, TILE_SIZE, TILE_SIZE);
			mPipeVertical = mSpriteSheet1.getSubimage(50, 48, 28, 16);
			mPipeFromLeft = mSpriteSheet1.getSubimage(80, 64, 48, 32);
			mPipeToRight = mSpriteSheet1.getSubimage(0, 64, 48, 32);
			
			mSpriteSheet3 = ImageIO.read(getClass().getResource("/resources/spritesheet3.gif"));
			mFlag = mSpriteSheet3.getSubimage(257, 46, 27, 168);
			mCastle = mSpriteSheet3.getSubimage(272, 218, 80, 80);
			mBlockQuestion = mSpriteSheet3.getSubimage(372, 160, 16, 16);
			mBlockNoQuestion = mSpriteSheet3.getSubimage(373, 84, 16, 16);
			mMountainBig = mSpriteSheet3.getSubimage(99, 160, 80, 35);
			mBushSimple = mSpriteSheet3.getSubimage(51, 253, 32, 16);
			mBushDouble = mSpriteSheet3.getSubimage(151, 253, 48, 16);
			mTree = mSpriteSheet3.getSubimage(72, 271, 16, 46);
			mCloudSimple = mSpriteSheet3.getSubimage(162, 198, 32, 24);
			mCloudDouble = mSpriteSheet3.getSubimage(46, 198, 48, 24);
			mLavaUpper = mSpriteSheet3.getSubimage(622, 449, 32, 11);
			mLavaRegular = mSpriteSheet3.getSubimage(622, 462, 32, 29);
			
			mSpriteSheet4 = ImageIO.read(getClass().getResource("/resources/spritesheet4.png"));
			mNumbers[0] = mSpriteSheet4.getSubimage(139, 209, 6, 6);
			mNumbers[1] = mSpriteSheet4.getSubimage(133, 202, 4, 6);
			mNumbers[2] = mSpriteSheet4.getSubimage(138, 202, 6, 6);
			mNumbers[3] = mSpriteSheet4.getSubimage(145, 202, 6, 6);
			mNumbers[4] = mSpriteSheet4.getSubimage(152, 202, 6, 6);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Dibuja en pantalla lo que estará detrás de los personajes.
	 * @param graphics
	 * @param parentWidth
	 * @param parentHeight
	 * @param ascensorWidth
	 */
	public void drawBehindCharacter(Graphics graphics, int parentWidth, int parentHeight, int ascensorWidth) {
		// SKY
		graphics.setColor(Color.decode(Background.COLOR_BACKGROUND));
		graphics.fillRect(0, 0, parentWidth, parentHeight);
		
		// LAVA
		int lavaHorizontalTimes = parentWidth / mLavaUpper.getWidth() + 1;
		for (int i=0; i<lavaHorizontalTimes; i++) {
			graphics.drawImage(mLavaUpper, i*mLavaUpper.getWidth(), GameUtils.SUELO_PX+mLavaUpper.getHeight()*2, null);
		}
		
		int lavaVerticalTimes = (parentHeight - GameUtils.SUELO_PX) / mLavaRegular.getHeight() + 1;
		for (int i=0; i<lavaHorizontalTimes; i++) {
			for (int j=0; j<lavaVerticalTimes; j++) {
				graphics.drawImage(mLavaRegular, i*mLavaRegular.getWidth(), GameUtils.SUELO_PX+mLavaUpper.getHeight()*3 + j*mLavaRegular.getHeight(), null);
			}
		}
		
		// Nubes.
		graphics.drawImage(mCloudSimple, mCloudSimple.getWidth()*2, mCloudSimple.getHeight()/2*3, null);
		graphics.drawImage(mCloudSimple, parentWidth - mCloudSimple.getWidth()*3, mCloudSimple.getHeight()*3, null);
		graphics.drawImage(mCloudDouble, mCloudDouble.getWidth()*6, mCloudDouble.getHeight()*3, null);
		graphics.drawImage(mCloudDouble, parentWidth - mCloudDouble.getWidth()*4, mCloudDouble.getHeight()*6, null);
		
		// Decorado.
		graphics.drawImage(mMountainBig, mMountainBig.getWidth(), GameUtils.SUELO_PX - mMountainBig.getHeight(), null);
		graphics.drawImage(mBushDouble, mBushSimple.getWidth(), GameUtils.SUELO_PX-2*GameUtils.PLANTA_ALTURA_PX - mBushSimple.getHeight(), null);
		graphics.drawImage(mBushSimple, mBushSimple.getWidth()*4, GameUtils.SUELO_PX-4*GameUtils.PLANTA_ALTURA_PX - mBushSimple.getHeight(), null);
		graphics.drawImage(mTree, mTree.getWidth()*3, GameUtils.SUELO_PX - 4*GameUtils.PLANTA_ALTURA_PX - mTree.getHeight(), null);
		
		// Bandera y Castillo.
		graphics.drawImage(mCastle, parentWidth-mCastle.getWidth() - mCastle.getWidth()/2, GameUtils.SUELO_PX - mCastle.getHeight(), null);
		graphics.drawImage(mFlag, parentWidth-mCastle.getWidth() - mCastle.getWidth()/2 - mFlag.getWidth()*2, GameUtils.SUELO_PX - mFlag.getHeight(), null);
	}
	
	/**
	 * Dibuja en pantalla lo que se mostrará encima de los personajes.
	 * @param graphics
	 * @param parentWidth
	 * @param parentHeight
	 * @param ascensorWidth
	 */
	public void drawInFrontOfCharacter(Graphics graphics, int parentWidth, int parentHeight, int ascensorWidth) {
		/*int floorWidth = 3;
		graphics.setColor(Color.DARK_GRAY);
		graphics.fillRect(0, GameUtils.SUELO_PX - GameUtils.PLANTA_ALTURA_PX*1, parentWidth / 3 - ascensorWidth / 2, floorWidth);
		graphics.fillRect(0, GameUtils.SUELO_PX - GameUtils.PLANTA_ALTURA_PX*2, parentWidth / 3 - ascensorWidth / 2, floorWidth);
		graphics.fillRect(0, GameUtils.SUELO_PX - GameUtils.PLANTA_ALTURA_PX*3, parentWidth / 3 - ascensorWidth / 2, floorWidth);
		graphics.fillRect(0, GameUtils.SUELO_PX - GameUtils.PLANTA_ALTURA_PX*4, parentWidth / 3 - ascensorWidth / 2, floorWidth);
		
		graphics.fillRect(parentWidth/3 + ascensorWidth/2, GameUtils.SUELO_PX - GameUtils.PLANTA_ALTURA_PX*1, parentWidth / 4, floorWidth);
		graphics.fillRect(parentWidth/3 + ascensorWidth/2, GameUtils.SUELO_PX - GameUtils.PLANTA_ALTURA_PX*2, parentWidth / 4, floorWidth);
		graphics.fillRect(parentWidth/3 + ascensorWidth/2, GameUtils.SUELO_PX - GameUtils.PLANTA_ALTURA_PX*3, parentWidth / 4, floorWidth);
		graphics.fillRect(parentWidth/3 + ascensorWidth/2, GameUtils.SUELO_PX - GameUtils.PLANTA_ALTURA_PX*4, parentWidth / 4, floorWidth);
		
		graphics.fillRect(parentWidth/3 + ascensorWidth/2 + parentWidth / 4, GameUtils.SUELO_PX - GameUtils.PLANTA_ALTURA_PX*6, 5, GameUtils.PLANTA_ALTURA_PX*5 + 3);
		*/
		
		// Suelo
		int numBlocksWidthSection1 = ((parentWidth / 3 - ascensorWidth / 2) / mRegularBlock.getWidth()) +1;
		int numBlocksHeight = (parentHeight / TILE_SIZE) + 1;
		for (int i=0; i<numBlocksHeight; i++) {
			for (int j=0; j<numBlocksWidthSection1; j++) {
				graphics.drawImage(mRegularBlock, TILE_SIZE*j, GameUtils.SUELO_PX + (i*TILE_SIZE), null);
			}
		}
		
		int numBlocksWidthSection2 = (((parentWidth/3 + ascensorWidth/2 + parentWidth / 4) - (parentWidth/3 + ascensorWidth/2)) / mRegularBlock.getWidth());
		numBlocksWidthSection2 = parentWidth - parentWidth/3 + ascensorWidth/2 / TILE_SIZE;
		for (int i=0; i<numBlocksHeight; i++) {
			for (int j=0; j<numBlocksWidthSection2; j++) {
				//graphics.drawImage(mRegularBlock, 16*j, Utils.SUELO_PX + (i*TILE_SIZE), null);
				graphics.drawImage(mRegularBlock, (parentWidth/3 + ascensorWidth/2) + j * mRegularBlock.getWidth(), GameUtils.SUELO_PX + (i*TILE_SIZE), null);
			}
		}		
		
		// Plataformas.
		int plantaWidthSection1 = ((parentWidth / 3 - ascensorWidth / 2) / mRegularBlock.getWidth());
		for (int i=1; i<GameUtils.PLANTA_MAX; i++) {
			for (int j=0; j<plantaWidthSection1; j++) {
				graphics.drawImage(mRegularBlock, j*mRegularBlock.getWidth(), GameUtils.SUELO_PX - GameUtils.PLANTA_ALTURA_PX * i, null);
			}
		}
		
		int plantaWidthSection2 = (((parentWidth/3 + ascensorWidth/2 + parentWidth / 4) - (parentWidth/3 + ascensorWidth/2)) / mRegularBlock.getWidth()) +1;
		for (int i=1; i<GameUtils.PLANTA_MAX; i++) {
			for (int j=0; j<plantaWidthSection2; j++) {
				graphics.drawImage(mRegularBlock, (parentWidth/3 + ascensorWidth/2) + j * mRegularBlock.getWidth(), GameUtils.SUELO_PX - GameUtils.PLANTA_ALTURA_PX * i, null);
			}
		}
		
		// Tuberías.
		int numPipesVertical = (parentHeight / mPipeVertical.getHeight()) + 1;
		for (int i=0; i<numPipesVertical; i++) {
			graphics.drawImage(mPipeVertical, parentWidth/3 + ascensorWidth/2 + parentWidth / 4, i*mPipeVertical.getHeight(), null);
		}
		
		for (int i=0; i<GameUtils.PLANTA_MAX; i++) {
			graphics.drawImage(mPipeFromLeft, parentWidth/3 + ascensorWidth/2 + parentWidth /4 - mPipeVertical.getWidth() - 6, GameUtils.SUELO_PX - mPipeFromLeft.getHeight() - i*GameUtils.PLANTA_ALTURA_PX, null);
		}
		
		graphics.drawImage(mPipeToRight, parentWidth/3 + ascensorWidth/2 + parentWidth/4 + mPipeVertical.getWidth() - mPipeVertical.getWidth()/2, GameUtils.SUELO_PX - mPipeToRight.getHeight(), null);
		
		// Indicadores de solicitud del ascensor.
		int baseX = plantaWidthSection1*mRegularBlock.getWidth();
		for (int i=0; i<GameUtils.PLANTA_MAX; i++) {
			if (mIndicators.get(i) < 0) {
				graphics.drawImage(mBlockQuestion, baseX, GameUtils.SUELO_PX - GameUtils.PLANTA_ALTURA_PX*i, null);
			} else {
				graphics.drawImage(mBlockNoQuestion, baseX, GameUtils.SUELO_PX - GameUtils.PLANTA_ALTURA_PX*i, null);
				graphics.drawImage(mNumbers[mIndicators.get(i)], baseX + mBlockNoQuestion.getWidth()/2 - mNumbers[mIndicators.get(i)].getWidth()/2, GameUtils.SUELO_PX - GameUtils.PLANTA_ALTURA_PX*i + mBlockNoQuestion.getHeight()/2 - mNumbers[mIndicators.get(i)].getHeight()/2, null);
				//graphics.drawString(String.valueOf(mIndicators.get(i)), plantaWidthSection1*mRegularBlock.getWidth(), Utils.SUELO_PX - Utils.PLANTA_ALTURA_PX*i);
			}
		}
	}
	
	public void setIndicator(int plantaOrigen, int plantaDestino) {
		mIndicators.set(plantaOrigen, plantaDestino);
	}
}
