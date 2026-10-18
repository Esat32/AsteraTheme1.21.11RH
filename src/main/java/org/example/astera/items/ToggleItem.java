package org.example.astera.items;

import org.example.astera.AsteraPanel;
import org.lwjgl.glfw.GLFW;
import org.rusherhack.client.api.render.RenderContext;

import java.util.function.BooleanSupplier;
import java.util.function.Consumer;

/**
 * Boolean row, drawn like an enabled module when on
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
		if(on) {
			this.fillRow(this.theme().enabledFill());
		}
		this.renderHover(mouseX, mouseY);
		this.drawLabel(this.label, on ? this.theme().enabledTextColor.getValueRGB() : this.theme().textColor.getValueRGB());
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
