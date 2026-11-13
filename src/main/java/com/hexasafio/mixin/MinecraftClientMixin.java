package com.hexasafio.mixin;

import com.hexasafio.HexaPauseScreen;
import com.hexasafio.HexaTitleScreen;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.GameMenuScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

/**
 * Cambia el menu principal vanilla (TitleScreen) por HexaTitleScreen
 * y el menu de pausa vanilla (GameMenuScreen) por HexaPauseScreen.
 */
@Mixin(MinecraftClient.class)
public abstract class MinecraftClientMixin {

    @ModifyVariable(method = "setScreen", at = @At("HEAD"), argsOnly = true)
    private Screen hexasafio$replaceTitleScreen(Screen screen) {
        if (screen != null && screen.getClass() == TitleScreen.class) {
            return new HexaTitleScreen();
        }
        if (screen != null && screen.getClass() == GameMenuScreen.class) {
            return new HexaPauseScreen();
        }
        return screen;
    }
}
