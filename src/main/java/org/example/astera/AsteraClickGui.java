package org.example.astera;

import net.minecraft.client.Minecraft;
import org.example.astera.items.ModuleItem;
import org.example.astera.items.SliderItem;
import org.rusherhack.client.api.RusherHackAPI;
import org.rusherhack.client.api.feature.module.IModule;
import org.rusherhack.client.api.feature.module.Module;
import org.rusherhack.client.api.feature.module.ModuleCategory;
import org.rusherhack.client.api.render.IRenderer2D;
import org.rusherhack.client.api.render.RenderContext;
import org.rusherhack.client.api.ui.panel.PanelHandlerBase;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Lays out one panel per module category in a single row, Rusherhack's default order first
 */
public class AsteraClickGui extends PanelHandlerBase<AsteraPanel> {

	private static final List<ModuleCategory> ORDER = List.of(
			ModuleCategory.COMBAT,
			ModuleCategory.MISC,
			ModuleCategory.MOVEMENT,
			ModuleCategory.PLAYER,
			ModuleCategory.RENDER,
			ModuleCategory.WORLD,
			ModuleCategory.CHAT,
			ModuleCategory.CLIENT,
			ModuleCategory.EXTERNAL
	);

	public AsteraClickGui() {
		super(true);
	}

	@Override
	public AsteraPanel createPanel(String name) {
		return new AsteraPanel(this, name);
	}

	@Override
	public void initialize() {
		this.panels.clear();

		final List<ModuleCategory> categories = new ArrayList<>(ORDER);
		for(ModuleCategory category : ModuleCategory.getCategories()) {
			if(!categories.contains(category)) {
				categories.add(category);
			}
		}

		for(ModuleCategory category : categories) {
			final List<IModule> modules = new ArrayList<>();
			for(IModule module : RusherHackAPI.getModuleManager().getFeatures()) {
				if(module.getCategory() != category) continue;
				if(module instanceof Module m && m.isHidden()) continue;
				modules.add(module);
			}
			//External is for modules from other plugins, keep its tab even when nothing is loaded
			if(modules.isEmpty() && category != ModuleCategory.EXTERNAL) {
				continue;
			}
			modules.sort(Comparator.comparing(m -> m.getDisplayName().toLowerCase()));

			final AsteraPanel panel = new AsteraPanel(this, category.getName());
			panel.setCategory(category);
			for(IModule module : modules) {
				panel.addItem(new ModuleItem(panel, module));
			}
			this.addPanel(panel);
		}

		this.setDefaultPositions();
	}

	@Override
	public void setDefaultPositions() {
		double x = AsteraPanel.MARGIN;
		for(AsteraPanel panel : this.panels) {
			panel.setX(x);
			panel.setY(AsteraPanel.MARGIN);
			x += AsteraPanel.WIDTH + AsteraPanel.GAP;
		}
	}

	// scale used for render and mouse input. it only follows the setting while no slider is dragged,
	// otherwise dragging the Scale slider resizes the slider under the mouse and the value runs away
	private float appliedScale = 1f;

	@Override
	public void render(RenderContext context, double mouseX, double mouseY) {
		if(!SliderItem.isAnyDragging()) {
			this.appliedScale = this.fitScale(AsteraPlugin.THEME.scale.getValue());
		}
		if(AsteraPlugin.THEME.backgroundBlur.getValue()) {
			try {
				context.graphics().blurBeforeThisStratum();
			} catch(IllegalStateException ignored) {
				//already blurred this frame
			}
		}
		super.render(context, mouseX, mouseY);
	}

	// caps the scale so the full panel row fits the window width
	private float fitScale(float wanted) {
		if(!AsteraPlugin.THEME.autoFit.getValue() || this.panels.isEmpty()) {
			return wanted;
		}
		final double rowWidth = AsteraPanel.MARGIN * 2 + this.panels.size() * (AsteraPanel.WIDTH + AsteraPanel.GAP) - AsteraPanel.GAP;
		final double available = Minecraft.getInstance().getWindow().getGuiScaledWidth() / (double) super.getScale();
		return (float) Math.min(wanted, available / rowWidth);
	}

	@Override
	public float getScale() {
		return super.getScale() * this.appliedScale;
	}

	// panels draw in absolute coordinates so hit testing matches what is drawn
	@Override
	public void renderElements(RenderContext context, double mouseX, double mouseY) {
		final IRenderer2D renderer = this.getRenderer();
		final boolean building = renderer.isBuilding();
		if(!building) renderer.begin(context.graphics(), this.getFontRenderer());

		for(AsteraPanel panel : this.panels) {
			if(!this.isEnabled(panel)) continue;
			panel.render(context, mouseX, mouseY);
		}

		if(!building) renderer.end();
	}

}
