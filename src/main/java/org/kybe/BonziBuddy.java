package org.kybe;

import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.text2speech.Narrator;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.resources.ResourceLocation;
import org.rusherhack.client.api.events.render.EventRender2D;
import org.rusherhack.client.api.feature.hud.ResizeableHudElement;
import org.rusherhack.client.api.render.RenderContext;
import org.rusherhack.client.api.utils.ChatUtils;
import org.rusherhack.core.event.stage.Stage;
import org.rusherhack.core.event.subscribe.Subscribe;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*
 * @author kybe236
 */
public class BonziBuddy extends ResizeableHudElement {
	private Map<String, Sprite> sprites;
	private Map<String, DynamicTexture> textures;
	private Map<String, ResourceLocation> locations;

	private Sprite currentSprite;
	private String currentSpriteName;
	private int currentSpriteIndex = 0;

	int x = 0;
	int y = 0;

	public BonziBuddy() {
		super("BonziBuddy");
		sprites = new HashMap<>();
		textures = new HashMap<>();
		locations = new HashMap<>();

		loadSprite("white", "/sprites/white.png", 64, 64, 64);
		loadSprite("apears", "/sprites/bonzi_apears.png", 5200, 160, 200);
		loadSprite("idle", "/sprites/idle.png", 200, 160, 200);
		loadSprite("read_book", "/sprites/bonzi_read_book.png", 2800, 160, 200);
		loadSprite("read_book_start", "/sprites/bonzi_read_book_start.png", 4400, 160, 200);
		loadSprite("coconut_start", "/sprites/bonzi_coconut_start.png", 1000, 160, 200);
		loadSprite("coconut", "/sprites/bonzi_coconut_loop.png", 2000, 160, 200);

		switchSpriteFile("apears");
	}

	/*
	 * @param name The name of the sprite used for future reference
	 * @param path The path to the sprite file in the resources folder
	 * @param width The width of the sprite
	 * @param height The height of the sprite
	 * @param singleWidth The width of a single sprite in the sprite file
	 */
	public void loadSprite(String name, String path, int width, int height, int singleWidth) {
		Sprite sprite = null;

		try {
			sprite = new Sprite(this.getClass().getResourceAsStream(path), width, height, singleWidth);
		} catch (IOException e) {
			ChatUtils.print("Failed to load sprite: " + name);
			this.getLogger().error("Failed to load sprite: " + name);
			e.printStackTrace();
		}

		if (sprite == null) {
			ChatUtils.print("Sprite " + name + " is null");
			this.getLogger().error("Sprite " + name + " is null");
			return;
		}
		NativeImage image = sprite.getSprite(0);

		DynamicTexture texture = new DynamicTexture(image);
		texture.upload();

		sprites.put(name, sprite);
		textures.put(name, texture);
		ResourceLocation location = mc.getTextureManager().register(name, texture);
		locations.put(name, location);
	}

	/*
	 * @param spriteFileName The name of the sprite to switch to
	 */
	public void switchSpriteFile(String spriteFileName) {
		if (sprites.containsKey(spriteFileName)) {
			currentSpriteName = spriteFileName;
			currentSprite = sprites.get(spriteFileName);
			currentSpriteIndex = 0;
		}
	}

	/*
	 * @param spriteIndex The index of the sprite to switch to (starting from 0)
	 */
	public void switchSpriteInFile(int spriteIndex) {
		if (currentSprite != null) {
			currentSpriteIndex = spriteIndex;
		}
	}

	@Override
	public double getWidth() {
		return 0;
	}

	@Override
	public double getHeight() {
		return 0;
	}

	/*
	 * This method needs to be overridden for compatibility with the RusherHack API
	 */
	@Override
	public void renderContent(RenderContext context, double mouseX, double ignore) {}

	// Target FPS for the animation
	int TARGET_FPS = 10;

	// Time Frames to achieve the target FPS
	private long lastUpdateTime = System.nanoTime();
	private final long updateInterval = 1_000_000_000 / TARGET_FPS;

	/*
	 * Called after the 2D rendering stage to render the sprite above the HUD
	 */
	@Subscribe(stage = Stage.POST, priority = 100)
	public void onRender2D(EventRender2D event) {
		try {
			// Calculate the elapsed time since the last update
			long currentTime = System.nanoTime();
			long elapsedTime = currentTime - lastUpdateTime;

			// Update the sprite if the elapsed time is greater than the update interval
			if (elapsedTime >= updateInterval) {
				updateLogics();
				lastUpdateTime = currentTime; // Update the last update time
			}

			if (currentSprite == null || currentSpriteName == null) {
				return;
			}

			// Get the current sprite and render it
			NativeImage image = currentSprite.getSprite(currentSpriteIndex);
			textures.get(currentSpriteName).setPixels(image);
			textures.get(currentSpriteName).upload();

			ResourceLocation spriteLocation = locations.get(currentSpriteName);
			GuiGraphics graphics = event.getGraphics();
			graphics.blit(spriteLocation, this.x, this.y, 0, 0, sprites.get(currentSpriteName).singleWidth, sprites.get(currentSpriteName).height, sprites.get(currentSpriteName).singleWidth, sprites.get(currentSpriteName).height);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	enum State {
		SETUP,
		IDLE,
		RANDOM
	}

	int randomLoopTimes = 1;
	int loppedTimes = 0;

	private boolean reverse = false;
	private boolean randomInit = false;

	private long currentTime = 0;
	private long randomTime = 0;
	State state = State.SETUP;

	boolean coordsLeaked = false;
	public void updateLogics() {
		if (currentSprite == null) {
			return;
		}


		this.currentTime += 1;
		ChatUtils.print("Current time: " + this.currentTime + " Random time: " + this.randomTime);
		if (this.currentTime >= this.randomTime) {
			this.randomTime = (long) (Math.random() * 50) + 50;
			this.currentTime = 0;
			if (state == State.IDLE) {
				this.state = State.RANDOM;
			}
		}

		switch (state) {
			case SETUP: {
				if (currentSpriteName.equals("apears")) {
					if (currentSpriteIndex < currentSprite.getSpriteCount()) {
						switchSpriteInFile(currentSpriteIndex + 1);
					}
					if (currentSpriteIndex == currentSprite.getSpriteCount()) {
						this.state = State.IDLE;
						ChatUtils.print("Hello! I am Bonzi Buddy!");
						Narrator.getNarrator().clear();
						Narrator.getNarrator().say("Hello! I am Bonzi Buddy!", false);
 					}
				} else {
					switchSpriteFile("apears");
				}
				break;
			}
			case IDLE: {
				if (!currentSpriteName.equals("idle")) {
					switchSpriteFile("idle");
				}
				break;
			}
			case RANDOM: {
				this.currentTime = 0;
				if (!randomInit) {
					randomInit = true;
					randomLoopTimes = (int) (Math.random() * 4) + 1;

					List<String> keys = List.of("read_book_start", "coconut_start");
					String randomKey = keys.get((int) (Math.random() * keys.size()));
					switchSpriteFile(randomKey);

					if (currentSpriteName.equals("read_book_start") && !coordsLeaked) {
						ChatUtils.print("Reading a book!");
						String msg = "Wanna hear a fun fact?";
						if (mc.player != null) {
							msg += "Your minecraft username is " + mc.player.getName().getString() + "!";
							msg += " YOURE COORDINATES ARE " + Math.round(mc.player.getX()) + " " + Math.round(mc.player.getY()) + " " + Math.round(mc.player.getZ()) + "!";
						} else {
							msg += "Join an server faster for an fun fact!";
						}
						ChatUtils.print(msg);
						Narrator.getNarrator().clear();
						Narrator.getNarrator().say(msg, true);
						coordsLeaked = true;
					}
				}
				if (currentSpriteIndex <= currentSprite.getSpriteCount()) {
					if (reverse) {
						switchSpriteInFile(currentSpriteIndex - 1);
					} else {
						switchSpriteInFile(currentSpriteIndex + 1);
					}
				}
				if (reverse) {
					if (currentSpriteIndex == 0) {
						this.state = State.IDLE;
						this.reverse = false;
						this.randomInit = false;
					}
				} else {
					if (currentSpriteIndex == currentSprite.getSpriteCount()) {
						// TODO add more special animations
						if (currentSpriteName.equals("read_book_start")) {
							switchSpriteFile("read_book");
						} else if (currentSpriteName.equals("coconut_start")) {
							switchSpriteFile("coconut");
						} else if (currentSpriteName.equals("read_book") && !reverse) {
							loppedTimes++;
							ChatUtils.print("target looop: " + randomLoopTimes + " current: " + loppedTimes);
							if (this.randomLoopTimes == loppedTimes) {
								switchSpriteFile("read_book_start");
								switchSpriteInFile(currentSprite.getSpriteCount());
								this.reverse = true;
								loppedTimes = 0;
							} else {
								switchSpriteInFile(0);
							}
						} else if (currentSpriteName.equals("coconut")) {
							loppedTimes++;
							ChatUtils.print("target looop: " + randomLoopTimes + " current: " + loppedTimes);
							if (this.randomLoopTimes == loppedTimes) {
								switchSpriteFile("coconut_start");
								switchSpriteInFile(currentSprite.getSpriteCount());
								this.reverse = true;
								loppedTimes = 0;
							} else {
								switchSpriteInFile(0);
							}
						} else {
							this.state = State.IDLE;
							this.reverse = false;
							this.randomInit = false;
						}
					}
				}
			}
		}
	}
}
