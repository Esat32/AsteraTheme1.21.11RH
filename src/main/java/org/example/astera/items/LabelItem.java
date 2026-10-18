package org.example.astera.items;

import org.example.astera.AsteraPanel;
import org.lwjgl.glfw.GLFW;
import org.rusherhack.client.api.render.RenderContext;

import java.util.function.Supplier;

/**
 * Group header (NullSetting) or read-only fallback for settings without an editor
 */
public class LabelItem extends AsteraItem {

	private final String label;
	private final Supplier<String> display;

	public LabelItem(AsteraPanel panel, AsteraItem parent, String label, Supplier<String> display) {
		super(panel, parent);
		this.label = label;
		this.display = display;
	}

	@Override
	protected void renderRow(RenderContext context, double mouseX, double mouseY) {
		this.renderHover(mouseX, mouseY);
		this.drawLabel(this.label, this.open ? this.theme().enabledTextColor.getValueRGB() : this.theme().textColor.getValueRGB());
		if(this.display != null) {
			this.drawValue(this.display.get(), this.theme().valueColor.getValueRGB());
		}
	}

	@Override
	protected String labelForSpacing() {
		return this.label;
	}

	@Override
	protected boolean onClick(double mouseX, double mouseY, int button) {
		if(button == GLFW.GLFW_MOUSE_BUTTON_LEFT && this.hasVisibleChildren()) {
			this.open = !this.open;
			return true;
		}
		return super.onClick(mouseX, mouseY, button);
	}

}
