package org.example.astera.items;

import org.example.astera.AsteraPanel;
import org.rusherhack.client.api.setting.BindSetting;
import org.rusherhack.client.api.setting.ColorSetting;
import org.rusherhack.core.setting.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Picks the row type for a setting and adds its sub settings as children
 */
public final class SettingItems {

	private SettingItems() {
	}

	@SuppressWarnings({"unchecked", "rawtypes"})
	public static AsteraItem create(AsteraPanel panel, AsteraItem parent, Setting<?> setting) {
		final String name = setting.getDisplayName();
		final AsteraItem item;

		if(setting instanceof BooleanSetting bool) {
			item = new ToggleItem(panel, parent, name, bool::getValue, bool::setValue);
		} else if(setting instanceof NumberSetting<?> number) {
			item = new SliderItem(panel, parent, name,
					number.getMinimum().doubleValue(), number.getMaximum().doubleValue(),
					() -> number.getValue().doubleValue(), number::setValue, number::getDisplayValue);
		} else if(setting instanceof EnumSetting<?> enumSetting) {
			item = new OptionItem(panel, parent, name, enumSetting::getDisplayValue, enumSetting::increment, enumSetting::decrement);
		} else if(setting instanceof OptionSetting option) {
			item = new OptionItem(panel, parent, name, option::getDisplayValue, () -> cycle(option, option.getOptions(), 1), () -> cycle(option, option.getOptions(), -1));
		} else if(setting instanceof StringSetting string) {
			if(string.getOptions().isEmpty()) {
				item = new StringItem(panel, parent, name, string::getValue, string::setValue);
			} else {
				item = new OptionItem(panel, parent, name, string::getDisplayValue,
						() -> cycle((Setting) string, new ArrayList<>(string.getOptions()), 1),
						() -> cycle((Setting) string, new ArrayList<>(string.getOptions()), -1));
			}
		} else if(setting instanceof BindSetting bind) {
			item = new BindItem(panel, parent, name, bind::getValue, bind::setValue);
		} else if(setting instanceof ColorSetting color) {
			item = new ColorItem(panel, parent, name, color);
		} else {
			item = new LabelItem(panel, parent, name, setting instanceof NullSetting ? null : setting::getDisplayValue);
		}

		item.setVisibility(() -> !setting.isHidden());
		for(Setting<?> sub : setting.getSubSettings()) {
			item.addChild(create(panel, item, sub));
		}
		return item;
	}

	private static <T> void cycle(Setting<T> setting, List<T> options, int direction) {
		if(options.isEmpty()) return;
		final int index = options.indexOf(setting.getValue());
		final int next = Math.floorMod(index + direction, options.size());
		setting.setValue(options.get(next));
	}

}
