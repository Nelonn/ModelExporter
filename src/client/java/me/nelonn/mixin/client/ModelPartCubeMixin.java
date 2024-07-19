package me.nelonn.mixin.client;

import me.nelonn.CubeTex;
import net.minecraft.client.model.geom.ModelPart;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Set;

@Mixin(ModelPart.Cube.class)
public abstract class ModelPartCubeMixin implements CubeTex {

    @Unique private float tex_u0 = 0.0F;
    @Unique private float tex_v0 = 0.0F;
    @Unique private float tex_u1 = 0.0F;
    @Unique private float tex_v1 = 0.0F;
    @Unique private Vector3f grow = new Vector3f();

    @Inject(method = "<init>", at = @At("TAIL"))
    private void onInit(int i, int j, float f, float g, float h, float k, float l, float m, float n, float o, float p, boolean bl, float q, float r, Set set, CallbackInfo ci) {
        tex_u0 = i;
        tex_v0 = j;
        tex_u1 = q;
        tex_v1 = r;
        grow.x = n;
        grow.y = o;
        grow.z = p;
    }

    @Override
    public Vector3f modelExporter$getGrow() {
        return grow;
    }

    @Override
    public Vector4f modelExporter$texUVWH() {
        return new Vector4f(tex_u0, tex_v0, tex_u1, tex_v1);
    }
}
