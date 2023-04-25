package io.github.jacg311.resourcepackconflicts.client.mixin;

import io.github.jacg311.resourcepackconflicts.client.ResourcepackConflictsClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.pack.PackScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PackScreen.class)
public class PackScreenMixin extends Screen {
    protected PackScreenMixin(Text title) {
        super(title);
    }

    @Inject(method = "init", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/pack/PackScreen;refresh()V"))
    private void respack_conflicts$addButtons(CallbackInfo ci) {
        this.addDrawableChild(
                new ButtonWidget(this.width / 2 - 154, this.height - 30, 150, 20, Text.of("Check Conflicts"), ResourcepackConflictsClient::logConflicts)
        );
    }
}
