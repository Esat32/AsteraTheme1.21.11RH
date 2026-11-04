package org.example.astera;

import com.mojang.logging.LogUtils;
import net.minecraft.client.Minecraft;
import org.slf4j.Logger;
import com.mojang.blaze3d.platform.Window;
import org.rusherhack.client.api.RusherHackAPI;
import org.rusherhack.client.api.feature.hud.HudElement;
import org.rusherhack.client.api.setting.ColorSetting;
import org.rusherhack.core.setting.Setting;
import org.rusherhack.client.api.render.IRenderer2D;
import org.rusherhack.client.api.render.RenderContext;
import org.rusherhack.client.api.ui.hud.HudHandlerBase;
import org.rusherhack.client.api.ui.panel.PanelHandlerBase;

import java.awt.*;
import java.lang.reflect.Field;
import java.util.List;

/**
 * Astera look for HUD elements: translucent gradient background
 */
public class AsteraHudHandler extends HudHandlerBase {

	private static final Logger LOGGER = LogUtils.getLogger();

	private final AsteraHudManager manager = new AsteraHudManager();

	public AsteraHudHandler() {
		super(true);
	}

	// the sync and layout settings act as buttons: apply once, then switch themselves back off
	@Override
	public void tick() {
		super.tick();

		final AsteraTheme theme = AsteraPlugin.THEME;
		if(theme.layoutOnLoad.getValue() && !this.layoutApplied && Minecraft.getInstance().getWindow() != null) {
			this.layoutApplied = true;
			this.applyHudLayout();
			LOGGER.info("[Astera] applied preset HUD layout");
		}
		if(theme.syncHudColors.getValue()) {
			theme.syncHudColors.setValue(false);
			this.applyHudColors();
		}
		if(theme.applyHudLayout.getValue()) {
			theme.applyHudLayout.setValue(false);
			this.applyHudLayout();
		}
	}

	private void applyHudColors() {
		RusherHackAPI.colors().primaryColor().setValue(AsteraPlugin.THEME.getPrimaryColor());
		for(HudElement element : RusherHackAPI.getHudManager().getFeatures()) {
			syncColors(element.getSettings());
		}
	}

	private static void syncColors(List<Setting<?>> settings) {
		for(Setting<?> setting : settings) {
			if(setting instanceof ColorSetting color && color.isThemeSyncAllowed()) {
				color.setThemeSync(true);
			}
			syncColors(setting.getSubSettings());
		}
	}

	// the preset is applied once per game start
	private boolean layoutApplied = false;

	private static final int STACK = 14;

	/**
	 * Preset layout. Everything is in gui units, the bottom cluster is placed relative to the centered hotbar.
	 * Edit positions in the HUD editor afterwards.
	 */
	private void applyHudLayout() {
		final Window window = Minecraft.getInstance().getWindow();
		final double w = window.getGuiScaledWidth();
		final double h = window.getGuiScaledHeight();
		final HudElement.SnapPoint topLeft = HudElement.SnapPoint.TOP_LEFT;
		final HudElement.SnapPoint bottomLeft = HudElement.SnapPoint.BOTTOM_LEFT;

		//top left, stacked
		this.place("Weather", topLeft, 0, 0);
		this.place("FPS", topLeft, 0, STACK);

		//top right
		this.place("FeatureList", HudElement.SnapPoint.TOP_RIGHT, w, 0);

		//bottom left, stacked upwards
		this.place("Position", bottomLeft, 0, h);
		this.place("Biome", bottomLeft, 0, h - STACK);

		//around the hotbar, which is 182 gui units wide and centered
		final double center = w / 2;
		this.place("PlayerView", bottomLeft, center - 142, h);
		//armor on the right half above the hunger bar
		this.place("AsteraArmor", bottomLeft, center + 11, h - 40);
		//durability and speed on the left above minecraft's own armor bar, which sits over the health bar
		this.place("AsteraDurability", bottomLeft, center - 91, h - 62);
		this.place("Speed", bottomLeft, center - 91, h - 62 - STACK);
		//the Astera versions show a % sign in percent mode, so they replace the built in ones
		this.disable("Durability");
		this.disable("Armor");
		//saved settings override the elements' defaults, so make their text follow the theme color here
		for(HudElement element : RusherHackAPI.getHudManager().getFeatures()) {
			if(element.getName().equals("AsteraDurability") || element.getName().equals("AsteraArmor")) {
				syncColors(element.getSettings());
			}
		}
		this.place("Inventory", bottomLeft, center + 91 + 4, h);
	}

	/**
	 * An element keeps its alignment in a separate field that starts as top left. If it differs from the one the
	 * snap point gives, the element shifts itself by its own height on the next render, which pushed bottom
	 * aligned elements off screen. There is no setter, so the field is set directly.
	 */
	private static void syncAlignment(HudElement element) {
		try {
			final Field field = HudElement.class.getDeclaredField("alignment");
			field.setAccessible(true);
			field.set(element, HudElement.calculateAlignment(element));
		} catch(ReflectiveOperationException | RuntimeException e) {
			LOGGER.warn("[Astera] could not set alignment of {}", element.getName(), e);
		}
	}

	private void disable(String name) {
		for(HudElement element : RusherHackAPI.getHudManager().getFeatures()) {
			if(element.getName().equalsIgnoreCase(name)) {
				element.setToggled(false);
				return;
			}
		}
	}

	private void place(String name, HudElement.SnapPoint snap, double x, double y) {
		for(HudElement element : RusherHackAPI.getHudManager().getFeatures()) {
			if(element.getName().equalsIgnoreCase(name)) {
				element.setSnapPoint(snap);
				element.setX(x);
				element.setY(y);
				syncAlignment(element);
				element.setToggled(true);
				return;
			}
		}
	}

	@Override
	protected Color getBackgroundColor(HudElement hudElement, double mouseX, double mouseY) {
		return new Color(AsteraPlugin.THEME.bodyFill(), true);
	}

	@Override
	protected void renderHudElementBackground(HudElement hudElement, RenderContext renderContext, IRenderer2D renderer, double width, double height, int color) {
		AsteraPlugin.THEME.fillGradient(renderer, 0, 0, width, height, AsteraPlugin.THEME.hudOpacity.getValue());
	}

	/**
	 * The HUD editor's element list, drawn in the Astera style
	 */
	@Override
	public PanelHandlerBase<?> getHudManagerPanel() {
		return this.manager;
	}

}
