package org.example.astera.items;

import org.example.astera.AsteraPanel;
import org.lwjgl.glfw.GLFW;
import org.rusherhack.client.api.RusherHackAPI;
import org.rusherhack.client.api.render.RenderContext;
import org.rusherhack.core.bind.key.IKey;
import org.rusherhack.core.bind.key.NullKey;

import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Left click to listen, then press a key or mouse button. Esc / Delete / Backspace unbinds.
 */
public class BindItem extends AsteraItem {

	private final String label;
	private final Supplier<IKey> getter;
	private final Consumer<IKey> setter;
	private boolean listening = false;

	public BindItem(AsteraPanel panel, AsteraItem parent, String label, Supplier<IKey> getter, Consumer<IKey> setter) {
		super(panel, parent);
		this.label = label;
		this.getter = getter;
		this.setter = setter;
	}

	@Override
	protected void renderRow(RenderContext context, double mouseX, double mouseY) {
		if(this.listening) {
			this.fillRow(this.theme().enabledFill());
		}
		this.renderHover(mouseX, mouseY);
		this.drawLabel(this.label, this.theme().textColor.getValueRGB());
		this.drawValue(this.listening ? "..." : this.keyName(), this.theme().valueColor.getValueRGB());
	}

	@Override
	protected String labelForSpacing() {
		return this.label;
	}

	private String keyName() {
		final IKey key = this.getter.get();
		if(key == null || key instanceof NullKey) {
			return "None";
		}
		return key.getDisplayLabel();
	}

	@Override
	protected boolean onClick(double mouseX, double mouseY, int button) {
		if(this.listening) {
			if(button != GLFW.GLFW_MOUSE_BUTTON_LEFT) {
				this.setter.accept(RusherHackAPI.getBindManager().createMouseKey(button));
			}
			this.listening = false;
			return true;
		}
		if(button == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
			this.listening = true;
			return true;
		}
		return super.onClick(mouseX, mouseY, button);
	}

	@Override
	protected boolean onKey(int key) {
		if(!this.listening) {
			return false;
		}
		if(key == GLFW.GLFW_KEY_ESCAPE || key == GLFW.GLFW_KEY_DELETE || key == GLFW.GLFW_KEY_BACKSPACE) {
			this.setter.accept(NullKey.INSTANCE);
		} else {
			this.setter.accept(RusherHackAPI.getBindManager().createKeyboardKey(key));
		}
		this.listening = false;
		return true;
	}

}
