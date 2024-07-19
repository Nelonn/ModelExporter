package me.nelonn.mixin.client;

import com.mojang.blaze3d.vertex.PoseStack;
import me.nelonn.RawPose;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(PoseStack.Pose.class)
public abstract class PoseStackPoseMixin implements RawPose {

    @Unique private Vector3f translation = new Vector3f();
    @Unique private Vector3f scale = new Vector3f(1, 1, 1);
    @Unique private Quaternionf rotation = new Quaternionf();

    @Override
    public Vector3f modelExporter$translation() {
        return translation;
    }

    @Override
    public Vector3f modelExporter$scale() {
        return scale;
    }

    @Override
    public Quaternionf modelExporter$rotation() {
        return rotation;
    }

}
