package org.example.astera;

import org.rusherhack.client.api.RusherHackAPI;
import org.rusherhack.client.api.plugin.Plugin;

/**
 * Registers the Astera ClickGUI theme
 *
 * @author Esat32
 */
public class AsteraPlugin extends Plugin {

	public static AsteraTheme THEME;

	@Override
	public void onLoad() {
		THEME = new AsteraTheme();
		RusherHackAPI.getThemeManager().registerTheme(THEME);
		this.getLogger().info("Astera theme loaded");
	}

	@Override
	public void onUnload() {
		this.getLogger().info("Astera theme unloaded");
	}

}
