package org.example.astera;

import org.rusherhack.client.api.RusherHackAPI;
import org.rusherhack.client.api.feature.hud.HudElement;
import org.rusherhack.client.api.render.IRenderer2D;
import org.rusherhack.client.api.render.RenderContext;
import org.rusherhack.client.api.ui.panel.PanelHandlerBase;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * The panel shown in the HUD editor. Lists every HUD element with its settings, same look as the ClickGUI.
 */
public class AsteraHudManager extends PanelHandlerBase<AsteraPanel> {

	public AsteraHudManager() {
		super(true);
	}

	@Override
	public AsteraPanel createPanel(String name) {
		return new AsteraPanel(this, name);
	}

	@Override
	public void initialize() {
		this.panels.clear();

		final List<HudElement> elements = new ArrayList<>();
		for(HudElement element : RusherHackAPI.getHudManager().getFeatures()) {
			elements.add(element);
		}
		elements.sort(Comparator.comparing(e -> e.getDisplayName().toLowerCase()));

		final AsteraPanel panel = this.createPanel("HUD");
		for(HudElement element : elements) {
			panel.addItem(element);
		}
		this.addPanel(panel);
		this.setDefaultPositions();
	}

	@Override
	public void setDefaultPositions() {
		for(AsteraPanel panel : this.panels) {
			panel.setX(AsteraPanel.MARGIN);
			panel.setY(AsteraPanel.MARGIN);
		}
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
