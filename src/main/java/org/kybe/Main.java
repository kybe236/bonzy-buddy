package org.kybe;

import org.rusherhack.client.api.RusherHackAPI;
import org.rusherhack.client.api.plugin.Plugin;

import java.io.IOException;

/**
 * @author kybe236
 */
public class Main extends Plugin {
	
	@Override
	public void onLoad() {
		final BonziBuddy bonziBuddy;
		bonziBuddy = new BonziBuddy();
		RusherHackAPI.getHudManager().registerFeature(bonziBuddy);
	}
	
	@Override
	public void onUnload() {
		this.getLogger().info("Example plugin unloaded!");
	}
	
}