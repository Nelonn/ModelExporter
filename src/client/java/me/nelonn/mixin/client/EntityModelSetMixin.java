package me.nelonn.mixin.client;

import me.nelonn.ModelExporter;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.server.packs.resources.ResourceManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityModelSet.class)
public class EntityModelSetMixin {

    @Inject(method = "onResourceManagerReload", at = @At("TAIL"))
    private void modelexporter_onReload(ResourceManager resourceManager, CallbackInfo ci) {
        ModelExporter.export();
    }

}
