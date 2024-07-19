package me.nelonn;

import com.google.common.math.DoubleMath;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.core.Direction;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.Function;

public class ModelExporter {

    public static void export() {
        System.out.println("Exporting Models");
        Path dir = Path.of(System.getProperty("user.home"), "Downloads/ModelExporter");
        try {
            Files.createDirectories(dir);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        float global_scale = 0.5F;
        boolean reformat_angles = true; // make angles snap by 22.5
        EntityModelSet entityModelSet = Minecraft.getInstance().getEntityModels();
        ModelLayers.getKnownLocations().forEach(modelLayerLocation -> {
            PoseStack poseStack = new PoseStack();
            JsonObject rootObject = new JsonObject();
            rootObject.addProperty("credit", "Model Exporter by Nelonn");
            JsonArray elements = new JsonArray();
            if (modelLayerLocation.getModel().getPath().equals("wolf") && modelLayerLocation.getLayer().equals("main")) {
                System.out.println("DEBUG POINT");
            }
            entityModelSet.bakeLayer(modelLayerLocation).visit(poseStack, (PoseStack.Pose pose, String path, int cubeIndex, ModelPart.Cube cube) -> {
                CubeTex cubeTex = (CubeTex) (Object) cube;
                // Used as workaround for IntelliJ IDEA issue
                RawPose rawPose = new Function<PoseStack.Pose, RawPose>() {
                    @Override
                    public RawPose apply(PoseStack.Pose pose) {
                        return (RawPose) (Object) pose;
                    }
                }.apply(pose);
                JsonObject element = new JsonObject();
                element.addProperty("name", path);
                Vector3f from = new Vector3f(cube.minX, cube.minY, cube.minZ).sub(cubeTex.modelExporter$getGrow()).div(16.0F);
                Vector3f to = new Vector3f(cube.maxX, cube.maxY, cube.maxZ).add(cubeTex.modelExporter$getGrow()).div(16.0F);
                from.add(rawPose.modelExporter$translation()).mul(rawPose.modelExporter$scale());
                to.add(rawPose.modelExporter$translation()).mul(rawPose.modelExporter$scale());
                from.mul(16.0F * global_scale);
                to.mul(16.0F * global_scale);
                float from_y = from.y;
                from.y = to.y * -1;
                to.y = from_y * -1;
                // Center model
                from.add(8.0F, 12.0F, 8.0F);
                to.add(8.0F, 12.0F, 8.0F);
                Vector3f euler = new Vector3f();
                rawPose.modelExporter$rotation().getEulerAnglesXYZ(euler);
                euler.mul((float) (180.0D / Math.PI));
                element.add("from", floatsToArray(from.x, from.y, from.z));
                element.add("to", floatsToArray(to.x, to.y, to.z));
                float angle = 0.0F;
                Direction.Axis axis = Direction.Axis.Y;
                if (euler.x != 0 && DoubleMath.fuzzyEquals(euler.y, 0, 1.0E-7) && DoubleMath.fuzzyEquals(euler.z, 0, 1.0E-7)) {
                    angle = -euler.x;
                    axis = Direction.Axis.X;
                } else if (DoubleMath.fuzzyEquals(euler.x, 0, 1.0E-7) && euler.y != 0 && DoubleMath.fuzzyEquals(euler.z, 0, 1.0E-7)) {
                    angle = -euler.y;
                    axis = Direction.Axis.Y;
                } else if (DoubleMath.fuzzyEquals(euler.x, 0, 1.0E-7) && DoubleMath.fuzzyEquals(euler.y, 0, 1.0E-7) && euler.z != 0) {
                    angle = -euler.z;
                    axis = Direction.Axis.Z;
                }
                if (reformat_angles) {
                    angle = Math.round(angle / 22.5F) * 22.5F;
                }
                element.add("rotation", createRotation(angle, axis, new Vector3f()
                        .add(rawPose.modelExporter$translation())
                        .mul(16.0F * global_scale)
                        .mul(1, -1, 1)
                        .add(8, 12, 8))); // Center model
                element.add("faces", createFaces(cube));
                elements.add(element);
            });
            rootObject.add("elements", elements);
            String fileName = modelLayerLocation.getModel().getPath() + "-" + modelLayerLocation.getLayer() + ".json";
            Path file = dir.resolve(fileName);
            try {
                Files.writeString(file, rootObject.toString());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            System.out.println("Exported " + fileName);
        });
    }



    private static JsonObject createRotation(float angle, Direction.Axis axis, Vector3f origin) {
        JsonObject rotation = new JsonObject();
        rotation.addProperty("angle", angle);
        rotation.addProperty("axis", axis.toString());
        rotation.add("origin", floatsToArray(origin.x, origin.y, origin.z));
        return rotation;
    }

    private static JsonObject createFaces(ModelPart.Cube cube) {
        CubeTex cubeTex = (CubeTex) (Object) cube;
        Vector4f uvwh = cubeTex.modelExporter$texUVWH();
        float xSize = cube.maxX - cube.minX;
        float ySize = cube.maxY - cube.minY;
        float zSize = cube.maxZ - cube.minZ;
        float sf = 1.0F / 4.0F;
        Vector4f scale = new Vector4f(sf, sf, sf, sf);
        Vector2f offset = new Vector2f(uvwh.x, uvwh.y).mul(sf);
        JsonObject faces = new JsonObject();
        //faces.add("north", createFace(cube.maxX - cube.minX, cube.maxY - cube.minY));
        //faces.add("east", createFace(cube.maxZ - cube.minZ, cube.maxY - cube.minY));
        //faces.add("south", createFace(cube.maxX - cube.minX, cube.maxY - cube.minY));
        //faces.add("west", createFace(cube.maxZ - cube.minZ, cube.maxY - cube.minY));
        //faces.add("up", createFace(cube.maxX - cube.minX, cube.maxZ - cube.minZ));
        //faces.add("down", createFace(cube.maxX - cube.minX, cube.maxZ - cube.minZ));
        faces.add("north", createFace(offset, new Vector4f(   zSize,                 zSize,  xSize, ySize).mul(scale)));
        faces.add("east",  createFace(offset, new Vector4f(0,                     zSize,  zSize, ySize).mul(scale)));
        faces.add("south", createFace(offset, new Vector4f(zSize + xSize + zSize, zSize,  xSize, ySize).mul(scale)));
        faces.add("west",  createFace(offset, new Vector4f(zSize + xSize,         zSize,  zSize, ySize).mul(scale)));
        faces.add("up",    createFace(offset, new Vector4f(   zSize,                 0,   xSize, zSize).mul(scale)));
        faces.add("down",  createFace(offset, new Vector4f(zSize + xSize,         0,   xSize, zSize).mul(scale)));
        return faces;
    }

    private static JsonObject createFace(Vector2f offset, Vector4f uvwh) {
        JsonObject face = new JsonObject();
        JsonArray uv = new JsonArray(4);
        uv.add(offset.x + uvwh.x);
        uv.add(offset.y + uvwh.y);
        uv.add(offset.x + uvwh.x + uvwh.z);
        uv.add(offset.y + uvwh.y + uvwh.w);
        face.add("uv", uv);
        face.addProperty("texture", "#missing");
        return face;
    }

    private static JsonArray floatsToArray(float... floats) {
        JsonArray array = new JsonArray(floats.length);
        for (float vf : floats) {
            array.add(vf);
        }
        return array;
    }

}
