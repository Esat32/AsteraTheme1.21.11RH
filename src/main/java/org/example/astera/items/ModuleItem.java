package org.example.astera.items;

import org.example.astera.AsteraPanel;
import org.lwjgl.glfw.GLFW;
import org.rusherhack.client.api.RusherHackAPI;
import org.rusherhack.client.api.bind.IBindManager;
import org.rusherhack.client.api.feature.module.IModule;
import org.rusherhack.client.api.feature.module.ToggleableModule;
import org.rusherhack.client.api.render.RenderContext;
import org.rusherhack.core.interfaces.IToggleable;
import org.rusherhack.core.setting.Setting;

/**
 * Left click toggles, right click shows settings
 */
public class ModuleItem extends AsteraItem {

	private final IModule module;

	public ModuleItem(AsteraPanel panel, IModule module) {
		super(panel, null);
		this.module = module;

		if(module instanceof ToggleableModule toggleable) {
			final IBindManager binds = RusherHackAPI.getBindManager();
			this.addChild(new BindItem(panel, this, "Bind", () -> binds.getBind(toggleable), key -> binds.setBind(toggleable, key)));
		}
		for(Setting<?> setting : module.getSettings()) {
			this.addChild(SettingItems.create(panel, this, setting));
		}
	}

	private boolean isEnabled() {
		return this.module instanceof IToggleable toggleable && toggleable.isToggled();
	}

	@Override
	protected void renderRow(RenderContext context, double mouseX, double mouseY) {
		final boolean enabled = this.isEnabled();
		if(enabled) {
			this.fillRow(this.theme().enabledFill());
		}
		this.renderHover(mouseX, mouseY);
		this.drawLabel(this.module.getDisplayName(), enabled ? this.theme().enabledTextColor.getValueRGB() : this.theme().textColor.getValueRGB());
	}

	@Override
	protected boolean onClick(double mouseX, double mouseY, int button) {
		if(button == GLFW.GLFW_MOUSE_BUTTON_LEFT && this.module instanceof IToggleable toggleable) {
			toggleable.toggle();
			return true;
		}
		return super.onClick(mouseX, mouseY, button);
	}

}
