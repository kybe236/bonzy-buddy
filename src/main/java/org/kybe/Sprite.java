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

	public Sprite(InputStream stream, int width, int height, int singleWidth) throws IOException {
		this.stream = stream;
		this.width = width;
		this.height = height;
		this.singleWidth = singleWidth;
		this.image = NativeImage.read(stream);
	}

	public NativeImage getSprite(int spriteIndex) {
		int startX = spriteIndex * singleWidth;
		int startY = 0;

		NativeImage result = new NativeImage(singleWidth, height, false);

		for (int x = 0; x < singleWidth; x++) {
			for (int y = 0; y < height; y++) {
				int pixelColor = this.image.getPixelRGBA(startX + x, startY + y);

				if (pixelColor == 0xFFFFFF00) {
					int transparentCyan = 0x00FFFF00;
					result.setPixelRGBA(x, y, transparentCyan);
				} else {
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
