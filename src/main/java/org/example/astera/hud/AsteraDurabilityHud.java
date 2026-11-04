package org.example.astera.hud;

import net.minecraft.world.item.ItemStack;
import org.rusherhack.client.api.feature.hud.TextHudElement;
import org.rusherhack.client.api.setting.ColorSetting;
import org.rusherhack.core.setting.BooleanSetting;
import org.rusherhack.core.setting.Setting;

import java.util.List;

/**
 * Durability of the held item, with a % sign in percentage mode
 */
public class AsteraDurabilityHud extends TextHudElement {

	private final BooleanSetting percentage = new BooleanSetting("Percentage", "Show the durability left as a percentage", true);
	private final BooleanSetting offhand = new BooleanSetting("Offhand", "Use the offhand item instead of the main hand", false);

	public AsteraDurabilityHud() {
		super("AsteraDurability");
		this.setDisplayName("Astera Durability");
		this.registerSettings(this.percentage, this.offhand);
		//follow the theme color like the other HUD elements
		syncColors(this.getSettings());
	}

	private static void syncColors(List<Setting<?>> settings) {
		for(Setting<?> setting : settings) {
			if(setting instanceof ColorSetting color && color.isThemeSyncAllowed()) {
				color.setThemeSync(true);
			}
			syncColors(setting.getSubSettings());
		}
	}

	@Override
	public String getLabel() {
		return "Durability";
	}

	@Override
	public String getText() {
		if(mc.player == null) {
			return "N/A";
		}
		final ItemStack stack = this.offhand.getValue() ? mc.player.getOffhandItem() : mc.player.getMainHandItem();
		if(stack.isEmpty() || !stack.isDamageableItem()) {
			return "N/A";
		}
		final int max = stack.getMaxDamage();
		final int left = max - stack.getDamageValue();
		return this.percentage.getValue() ? Math.round(100f * left / max) + "%" : String.valueOf(left);
	}

}
