package org.example.astera.window;

import org.example.astera.AsteraPlugin;
import org.example.astera.AsteraTheme;
import org.rusherhack.client.api.render.IRenderer2D;
import org.rusherhack.client.api.render.font.IFontRenderer;
import org.rusherhack.client.api.ui.window.WindowContentHandlerBase;
import org.rusherhack.client.api.ui.window.content.ListItemContent;
import org.rusherhack.client.api.ui.window.content.WindowContent;
import org.rusherhack.client.api.ui.window.content.component.ButtonComponent;
import org.rusherhack.client.api.ui.window.content.component.CheckBoxComponent;
import org.rusherhack.client.api.ui.window.content.component.ComboBoxComponent;
import org.rusherhack.client.api.ui.window.content.component.TextFieldComponent;
import org.rusherhack.client.api.ui.window.view.ListView;
import org.rusherhack.client.api.ui.window.view.WindowView;

import java.util.List;

/**
 * Window controls in the Astera style: square, dark fields, gradient for active and selected states
 */
public class AsteraWindowContentHandler extends WindowContentHandlerBase {

	private static final int FIELD = 0xFF2C2142;
	private static final int FIELD_HOVER = 0xFF35294D;

	private AsteraTheme theme() {
		return AsteraPlugin.THEME;
	}

	private void centeredText(IFontRenderer fr, String text, double x, double y, double w, double h, int color) {
		fr.drawString(text, x + (w - fr.getStringWidth(text)) / 2, y + (h - fr.getFontHeight()) / 2 + 0.5, color, this.theme().textShadow.getValue());
	}

	// button predicates can touch the player or world, which are null on the main menu
	private static boolean isEnabled(ButtonComponent button) {
		try {
			return button.isEnabled();
		} catch(Throwable t) {
			return false;
		}
	}

	@Override
	public void renderButton(ButtonComponent button, double mouseX, double mouseY, WindowView view) {
		final IRenderer2D renderer = button.getRenderer();
		final double x = button.getX(), y = button.getY(), w = button.getWidth(), h = button.getHeight();
		final boolean hovered = button.isHovered(mouseX, mouseY);
		final boolean enabled = isEnabled(button);

		if(!enabled) {
			renderer.drawRectangle(x, y, w, h, FIELD);
		} else if(button.isPressed()) {
			this.theme().fillGradient(renderer, x, y, w, h, 255);
		} else if(hovered) {
			this.theme().fillGradient(renderer, x, y, w, h, 170);
		} else {
			renderer.drawRectangle(x, y, w, h, FIELD);
		}
		this.centeredText(button.getFontRenderer(), button.getName(), x, y, w, h,
				enabled ? this.theme().enabledTextColor.getValueRGB() : this.theme().valueColor.getValueRGB());
	}

	@Override
	public void renderCheckBox(CheckBoxComponent checkBox, double mouseX, double mouseY, WindowView view) {
		final IRenderer2D renderer = checkBox.getRenderer();
		final IFontRenderer fr = checkBox.getFontRenderer();
		final double x = checkBox.getX(), y = checkBox.getY(), size = checkBox.getSize();

		renderer.drawRectangle(x, y, size, size, checkBox.isHovered(mouseX, mouseY) ? FIELD_HOVER : FIELD);
		if(checkBox.getValue()) {
			this.theme().fillGradient(renderer, x + 2, y + 2, size - 4, size - 4, 255);
		}
		fr.drawString(checkBox.getLabel(), x + size + 2, y + (size - fr.getFontHeight()) / 2 + 0.5, this.theme().textColor.getValueRGB(), this.theme().textShadow.getValue());
	}

	@Override
	public void renderComboBox(ComboBoxComponent dropdown, double mouseX, double mouseY, WindowView view) {
		final IRenderer2D renderer = dropdown.getRenderer();
		final IFontRenderer fr = dropdown.getFontRenderer();
		final double x = dropdown.getX(), y = dropdown.getY(), w = dropdown.getWidth(), h = dropdown.getHeight();

		renderer.drawRectangle(x, y, w, h, dropdown.isHovered(mouseX, mouseY) ? FIELD_HOVER : FIELD);
		this.theme().fillGradient(renderer, x, y, 2, h, 255);
		fr.drawString(dropdown.getSelected(), x + 4, y + (h - fr.getFontHeight()) / 2 + 0.5, this.theme().textColor.getValueRGB(), this.theme().textShadow.getValue());

		if(!dropdown.isOpen()) {
			return;
		}
		//options list below the box
		final List<String> options = dropdown.getOptions();
		double optionY = y + h;
		for(String option : options) {
			final boolean hovered = mouseX >= x && mouseX < x + w && mouseY >= optionY && mouseY < optionY + h;
			final boolean selected = option.equals(dropdown.getSelected());
			renderer.drawRectangle(x, optionY, w, h, this.theme().bodyFill() | 0xFF000000);
			if(selected) {
				this.theme().fillGradient(renderer, x, optionY, w, h, 128);
			} else if(hovered) {
				renderer.drawRectangle(x, optionY, w, h, this.theme().hoverFill());
			}
			fr.drawString(option, x + 4, optionY + (h - fr.getFontHeight()) / 2 + 0.5, selected ? this.theme().enabledTextColor.getValueRGB() : this.theme().textColor.getValueRGB(), this.theme().textShadow.getValue());
			optionY += h;
		}
	}

	// the dropdown behaviour is left to the theme, see ComboBoxComponent
	@Override
	public boolean handleMouseClicked(WindowContent content, double mouseX, double mouseY, int button, WindowView view) {
		if(content instanceof ComboBoxComponent dropdown) {
			final double x = dropdown.getX(), y = dropdown.getY(), w = dropdown.getWidth(), h = dropdown.getHeight();
			if(button == 0 && dropdown.isHovered(mouseX, mouseY)) {
				dropdown.setOpen(!dropdown.isOpen());
				return true;
			}
			if(button == 0 && dropdown.isOpen()) {
				final List<String> options = dropdown.getOptions();
				if(mouseX >= x && mouseX < x + w && mouseY >= y + h && mouseY < y + h + h * options.size()) {
					dropdown.setSelected(options.get((int) ((mouseY - y - h) / h)));
					dropdown.setOpen(false);
					return true;
				}
			}
			dropdown.setOpen(false);
			return false;
		}
		return super.handleMouseClicked(content, mouseX, mouseY, button, view);
	}

	@Override
	public void renderTextField(TextFieldComponent textField, double mouseX, double mouseY, WindowView view) {
		final IRenderer2D renderer = textField.getRenderer();
		final double x = textField.getX(), y = textField.getY(), w = textField.getWidth(), h = textField.getHeight();

		renderer.drawRectangle(x, y, w, h, FIELD);
		if(textField.isFocused()) {
			this.theme().fillGradient(renderer, x, y + h - 1, w, 1, 255);
		}
		//text, selection and cursor are drawn by the component itself
		textField.renderContent(mouseX, mouseY, view);
	}

	@Override
	@SuppressWarnings({"unchecked", "rawtypes"})
	public void renderListItem(ListItemContent listItem, double mouseX, double mouseY, WindowView view) {
		final IRenderer2D renderer = listItem.getRenderer();
		final IFontRenderer fr = listItem.getFontRenderer();
		final double x = listItem.getX(), y = listItem.getY(), w = listItem.getWidth(), h = listItem.getHeight();

		if(listItem.isSelected()) {
			this.theme().fillGradient(renderer, x, y, w, h, 128);
		} else if(listItem.isHovered(mouseX, mouseY)) {
			renderer.drawRectangle(x, y, w, h, this.theme().hoverFill());
		}

		final ListView listView = listItem.getListView();
		final int color = listItem.isSelected() ? this.theme().enabledTextColor.getValueRGB() : this.theme().textColor.getValueRGB();
		double columnX = x;
		for(Object object : listView.getColumns()) {
			final ListView.Column column = (ListView.Column) object;
			final double columnWidth = listView.getColumnWidth(column);
			final String text = fr.trimStringToWidth(listItem.getAsString(column), Math.max(columnWidth - 4, 0));
			fr.drawString(text, columnX + 2, y + (h - fr.getFontHeight()) / 2 + 0.5, color, this.theme().textShadow.getValue());
			columnX += columnWidth;
		}
	}

}
