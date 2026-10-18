package org.example.astera.items;

import org.example.astera.AsteraPanel;
import org.lwjgl.glfw.GLFW;
import org.rusherhack.client.api.render.RenderContext;

import java.util.function.Supplier;

/**
 * Enum / mode row: left click next, right click previous (or open sub settings)
 */
public class OptionItem extends AsteraItem {

	private final String label;
	private final Supplier<String> display;
	private final Runnable next, previous;

	public OptionItem(AsteraPanel panel, AsteraItem parent, String label, Supplier<String> display, Runnable next, Runnable previous) {
		super(panel, parent);
		this.label = label;
		this.display = display;
		this.next = next;
		this.previous = previous;
	}

	@Override
	protected void renderRow(RenderContext context, double mouseX, double mouseY) {
		this.renderHover(mouseX, mouseY);
		this.drawLabel(this.label, this.theme().textColor.getValueRGB());
		this.drawValue(this.display.get(), this.theme().enabledTextColor.getValueRGB());
	}

	@Override
	protected String labelForSpacing() {
		return this.label;
	}

	@Override
	protected boolean onClick(double mouseX, double mouseY, int button) {
		if(button == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
			this.next.run();
			return true;
		}
		if(button == GLFW.GLFW_MOUSE_BUTTON_RIGHT && !this.hasVisibleChildren()) {
			this.previous.run();
			return true;
		}
		return super.onClick(mouseX, mouseY, button);
	}

}
