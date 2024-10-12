package org.kybe;

import com.mojang.blaze3d.platform.NativeImage;

import java.io.IOException;
import java.io.InputStream;

public class Sprite {
	InputStream stream;
	NativeImage image;
	public int width;
	public int height;
	public int singleWidth;

	/*
	 * @param stream The input stream of the sprite file
	 * @param width The width of the sprite file
	 * @param height The height of the sprite file
	 * @param singleWidth The width of a single sprite in the sprite file
	 * @throws IOException
	 */
	public Sprite(InputStream stream, int width, int height, int singleWidth) throws IOException {
		this.stream = stream;
		this.width = width;
		this.height = height;
		this.singleWidth = singleWidth;
		this.image = NativeImage.read(stream);
	}

	/*
	 * @param spriteIndex The index of the sprite to get (starting from 0)
	 * @return The sprite as NativeImage at the given index
	 *
	 * @note the method also removes the background color (0xFFFFFF00) from the sprite
	 */
	public NativeImage getSprite(int spriteIndex) {
		int startX = spriteIndex * singleWidth;
		int startY = 0;

		NativeImage result = new NativeImage(singleWidth, height, false);

		/*
		 * Copy the pixels from the original image to the new image
		 */
		for (int x = 0; x < singleWidth; x++) {
			for (int y = 0; y < height; y++) {
				int pixelColor = this.image.getPixelRGBA(startX + x, startY + y);

				// Remove the background color (0xFFFFFF00)
				if (pixelColor == 0xFFFFFF00) {
					// Set the pixel to transparent
					int transparentCyan = 0x0000000;
					result.setPixelRGBA(x, y, transparentCyan);
				} else {
					// Copy the original pixel
					result.setPixelRGBA(x, y, pixelColor); // Copy the original pixel
				}
			}
		}
		return result;
	}

	public int getSpriteCount() {
		return width / singleWidth - 1;
	}
}