package strnq.pipyt;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;

import org.lwjgl.glfw.GLFW;

public class PiPYoutubeClient implements ClientModInitializer {

	private static final MinecraftClient minecraft = MinecraftClient.getInstance();

	public static final KeyBinding KEY_MAPPING = KeyBindingHelper.registerKeyBinding(new KeyBinding(
			"Open",
			InputUtil.Type.KEYSYM,
			GLFW.GLFW_KEY_H,
			"PiP YT"
	));

	@Override
	public void onInitializeClient() {
		ClientTickEvents.START_CLIENT_TICK.register((client) -> onTick());
		HudRenderCallback.EVENT.register((matrixStack, tickDelta)->{
			if (minecraft.currentScreen==null)
				PiPBrowser.renderBrowser();
		});
	}

	public void onTick() {
		// Check if our key was pressed
		if (KEY_MAPPING.wasPressed() && !(minecraft.currentScreen instanceof PiPBrowser)) {
			//Display the web browser UI.
			minecraft.setScreen(new PiPBrowser(
					Text.literal("PiP YT")
			));
		}
	}
}