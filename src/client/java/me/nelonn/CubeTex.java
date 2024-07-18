package me.nelonn;

import org.joml.Vector4f;

public interface CubeTex {

    default Vector4f modelExporter$texUVWH() {
        throw new UnsupportedOperationException();
    }

}
