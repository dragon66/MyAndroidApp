/**
 * Copyright (c) 2014-2016 by Wen Yu.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Any modifications to this file must keep this entire header intact.
 *
 * Change History - most recent changes go on top of previous changes
 *
 * FrameReader.java
 *
 * Who   Date       Description
 * ====  =========  ====================================================
 * WY    22Nov2015  Initial creation
 */

package com.github.dragon66;

import java.io.InputStream;

import android.graphics.Bitmap;

// Helper class to read GIF frames one at a time
public class FrameReader extends AnimatedGIFReader {
	// Get the next frame as a GIFFrame
	public GIFFrame getGIFFrame(InputStream is) throws Exception {
		Bitmap bmp = getFrameAsBitmap(is);
		return (bmp != null)?new GIFFrame(bmp, image_x, image_y, delay, disposalMethod, userInputFlag, transparencyFlag, transparent_color):null;
	}
	
	public GIFFrame getGIFFrameEx(InputStream is) throws Exception {
		Bitmap bmp = getFrameAsBitmapEx(is);
		return (bmp != null)?new GIFFrame(bmp, image_x, image_y, delay, disposalMethod, userInputFlag, transparencyFlag, transparent_color):null;
	}
	
	public Bitmap read(InputStream is) throws Exception {
		return null; // NOOP
	}
}