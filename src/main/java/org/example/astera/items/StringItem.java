package org.example.astera.items;

import org.example.astera.AsteraPanel;
import org.lwjgl.glfw.GLFW;
import org.rusherhack.client.api.render.RenderContext;

import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Text row: left click to edit, Enter saves, Esc cancels
 */
public class StringItem extends AsteraItem {

	private final String label;
	private final Supplier<String> getter;
	private final Consumer<String> setter;
	private StringBuilder buffer = null;

	public StringItem(AsteraPanel panel, AsteraItem parent, String label, Supplier<String> getter, Consumer<String> setter) {
		super(panel, parent);
		this.label = label;
		this.getter = getter;
		this.setter = setter;
	}

	@Override
	protected void renderRow(RenderContext context, double mouseX, double mouseY) {
		final boolean editing = this.buffer != null;
		if(editing) {
			this.fillRow(this.theme().enabledFill());
		}
		this.renderHover(mouseX, mouseY);
		this.drawLabel(this.label, this.theme().textColor.getValueRGB());

		final String value = editing
				? this.buffer + (System.currentTimeMillis() % 1000 < 500 ? "_" : " ")
				: this.getter.get();
		this.drawValue(value, this.theme().valueColor.getValueRGB());
	}

	@Override
	protected String labelForSpacing() {
		return this.label;
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		//clicking anywhere else saves the edit
		if(this.buffer != null && !this.isRowHovered(mouseX, mouseY)) {
			this.commit();
		}
		return super.mouseClicked(mouseX, mouseY, button);
	}

	@Override
	protected boolean onClick(double mouseX, double mouseY, int button) {
		if(button == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
			if(this.buffer == null) {
				this.buffer = new StringBuilder(this.getter.get());
			} else {
				this.commit();
			}
			return true;
		}
		return super.onClick(mouseX, mouseY, button);
	}

	private void commit() {
		this.setter.accept(this.buffer.toString());
		this.buffer = null;
	}

	@Override
	protected boolean onChar(char character) {
		if(this.buffer == null) {
			return false;
		}
		this.buffer.append(character);
		return true;
	}

	@Override
	protected boolean onKey(int key) {
		if(this.buffer == null) {
			return false;
		}
		switch(key) {
			case GLFW.GLFW_KEY_ENTER, GLFW.GLFW_KEY_KP_ENTER -> this.commit();
			case GLFW.GLFW_KEY_ESCAPE -> this.buffer = null;
			case GLFW.GLFW_KEY_BACKSPACE -> {
				if(!this.buffer.isEmpty()) this.buffer.setLength(this.buffer.length() - 1);
			}
			default -> {
			}
		}
		return true;
	}

}
