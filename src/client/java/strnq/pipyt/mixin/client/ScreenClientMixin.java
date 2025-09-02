package strnq.pipyt.mixin.client;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import strnq.pipyt.PiPBrowser;

@Mixin(Screen.class)
public class ScreenClientMixin {
	@Inject(at = @At("RETURN"), method = "render")
	private void onRender(CallbackInfo info) {
        if (MinecraftClient.getInstance().currentScreen instanceof PiPBrowser || PiPBrowser.browser == null){return;}
		if (MinecraftClient.getInstance().player == null) {
			PiPBrowser.browser.close();
			PiPBrowser.browser = null;
		}
        PiPBrowser.renderBrowser();
	}
}