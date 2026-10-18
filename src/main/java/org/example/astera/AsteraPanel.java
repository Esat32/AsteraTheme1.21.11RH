package org.example.astera;

import org.example.astera.items.AsteraItem;
import org.example.astera.items.ModuleItem;
import org.lwjgl.glfw.GLFW;
import org.rusherhack.client.api.feature.module.IModule;
import org.rusherhack.client.api.feature.module.ModuleCategory;
import org.rusherhack.client.api.render.IRenderer2D;
import org.rusherhack.client.api.render.RenderContext;
import org.rusherhack.client.api.render.font.IFontRenderer;
import org.rusherhack.client.api.ui.panel.PanelBase;
import org.rusherhack.core.feature.IFeature;

/**
 * Category panel. Left drag header to move, right click header to collapse.
 */
public class AsteraPanel extends PanelBase<AsteraItem> {

	// sizes in gui units, measured from the Astera screenshot at gui scale 2
	public static final double WIDTH = 112;
	public static final double HEADER_HEIGHT = 16;
	public static final double ROW_HEIGHT = 15;
	public static final double OUTLINE = 1;
	public static final double GAP = 2;
	public static final double MARGIN = 3;

	private ModuleCategory category;
	private boolean open = true;
	private boolean dragging = false;
	private double dragX, dragY;

	public AsteraPanel(AsteraClickGui handler, String name) {
		super(handler, name);
	}

	public void setCategory(ModuleCategory category) {
		this.category = category;
	}

	@Override
	public AsteraItem createFeatureItem(IFeature feature) {
		if(feature instanceof IModule module) {
			return new ModuleItem(this, module);
		}
		return null;
	}

	@Override
	public void render(RenderContext context, double mouseX, double mouseY) {
		if(this.dragging) {
			this.setX(mouseX - this.dragX);
			this.setY(mouseY - this.dragY);
		}

		final AsteraTheme theme = AsteraPlugin.THEME;
		final IRenderer2D renderer = this.getRenderer();
		final IFontRenderer fr = this.getFontRenderer();
		final double x = this.getX();
		final double y = this.getY();
		final int accent = theme.accent();

		//header
		renderer.drawRectangle(x, y, WIDTH, HEADER_HEIGHT, accent);
		fr.drawString(this.getTitle(), x + 5, y + (HEADER_HEIGHT - fr.getFontHeight()) / 2 + 0.5, theme.headerTextColor.getValueRGB(), theme.textShadow.getValue());

		if(!this.open) {
			return;
		}

		final double bodyY = y + HEADER_HEIGHT;
		final double bodyHeight = this.getContentHeight();

		//body tint and outline
		renderer.drawRectangle(x + OUTLINE, bodyY, WIDTH - OUTLINE * 2, bodyHeight, theme.bodyFill());
		renderer.drawRectangle(x, bodyY, OUTLINE, bodyHeight + OUTLINE, accent);
		renderer.drawRectangle(x + WIDTH - OUTLINE, bodyY, OUTLINE, bodyHeight + OUTLINE, accent);
		renderer.drawRectangle(x + OUTLINE, bodyY + bodyHeight, WIDTH - OUTLINE * 2, OUTLINE, accent);

		double itemY = bodyY;
		for(AsteraItem item : this.items) {
			item.setY(itemY);
			item.render(context, mouseX, mouseY);
			itemY += item.getHeight(true);
		}
	}

	private String getTitle() {
		if(this.category == null) {
			return this.getDisplayName();
		}
		if(AsteraPlugin.THEME.asteraNames.getValue()) {
			if(this.category == ModuleCategory.MISC) return "Misc";
			if(this.category == ModuleCategory.RENDER) return "Visual";
			if(this.category == ModuleCategory.CLIENT) return "Core";
		}
		return this.category.getDisplayName();
	}

	private double getContentHeight() {
		double height = 0;
		for(AsteraItem item : this.items) {
			height += item.getHeight(true);
		}
		return height;
	}

	@Override
	public double getWidth() {
		return WIDTH;
	}

	@Override
	public double getHeight() {
		return this.open ? HEADER_HEIGHT + this.getContentHeight() + OUTLINE : HEADER_HEIGHT;
	}

	private boolean isHeaderHovered(double mouseX, double mouseY) {
		return mouseX >= this.getX() && mouseX < this.getX() + WIDTH && mouseY >= this.getY() && mouseY < this.getY() + HEADER_HEIGHT;
	}

	@Override
	public boolean isHovered(double mouseX, double mouseY) {
		return mouseX >= this.getX() && mouseX < this.getX() + WIDTH && mouseY >= this.getY() && mouseY < this.getY() + this.getHeight();
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		if(this.isHeaderHovered(mouseX, mouseY)) {
			if(button == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
				this.dragging = true;
				this.dragX = mouseX - this.getX();
				this.dragY = mouseY - this.getY();
			} else if(button == GLFW.GLFW_MOUSE_BUTTON_RIGHT) {
				this.open = !this.open;
			}
			return true;
		}

		if(this.open) {
			for(AsteraItem item : this.items) {
				if(item.mouseClicked(mouseX, mouseY, button)) {
					return true;
				}
			}
		}

		//swallow clicks on empty panel space so panels underneath don't get them
		return this.isHovered(mouseX, mouseY);
	}

	@Override
	public void mouseReleased(double mouseX, double mouseY, int button) {
		if(button == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
			this.dragging = false;
		}
		for(AsteraItem item : this.items) {
			item.mouseReleased(mouseX, mouseY, button);
		}
	}

	// returns false so every panel scrolls together
	@Override
	public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
		this.setY(this.getY() + Math.signum(delta) * AsteraPlugin.THEME.scrollSpeed.getValue());
		return false;
	}

	@Override
	public boolean charTyped(char character) {
		if(!this.open) return false;
		for(AsteraItem item : this.items) {
			if(item.charTyped(character)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean keyTyped(int key, int scanCode, int modifiers) {
		if(!this.open) return false;
		for(AsteraItem item : this.items) {
			if(item.keyTyped(key, scanCode, modifiers)) {
				return true;
			}
		}
		return false;
	}

}
