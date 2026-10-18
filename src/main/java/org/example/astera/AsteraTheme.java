package org.example.astera;

import org.jetbrains.annotations.Nullable;
import org.rusherhack.client.api.setting.ColorSetting;
import org.rusherhack.client.api.ui.panel.PanelHandlerBase;
import org.rusherhack.client.api.ui.theme.ThemeBase;
import org.rusherhack.core.setting.BooleanSetting;
import org.rusherhack.core.setting.NumberSetting;

import java.awt.*;

/**
 * Astera client look: periwinkle header and outline, tinted body, filled rows for enabled modules.
 * Default values were sampled from an Astera screenshot.
 */
public class AsteraTheme extends ThemeBase {

	public final NumberSetting<Integer> bodyAlpha = new NumberSetting<>("BodyAlpha", "Opacity of the panel body tint", 64, 0, 255);
	public final NumberSetting<Integer> enabledAlpha = new NumberSetting<>("EnabledAlpha", "Opacity of enabled module rows", 165, 0, 255);
	public final NumberSetting<Integer> hoverAlpha = new NumberSetting<>("HoverAlpha", "Opacity of the hover overlay", 18, 0, 255);
	public final ColorSetting textColor = new ColorSetting("TextColor", new Color(236, 240, 252)).setThemeSync(false);
	public final ColorSetting enabledTextColor = new ColorSetting("EnabledTextColor", new Color(128, 145, 212)).setThemeSync(false);
	public final ColorSetting headerTextColor = new ColorSetting("HeaderTextColor", new Color(251, 255, 255)).setThemeSync(false);
	public final ColorSetting valueColor = new ColorSetting("ValueColor", new Color(170, 178, 205)).setThemeSync(false);
	public final BooleanSetting asteraNames = new BooleanSetting("AsteraNames", "Rename categories to Misc, Visual and Core", true);
	public final NumberSetting<Float> scrollSpeed = new NumberSetting<>("ScrollSpeed", 15f, 1f, 40f);
	public final NumberSetting<Float> scale = new NumberSetting<>("Scale", "Size of the whole ClickGUI", 1f, 0.5f, 2f).incremental(0.05);
	public final BooleanSetting textShadow = new BooleanSetting("TextShadow", "Astera draws text without shadow", false);

	private final AsteraClickGui clickGui;

	public AsteraTheme() {
		super("Astera", "Astera client styled ClickGUI", new Color(82, 102, 161));
		this.registerSettings(this.scale, this.textShadow, this.bodyAlpha, this.enabledAlpha, this.hoverAlpha, this.textColor, this.enabledTextColor, this.headerTextColor, this.valueColor, this.asteraNames, this.scrollSpeed);
		this.clickGui = new AsteraClickGui();
	}

	@Override
	public @Nullable PanelHandlerBase<?> getClickGuiHandler() {
		return this.clickGui;
	}

	public int accent() {
		return this.getColorSetting().getValueRGB();
	}

	public int bodyFill() {
		return withAlpha(this.accent(), this.bodyAlpha.getValue());
	}

	public int enabledFill() {
		return withAlpha(this.accent(), this.enabledAlpha.getValue());
	}

	public int hoverFill() {
		return withAlpha(0xFFFFFF, this.hoverAlpha.getValue());
	}

	public static int withAlpha(int rgb, int alpha) {
		return (rgb & 0xFFFFFF) | ((alpha & 0xFF) << 24);
	}

}
