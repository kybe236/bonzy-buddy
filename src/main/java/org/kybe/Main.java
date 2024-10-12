package org.kybe;

import org.rusherhack.client.api.RusherHackAPI;
import org.rusherhack.client.api.events.client.chat.EventAddChat;
import org.rusherhack.client.api.plugin.Plugin;
import org.rusherhack.client.api.utils.ChatUtils;
import org.rusherhack.core.event.subscribe.Subscribe;

import java.io.IOException;

/**
 * @author kybe236
 */
public class Main extends Plugin {
	BonziBuddy bonziBuddy = new BonziBuddy();

	@Override
	public void onLoad() {
		RusherHackAPI.getHudManager().registerFeature(bonziBuddy);
	}
	
	@Override
	public void onUnload() {
		this.getLogger().info("Example plugin unloaded!");
	}
}