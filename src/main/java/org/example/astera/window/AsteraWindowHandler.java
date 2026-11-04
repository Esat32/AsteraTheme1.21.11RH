package org.example.astera.window;

import org.example.astera.AsteraPlugin;
import org.example.astera.AsteraTheme;
import org.rusherhack.client.api.feature.window.Window;
import org.rusherhack.client.api.render.IRenderer2D;
import org.rusherhack.client.api.render.font.IFontRenderer;
import org.rusherhack.client.api.ui.window.WindowContentHandlerBase;
import org.rusherhack.client.api.ui.window.WindowHandlerBase;
import org.rusherhack.client.api.ui.window.WindowViewHandlerBase;

/**
 * Astera look for windows: square dark frame, thin gradient title bar
 */
public class AsteraWindowHandler extends WindowHandlerBase {

	public static final int TITLE_HEIGHT = 12;
	public static final int BORDER = 2;

	private final WindowViewHandlerBase viewHandler = new WindowViewHandlerBase(this) {
	};
	private final AsteraWindowContentHandler contentHandler = new AsteraWindowContentHandler();

	@Override
	public WindowViewHandlerBase getViewHandler() {
		return this.viewHandler;
	}

	@Override
	public WindowContentHandlerBase getContentHandler() {
		return this.contentHandler;
	}

	@Override
	public void renderWindowFrame(Window window, double mouseX, double mouseY) {
		final AsteraTheme theme = AsteraPlugin.THEME;
		final IRenderer2D renderer = this.getRenderer();
		final IFontRenderer fr = this.getFontRenderer();
		final double x = window.getX();
		final double y = window.getY();
		final double w = window.getWidth();
		final double h = window.getHeight();

		//body and faint outline
		renderer.drawRectangle(x, y, w, h, theme.bodyFill());
		final int edge = AsteraTheme.withAlpha(theme.accent(), 90);
		renderer.drawRectangle(x, y + TITLE_HEIGHT, 1, h - TITLE_HEIGHT, edge);
		renderer.drawRectangle(x + w - 1, y + TITLE_HEIGHT, 1, h - TITLE_HEIGHT, edge);
		renderer.drawRectangle(x, y + h - 1, w, 1, edge);

		//title bar, dimmer while the window isn't focused
		theme.fillGradient(renderer, x, y, w, TITLE_HEIGHT, window.isFocused() ? 255 : 150);
		fr.drawString(window.getDisplayName(), x + 4, y + (TITLE_HEIGHT - fr.getFontHeight()) / 2 + 0.5, theme.headerTextColor.getValueRGB(), theme.textShadow.getValue());
	}

	@Override
	public int getFramePadding(WindowSide side) {
		return side == WindowSide.TOP ? TITLE_HEIGHT : BORDER;
	}

}
