package org.example.astera.items;

import org.example.astera.AsteraPanel;
import org.lwjgl.glfw.GLFW;
import org.rusherhack.client.api.render.IRenderer2D;
import org.rusherhack.client.api.render.RenderContext;

import java.util.function.DoubleConsumer;
import java.util.function.DoubleSupplier;
import java.util.function.Supplier;

/**
 * Number row: label and value on top, slim gradient bar with a square handle below, left drag to change
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
		this.renderHover(mouseX, mouseY);
		this.drawLabel(this.label, this.theme().textColor.getValueRGB());
		this.drawValue(this.display.get(), this.theme().valueColor.getValueRGB());

		final IRenderer2D renderer = this.getRenderer();
		final double clamped = Math.max(0, Math.min(1, fraction));
		final double filled = this.barWidth() * clamped;
		renderer.drawRectangle(this.barX(), this.barY(), this.barWidth(), BAR_HEIGHT, 0xFF2C2142);
		this.theme().fillGradient(renderer, this.barX(), this.barY(), filled, BAR_HEIGHT, this.barWidth(), 255);
		renderer.drawRectangle(this.barX() + filled - 2, this.barY() - 1.5, 4, BAR_HEIGHT + 3, 0xFFFFFFFF);
	}

	private static final double BAR_HEIGHT = 3;

	@Override
	protected double rowHeight() {
		return ROW + 6;
	}

	private double barX() {
		return this.textX();
	}

	private double barWidth() {
		return this.valueRight() - this.textX();
	}

	private double barY() {
		return this.getY() + ROW + 0.5;
	}

	@Override
	protected String labelForSpacing() {
		return this.label;
	}

	private void update(double mouseX) {
		final double fraction = Math.max(0, Math.min(1, (mouseX - this.barX()) / this.barWidth()));
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
