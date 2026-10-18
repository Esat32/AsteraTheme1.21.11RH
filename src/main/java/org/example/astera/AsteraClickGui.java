package org.example.astera;

import org.example.astera.items.ModuleItem;
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
 * Lays out one panel per module category in a single row, Astera order first
 */
public class AsteraClickGui extends PanelHandlerBase<AsteraPanel> {

	private static final List<ModuleCategory> ORDER = List.of(
			ModuleCategory.COMBAT,
			ModuleCategory.MISC,
			ModuleCategory.RENDER,
			ModuleCategory.MOVEMENT,
			ModuleCategory.PLAYER,
			ModuleCategory.WORLD,
			ModuleCategory.CHAT,
			ModuleCategory.CLIENT
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
			if(modules.isEmpty()) {
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

	// render and mouse input both go through this, so clicks stay lined up
	@Override
	public float getScale() {
		return super.getScale() * AsteraPlugin.THEME.scale.getValue();
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
