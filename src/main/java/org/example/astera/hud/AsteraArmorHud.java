package org.example.astera.hud;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import org.rusherhack.client.api.feature.hud.HudElement;
import org.rusherhack.client.api.render.RenderContext;
import org.rusherhack.client.api.render.font.IFontRenderer;
import org.rusherhack.client.api.setting.ColorSetting;
import org.rusherhack.core.setting.EnumSetting;

import java.awt.Color;

/**
 * Worn armor with the durability under each piece, shown as a value or with a % sign in percent mode
 */
public class AsteraArmorHud extends HudElement {

	public enum DurabilityMode {
		OFF, VALUE, PERCENT
	}

	public enum Axis {
		X, Y
	}

	private static final EquipmentSlot[] SLOTS = {EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET};
	private static final int ICON = 16;
	private static final int PAD = 1;

	private final EnumSetting<DurabilityMode> durability = new EnumSetting<>("Durability", "How the durability under each piece is shown", DurabilityMode.PERCENT);
	private final EnumSetting<Axis> axis = new EnumSetting<>("Axis", "Direction the pieces are laid out in", Axis.X);

	private final ColorSetting color = new ColorSetting("Color", new Color(255, 255, 255)).setThemeSync(true).setAlphaAllowed(false);

	public AsteraArmorHud() {
		super("AsteraArmor");
		this.setDisplayName("Astera Armor");
		this.registerSettings(this.durability, this.axis, this.color);
	}

	private boolean showText() {
		return this.durability.getValue() != DurabilityMode.OFF;
	}

	private double slotWidth() {
		return this.showText() ? ICON + PAD * 2 + 4 : ICON + PAD * 2;
	}

	private double slotHeight() {
		return ICON + PAD * 2 + (this.showText() ? this.getFontRenderer().getFontHeight() : 0);
	}

	private String durabilityText(ItemStack stack) {
		final int max = stack.getMaxDamage();
		final int left = max - stack.getDamageValue();
		return this.durability.getValue() == DurabilityMode.PERCENT ? Math.round(100f * left / max) + "%" : String.valueOf(left);
	}

	@Override
	public void renderContent(RenderContext context, double mouseX, double mouseY) {
		if(mc.player == null) {
			return;
		}
		final IFontRenderer fr = this.getFontRenderer();
		final double slotWidth = this.slotWidth();
		final double slotHeight = this.slotHeight();

		for(int i = 0; i < SLOTS.length; i++) {
			final ItemStack stack = mc.player.getItemBySlot(SLOTS[i]);
			final double x = this.axis.getValue() == Axis.X ? i * slotWidth : 0;
			final double y = this.axis.getValue() == Axis.Y ? i * slotHeight : 0;
			if(stack.isEmpty()) {
				continue;
			}

			final double iconX = x + (slotWidth - ICON) / 2;
			context.graphics().renderItem(stack, (int) iconX, (int) (y + PAD));

			if(this.showText() && stack.isDamageableItem()) {
				final String text = this.durabilityText(stack);
				fr.drawString(text, x + (slotWidth - fr.getStringWidth(text)) / 2, y + PAD + ICON, this.color.getValueRGB());
			}
		}
	}

	@Override
	public double getWidth() {
		return this.axis.getValue() == Axis.X ? this.slotWidth() * SLOTS.length : this.slotWidth();
	}

	@Override
	public double getHeight() {
		return this.axis.getValue() == Axis.Y ? this.slotHeight() * SLOTS.length : this.slotHeight();
	}

}
