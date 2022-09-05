package ru.demmax93.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Matrix4;
import net.mgsx.gltf.scene3d.scene.Scene;

public class ModelComponent implements Component {
    public Model model;
    public ModelInstance instance;
    public Scene scene;

    public ModelComponent(Model model, float x, float y, float z) {
        this.model = model;
        this.instance = new ModelInstance(model, new Matrix4().setToTranslation(x, y, z));
        this.scene = new Scene(instance);
    }
}
