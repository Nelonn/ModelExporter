package me.nelonn;

import org.joml.Vector3f;
import org.joml.Vector4f;

public interface CubeTex {

    default Vector3f modelExporter$getGrow() {
        throw new UnsupportedOperationException();
    }

    default Vector4f modelExporter$texUVWH() {
        throw new UnsupportedOperationException();
    }

}
