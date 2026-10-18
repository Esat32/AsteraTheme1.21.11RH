package org.example.astera.items;

import org.example.astera.AsteraPanel;
import org.lwjgl.glfw.GLFW;
import org.rusherhack.client.api.render.RenderContext;

import java.util.function.DoubleConsumer;
import java.util.function.DoubleSupplier;
import java.util.function.Supplier;

/**
 * Number row: fill shows the value, left drag to change
 */
public class SliderItem extends AsteraItem {

	private static SliderItem active = null;

	private final String label;
	private final double min, max;
	private final DoubleSupplier getter;
	private final DoubleConsumer setter;
	private final Supplier<String> display;
	private boolean dragging = false;

	public SliderItem(AsteraPanel panel, AsteraItem parent, String label, double min, double max, DoubleSupplier getter, DoubleConsumer setter, Supplier<String> display) {
		super(panel, parent);
		this.label = label;
		this.min = min;
		this.max = max;
		this.getter = getter;
		this.setter = setter;
		this.display = display;
	}

	@Override
	protected void renderRow(RenderContext context, double mouseX, double mouseY) {
		if(this.dragging) {
			this.update(mouseX);
		}

		final double range = this.max - this.min;
		final double fraction = range == 0 ? 0 : (this.getter.getAsDouble() - this.min) / range;
		this.fillRow(fraction, this.theme().enabledFill());
		this.renderHover(mouseX, mouseY);
		this.drawLabel(this.label, this.theme().textColor.getValueRGB());
		this.drawValue(this.display.get(), this.theme().valueColor.getValueRGB());
	}

	@Override
	protected String labelForSpacing() {
		return this.label;
	}

	private void update(double mouseX) {
		final double left = this.getX() + 1;
		final double width = this.getWidth() - 2;
		final double fraction = Math.max(0, Math.min(1, (mouseX - left) / width));
		this.setter.accept(this.min + (this.max - this.min) * fraction);
	}

	@Override
	protected boolean onClick(double mouseX, double mouseY, int button) {
		if(button == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
			this.dragging = true;
			active = this;
			this.update(mouseX);
			return true;
		}
		return super.onClick(mouseX, mouseY, button);
	}

	@Override
	protected void onRelease(double mouseX, double mouseY, int button) {
		if(button == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
			this.dragging = false;
			if(active == this) active = null;
		}
	}

	public static boolean isAnyDragging() {
		return active != null;
	}

}
