package me.nelonn;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.geom.ModelPart;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class ModelExporter {

    public static void export() {
        System.out.println("Exporting Models");
        Path dir = Path.of(System.getProperty("user.home"), "Downloads/ModelExporter");
        try {
            Files.createDirectories(dir);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        EntityModelSet entityModelSet = Minecraft.getInstance().getEntityModels();
        ModelLayers.getKnownLocations().forEach(modelLayerLocation -> {
            PoseStack poseStack = new PoseStack();
            JsonObject rootObject = new JsonObject();
            rootObject.addProperty("credit", "Model Exporter by Nelonn");
            JsonArray elements = new JsonArray();
            if (modelLayerLocation.getModel().getPath().equals("player") && modelLayerLocation.getLayer().equals("main")) {
                System.out.println("PLAYER!");
            }
            entityModelSet.bakeLayer(modelLayerLocation).visit(poseStack, (PoseStack.Pose pose, String path, int cubeIndex, ModelPart.Cube cube) -> {
                JsonObject element = new JsonObject();
                element.addProperty("name", path);
                Matrix4f matrix = pose.pose();
                Vector3f min = new Vector3f(cube.minX, cube.minY, cube.minZ).div(16.0F);
                Vector3f max = new Vector3f(cube.maxX, cube.maxY, cube.maxZ).div(16.0F);
                Vector4f from = new Vector4f(min, 1.0F);
                Vector4f to = new Vector4f(max, 1.0F);
                matrix.transform(from);
                matrix.transform(to);
                from.mul(16.0F);
                to.mul(16.0F);
                from.add(8.0F, 0.0F, 8.0F, 0);
                to.add(8.0F, 0.0F, 8.0F, 0);
                element.add("from", floatsToArray(from.x, from.y, from.z));
                element.add("to", floatsToArray(to.x, to.y, to.z));
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

    private static JsonObject createFace(Vector2f offset, Vector4f uv4f) {
        JsonObject face = new JsonObject();
        JsonArray uv = new JsonArray(4);
        uv.add(offset.x + uv4f.x);
        uv.add(offset.y + uv4f.y);
        uv.add(offset.x + uv4f.x + uv4f.z);
        uv.add(offset.y + uv4f.y + uv4f.w);
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
