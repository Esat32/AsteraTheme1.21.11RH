package org.example.astera;

import org.jetbrains.annotations.Nullable;
import org.rusherhack.client.api.render.IRenderer2D;
import org.rusherhack.client.api.setting.ColorSetting;
import org.example.astera.window.AsteraWindowHandler;
import org.rusherhack.client.api.ui.hud.HudHandlerBase;
import org.rusherhack.client.api.ui.panel.PanelHandlerBase;
import org.rusherhack.client.api.ui.theme.ThemeBase;
import org.rusherhack.client.api.ui.window.WindowHandlerBase;
import org.rusherhack.core.setting.BooleanSetting;
import org.rusherhack.core.setting.NumberSetting;

import java.awt.*;

/**
 * Astera client look: square panels, thin cyan to magenta gradient headers, dark body,
 * gradient filled rows for enabled modules and gradient switches and sliders.
 */
public class AsteraTheme extends ThemeBase {

	public final ColorSetting gradientStart = new ColorSetting("GradientStart", new Color(0x40, 0xC9, 0xFF)).setThemeSync(false);
	public final ColorSetting gradientEnd = new ColorSetting("GradientEnd", new Color(0xE8, 0x1C, 0xFF)).setThemeSync(false);
	public final ColorSetting bodyColor = new ColorSetting("BodyColor", new Color(0x15, 0x0F, 0x26)).setThemeSync(false);
	public final NumberSetting<Integer> bodyAlpha = new NumberSetting<>("BodyOpacity", "Opacity of the panel body, lower is more see-through", 150, 0, 255);
	public final NumberSetting<Integer> enabledAlpha = new NumberSetting<>("EnabledAlpha", "Opacity of enabled module rows", 128, 0, 255);
	public final NumberSetting<Integer> optionsAlpha = new NumberSetting<>("OptionsAlpha", "Opacity of the gradient behind opened settings", 56, 0, 255);
	public final NumberSetting<Integer> hoverAlpha = new NumberSetting<>("HoverAlpha", "Opacity of the hover overlay", 18, 0, 255);
	public final ColorSetting textColor = new ColorSetting("TextColor", new Color(211, 200, 234)).setThemeSync(false);
	public final ColorSetting enabledTextColor = new ColorSetting("EnabledTextColor", new Color(255, 255, 255)).setThemeSync(false);
	public final ColorSetting headerTextColor = new ColorSetting("HeaderTextColor", new Color(255, 255, 255)).setThemeSync(false);
	public final ColorSetting valueColor = new ColorSetting("ValueColor", new Color(169, 159, 196)).setThemeSync(false);
	public final BooleanSetting asteraNames = new BooleanSetting("AsteraNames", "Rename categories to Misc, Visual and Core", false);
	public final NumberSetting<Float> scrollSpeed = new NumberSetting<>("ScrollSpeed", 15f, 1f, 40f);
	public final NumberSetting<Float> scale = new NumberSetting<>("Scale", "Size of the whole ClickGUI", 1f, 0.5f, 2f).incremental(0.05);
	public final BooleanSetting autoFit = new BooleanSetting("AutoFit", "Shrinks the ClickGUI so every panel fits the window width", true);
	public final BooleanSetting syncHudColors = new BooleanSetting("SyncHudColors", "Switch on to apply once: HUD element colors follow the theme and the client primary color becomes the theme color. Switches itself back off.", false);
	public final BooleanSetting applyHudLayout = new BooleanSetting("ApplyHudLayout", "Switch on to apply once: enables the Astera preset HUD elements and moves them to their preset positions. Switches itself back off.", false);
	public final BooleanSetting backgroundBlur = new BooleanSetting("BackgroundBlur", "Blurs the screen behind the ClickGUI. Strength follows Minecraft's menu background blur video setting.", true);
	public final NumberSetting<Integer> layoutVersion = new NumberSetting<>("LayoutVersion", "Version of the preset HUD layout that was applied. The preset is applied again on start when it is newer than this. Set to 0 to apply it again.", 0, 0, 100);
	public final BooleanSetting textShadow = new BooleanSetting("TextShadow", "Astera draws text without shadow", false);

	private final AsteraClickGui clickGui;
	private final AsteraHudHandler hudHandler;
	private final AsteraWindowHandler windowHandler;

	public AsteraTheme() {
		super("Astera", "Astera client styled ClickGUI", new Color(0x8E, 0x74, 0xFF));
		this.registerSettings(this.scale, this.autoFit, this.backgroundBlur, this.syncHudColors, this.applyHudLayout, this.layoutVersion, this.textShadow, this.gradientStart, this.gradientEnd, this.bodyColor, this.bodyAlpha, this.enabledAlpha, this.optionsAlpha, this.hoverAlpha, this.textColor, this.enabledTextColor, this.headerTextColor, this.valueColor, this.asteraNames, this.scrollSpeed);
		this.clickGui = new AsteraClickGui();
		this.hudHandler = new AsteraHudHandler();
		this.windowHandler = new AsteraWindowHandler();
	}

	@Override
	public @Nullable PanelHandlerBase<?> getClickGuiHandler() {
		return this.clickGui;
	}

	@Override
	public @Nullable HudHandlerBase getHudHandler() {
		return this.hudHandler;
	}

	@Override
	public @Nullable WindowHandlerBase getWindowHandler() {
		return this.windowHandler;
	}

	public int accent() {
		return this.getColorSetting().getValueRGB();
	}

	public int bodyFill() {
		return withAlpha(this.bodyColor.getValueRGB(), this.bodyAlpha.getValue());
	}

	public int enabledFill() {
		return withAlpha(this.accent(), this.enabledAlpha.getValue());
	}

	/**
	 * Fills a rectangle with the left to right gradient, optionally cut to the first part of its width.
	 * Gradient colors stay tied to the full width so a slider fill shows the matching part of the gradient.
	 */
	public void fillGradient(IRenderer2D renderer, double x, double y, double width, double height, int alpha) {
		this.fillGradient(renderer, x, y, width, height, width, alpha);
	}

	public void fillGradient(IRenderer2D renderer, double x, double y, double visibleWidth, double height, double fullWidth, int alpha) {
		if(visibleWidth <= 0 || height <= 0) return;
		final int start = this.gradientStart.getValueRGB();
		final int end = this.gradientEnd.getValueRGB();
		final double t = Math.min(1, visibleWidth / fullWidth);
		renderer.drawGradientRectangle(x, y, x + visibleWidth, y, visibleWidth, height, withAlpha(start, alpha), withAlpha(lerp(start, end, t), alpha));
	}

	private static int lerp(int a, int b, double t) {
		final int r = (int) (((a >> 16) & 0xFF) + ((((b >> 16) & 0xFF) - ((a >> 16) & 0xFF)) * t));
		final int g = (int) (((a >> 8) & 0xFF) + ((((b >> 8) & 0xFF) - ((a >> 8) & 0xFF)) * t));
		final int bl = (int) ((a & 0xFF) + (((b & 0xFF) - (a & 0xFF)) * t));
		return (r << 16) | (g << 8) | bl;
	}

	public int hoverFill() {
		return withAlpha(0xFFFFFF, this.hoverAlpha.getValue());
	}

	public static int withAlpha(int rgb, int alpha) {
		return (rgb & 0xFFFFFF) | ((alpha & 0xFF) << 24);
	}

}
