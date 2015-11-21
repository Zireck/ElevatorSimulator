package com.andres.elevator.applet;

import java.applet.Applet;
import java.awt.Graphics;
import java.awt.Image;

/**
 * Fuente:
 * ¿Cómo implementar double buffer en Java AWT?
 * http://www.codeproject.com/Articles/2136/Double-buffer-in-standard-Java-AWT
 * 
 * @author Andrés Hernández
 *
 */
@SuppressWarnings("serial")
public abstract class DoubleBufferApplet extends Applet {

	private boolean mDoubleBuffer = true;
	private int mBufferWidth;
	private int mBufferHeight;
	private Image mBufferImage;
	private Graphics mBufferGraphics;
	
	public DoubleBufferApplet() {
		// no-op
	}
	
	protected abstract void paintBuffer(Graphics graphics);
	
	@Override
	public void paint(Graphics graphics) {
	    if(mBufferWidth!=getSize().width || mBufferImage==null || mBufferGraphics==null) {
	    	resetBuffer();
	    }
	    
	    if (mDoubleBuffer && mBufferGraphics != null) {
	    	mBufferGraphics.clearRect(0, 0, mBufferWidth, mBufferHeight);
	    	paintBuffer(mBufferGraphics);
	    	graphics.drawImage(mBufferImage, 0, 0, this);
	    } else {
	    	paintBuffer(graphics);
	    }
	}
	
	private void resetBuffer() {
		if (mBufferGraphics != null) {
			mBufferGraphics.dispose();
			mBufferGraphics = null;
		}
		
		if (mBufferImage != null) {
			mBufferImage.flush();
			mBufferImage = null;
		}
		
		System.gc();
		
		mBufferImage = createImage(getSize().width, getSize().height);
		mBufferGraphics = mBufferImage.getGraphics();
	}
}
