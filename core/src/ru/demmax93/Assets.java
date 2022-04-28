package ru.demmax93;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

import java.util.HashMap;
import java.util.Map;

public class Assets {
    public static Skin skin;
    public static AssetManager assets;
    private static final Map<String, Boolean> isModelLoading = new HashMap<>();

    public static void load() {
        skin = new Skin();
        assets = new AssetManager();
        FileHandle fileHandle = Gdx.files.internal("data/uiskin.json");
        FileHandle atlasFile = fileHandle.sibling("uiskin.atlas");
        if (atlasFile.exists()) {
            skin.addRegions(new TextureAtlas(atlasFile));
        }
        skin.load(fileHandle);
    }

    public static void loadModel(String pathToModel) {
        assets.load(pathToModel, Model.class);
        isModelLoading.put(pathToModel, Boolean.TRUE);
    }

    public static Model getModel(String pathToModel) {
        if (isModelLoading.get(pathToModel)) {
            if (assets.update()) {
                isModelLoading.put(pathToModel, Boolean.FALSE);
                return assets.get(pathToModel, Model.class);
            }
        }
        return null;
    }

    public static void dispose() {
        skin.dispose();
    }
}
