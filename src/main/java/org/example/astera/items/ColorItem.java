package org.example.astera.items;

import org.example.astera.AsteraPanel;
import org.lwjgl.glfw.GLFW;
import org.rusherhack.client.api.render.IRenderer2D;
import org.rusherhack.client.api.render.RenderContext;
import org.rusherhack.client.api.setting.ColorSetting;

/**
 * Color row with a swatch. Click to open RGBA sliders, rainbow and theme sync.
 */
public class ColorItem extends AsteraItem {

	private final String label;
	private final ColorSetting setting;

	public ColorItem(AsteraPanel panel, AsteraItem parent, String label, ColorSetting setting) {
		super(panel, parent);
		this.label = label;
		this.setting = setting;

		this.addChild(channel("Red", setting::getRed, setting::setRed));
		this.addChild(channel("Green", setting::getGreen, setting::setGreen));
		this.addChild(channel("Blue", setting::getBlue, setting::setBlue));
		if(setting.isAlphaAllowed()) {
			this.addChild(channel("Alpha", setting::getAlpha, setting::setAlpha));
		}
		if(setting.isRainbowAllowed()) {
			this.addChild(new ToggleItem(panel, this, "Rainbow", setting::isRainbow, setting::setRainbow));
		}
		if(setting.isThemeSyncAllowed()) {
			this.addChild(new ToggleItem(panel, this, "Sync", setting::isThemeSync, setting::setThemeSync));
		}
	}

	private SliderItem channel(String name, java.util.function.IntSupplier getter, java.util.function.IntConsumer setter) {
		return new SliderItem(this.panel, this, name, 0, 255,
				getter::getAsInt, v -> setter.accept((int) Math.round(v)), () -> String.valueOf(getter.getAsInt()));
	}

	@Override
	protected void renderRow(RenderContext context, double mouseX, double mouseY) {
		this.renderHover(mouseX, mouseY);
		this.drawLabel(this.label, this.theme().textColor.getValueRGB());

		final IRenderer2D renderer = this.getRenderer();
		final double size = 7;
		final double swatchX = this.valueRight() - size * 1.5;
		final double swatchY = this.getY() + (ROW - size) / 2;
		renderer.drawRectangle(swatchX - 0.5, swatchY - 0.5, size * 1.5 + 1, size + 1, 0xFF000000);
		renderer.drawRectangle(swatchX, swatchY, size * 1.5, size, this.setting.getValueRGB() | 0xFF000000);
	}

	@Override
	protected boolean onClick(double mouseX, double mouseY, int button) {
		if(button == GLFW.GLFW_MOUSE_BUTTON_LEFT || button == GLFW.GLFW_MOUSE_BUTTON_RIGHT) {
			this.open = !this.open;
			return true;
		}
		return super.onClick(mouseX, mouseY, button);
	}

}
