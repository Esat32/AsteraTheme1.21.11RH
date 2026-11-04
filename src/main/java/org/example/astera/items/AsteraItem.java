package org.example.astera.items;

import org.example.astera.AsteraPanel;
import org.example.astera.AsteraPlugin;
import org.example.astera.AsteraTheme;
import org.lwjgl.glfw.GLFW;
import org.rusherhack.client.api.render.IRenderer2D;
import org.rusherhack.client.api.render.RenderContext;
import org.rusherhack.client.api.render.font.IFontRenderer;
import org.rusherhack.client.api.ui.ElementBase;
import org.rusherhack.client.api.ui.panel.IPanelItem;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BooleanSupplier;

/**
 * One row in a panel. Children (settings) show below the row when open.
 */
public abstract class AsteraItem extends ElementBase implements IPanelItem {

	protected static final double ROW = AsteraPanel.ROW_HEIGHT;

	protected final AsteraPanel panel;
	protected final AsteraItem parent;
	protected final int depth;
	protected final List<AsteraItem> children = new ArrayList<>();
	protected boolean open = false;
	private BooleanSupplier visibility = () -> true;

	protected AsteraItem(AsteraPanel panel, AsteraItem parent) {
		this.panel = panel;
		this.parent = parent;
		this.depth = parent == null ? 0 : parent.depth + 1;
	}

	public AsteraItem setVisibility(BooleanSupplier visibility) {
		this.visibility = visibility;
		return this;
	}

	public void addChild(AsteraItem item) {
		if(item != null) {
			this.children.add(item);
		}
	}

	@Override
	public boolean isVisible() {
		return this.visibility.getAsBoolean();
	}

	protected boolean hasVisibleChildren() {
		for(AsteraItem child : this.children) {
			if(child.isVisible()) return true;
		}
		return false;
	}

	/*
	 * layout
	 */

	@Override
	public double getX() {
		return this.panel.getX() + AsteraPanel.OUTLINE;
	}

	@Override
	public double getWidth() {
		return this.panel.getWidth() - AsteraPanel.OUTLINE * 2;
	}

	// height of this item's own row, children not included
	protected double rowHeight() {
		return ROW;
	}

	@Override
	public double getHeight() {
		return this.rowHeight();
	}

	@Override
	public double getHeight(boolean total) {
		if(!total || !this.open) {
			return this.rowHeight();
		}
		double height = this.rowHeight();
		for(AsteraItem child : this.children) {
			if(child.isVisible()) height += child.getHeight(true);
		}
		return height;
	}

	/*
	 * rendering
	 */

	@Override
	public void render(RenderContext context, double mouseX, double mouseY) {
		this.renderRow(context, mouseX, mouseY);

		if(this.open) {
			//translucent gradient behind the opened settings
			final double childrenHeight = this.getHeight(true) - this.rowHeight();
			if(childrenHeight > 0) {
				this.theme().fillGradient(this.getRenderer(), this.getX() + 1, this.getY() + this.rowHeight(), this.getWidth() - 2, childrenHeight, this.theme().optionsAlpha.getValue());
			}

			double childY = this.getY() + this.rowHeight();
			for(AsteraItem child : this.children) {
				if(!child.isVisible()) continue;
				child.setY(childY);
				child.render(context, mouseX, mouseY);
				childY += child.getHeight(true);
			}
		}
	}

	protected abstract void renderRow(RenderContext context, double mouseX, double mouseY);

	protected AsteraTheme theme() {
		return AsteraPlugin.THEME;
	}

	@Override
	public IRenderer2D getRenderer() {
		return this.panel.getRenderer();
	}

	@Override
	public IFontRenderer getFontRenderer() {
		return this.panel.getFontRenderer();
	}

	// row fill inset by 1 unit, like Astera's enabled rows
	protected void fillRow(int color) {
		this.getRenderer().drawRectangle(this.getX() + 1, this.getY() + 1, this.getWidth() - 2, this.rowHeight() - 1, color);
	}

	// gradient row fill, used for enabled modules and toggles
	protected void fillRowGradient(int alpha) {
		this.theme().fillGradient(this.getRenderer(), this.getX() + 1, this.getY() + 1, this.getWidth() - 2, this.rowHeight() - 1, alpha);
		//bright edge on the left, marks enabled rows
		this.getRenderer().drawRectangle(this.getX() + 1, this.getY() + 1, 1, this.rowHeight() - 1, 0xFFFFFFFF);
	}

	protected void renderHover(double mouseX, double mouseY) {
		if(this.isRowHovered(mouseX, mouseY)) {
			this.fillRow(this.theme().hoverFill());
		}
	}

	protected double textX() {
		return this.getX() + 6 + this.depth * 4;
	}

	protected double textY() {
		return this.getY() + (ROW - this.getFontRenderer().getFontHeight()) / 2 + 0.5;
	}

	protected double valueRight() {
		return this.getX() + this.getWidth() - 5;
	}

	protected void drawLabel(String text, int color) {
		this.getFontRenderer().drawString(text, this.textX(), this.textY(), color, this.theme().textShadow.getValue());
	}

	protected void drawValue(String text, int color) {
		final IFontRenderer fr = this.getFontRenderer();
		final double maxWidth = this.valueRight() - this.textX() - fr.getStringWidth(this.labelForSpacing()) - 4;
		final String trimmed = fr.trimStringToWidth(text, Math.max(maxWidth, 0));
		fr.drawString(trimmed, this.valueRight() - fr.getStringWidth(trimmed), this.textY(), color, this.theme().textShadow.getValue());
	}

	// label width used to keep values from drawing over the label
	protected String labelForSpacing() {
		return "";
	}

	/*
	 * input
	 */

	public boolean isRowHovered(double mouseX, double mouseY) {
		return mouseX >= this.getX() && mouseX < this.getX() + this.getWidth() && mouseY >= this.getY() && mouseY < this.getY() + this.rowHeight();
	}

	@Override
	public boolean isHovered(double mouseX, double mouseY, boolean includeSubItems) {
		final double height = includeSubItems ? this.getHeight(true) : this.rowHeight();
		return mouseX >= this.getX() && mouseX < this.getX() + this.getWidth() && mouseY >= this.getY() && mouseY < this.getY() + height;
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		if(this.isRowHovered(mouseX, mouseY)) {
			return this.onClick(mouseX, mouseY, button);
		}
		if(this.open) {
			for(AsteraItem child : this.children) {
				if(child.isVisible() && child.mouseClicked(mouseX, mouseY, button)) {
					return true;
				}
			}
		}
		return false;
	}

	// default: right click opens settings
	protected boolean onClick(double mouseX, double mouseY, int button) {
		if(button == GLFW.GLFW_MOUSE_BUTTON_RIGHT && this.hasVisibleChildren()) {
			this.open = !this.open;
		}
		return true;
	}

	@Override
	public void mouseReleased(double mouseX, double mouseY, int button) {
		this.onRelease(mouseX, mouseY, button);
		for(AsteraItem child : this.children) {
			child.mouseReleased(mouseX, mouseY, button);
		}
	}

	protected void onRelease(double mouseX, double mouseY, int button) {
	}

	@Override
	public boolean charTyped(char character) {
		if(this.onChar(character)) {
			return true;
		}
		if(this.open) {
			for(AsteraItem child : this.children) {
				if(child.isVisible() && child.charTyped(character)) return true;
			}
		}
		return false;
	}

	protected boolean onChar(char character) {
		return false;
	}

	@Override
	public boolean keyTyped(int key, int scanCode, int modifiers) {
		if(this.onKey(key)) {
			return true;
		}
		if(this.open) {
			for(AsteraItem child : this.children) {
				if(child.isVisible() && child.keyTyped(key, scanCode, modifiers)) return true;
			}
		}
		return false;
	}

	protected boolean onKey(int key) {
		return false;
	}

}
