package org.example.astera;

import org.example.astera.hud.AsteraArmorHud;
import org.example.astera.hud.AsteraDurabilityHud;
import org.rusherhack.client.api.RusherHackAPI;
import org.rusherhack.client.api.plugin.Plugin;

/**
 * Registers the Astera theme and its HUD elements
 *
 * @author Esat32
 */
public class AsteraPlugin extends Plugin {

	public static AsteraTheme THEME;

	@Override
	public void onLoad() {
		THEME = new AsteraTheme();
		RusherHackAPI.getThemeManager().registerTheme(THEME);
		RusherHackAPI.getHudManager().registerFeature(new AsteraDurabilityHud());
		RusherHackAPI.getHudManager().registerFeature(new AsteraArmorHud());
		this.getLogger().info("Astera theme loaded");
	}

	@Override
	public void onUnload() {
		this.getLogger().info("Astera theme unloaded");
	}

}
