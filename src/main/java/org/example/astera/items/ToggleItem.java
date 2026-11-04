package org.example.astera.items;

import org.example.astera.AsteraPanel;
import org.lwjgl.glfw.GLFW;
import org.rusherhack.client.api.render.IRenderer2D;
import org.rusherhack.client.api.render.RenderContext;

import java.util.function.BooleanSupplier;
import java.util.function.Consumer;

/**
 * Boolean row with a square switch on the right, filled with the gradient when on
 */
public class ToggleItem extends AsteraItem {

	private final String label;
	private final BooleanSupplier getter;
	private final Consumer<Boolean> setter;

	public ToggleItem(AsteraPanel panel, AsteraItem parent, String label, BooleanSupplier getter, Consumer<Boolean> setter) {
		super(panel, parent);
		this.label = label;
		this.getter = getter;
		this.setter = setter;
	}

	@Override
	protected void renderRow(RenderContext context, double mouseX, double mouseY) {
		final boolean on = this.getter.getAsBoolean();
		this.renderHover(mouseX, mouseY);
		this.drawLabel(this.label, this.theme().textColor.getValueRGB());

		final IRenderer2D renderer = this.getRenderer();
		final double w = 14, h = 7;
		final double x = this.valueRight() - w;
		final double y = this.getY() + (ROW - h) / 2;
		if(on) {
			this.theme().fillGradient(renderer, x, y, w, h, 255);
		} else {
			renderer.drawRectangle(x, y, w, h, 0xFF35294D);
		}
		final double knob = h - 2;
		renderer.drawRectangle(on ? x + w - knob - 1 : x + 1, y + 1, knob, knob, on ? 0xFFFFFFFF : 0xFF8C80AA);
	}

	@Override
	protected boolean onClick(double mouseX, double mouseY, int button) {
		if(button == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
			this.setter.accept(!this.getter.getAsBoolean());
			return true;
		}
		return super.onClick(mouseX, mouseY, button);
	}

}
