package ru.demmax93;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.PointLight;
import com.badlogic.gdx.graphics.g3d.utils.AnimationController;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.ScreenUtils;

public class SupermanDemoGame extends ApplicationAdapter {
    public PerspectiveCamera cam;
    private final Vector3 startPos = new Vector3(2f, 200f, -180f);
    public Model model;
    public ModelInstance instance;
    public ModelBatch modelBatch;
    public Environment environment;
    public AssetManager assets;
    public boolean loading;
    public AnimationController animationController;
    public CameraInputController cameraInputController;

    @Override
    public void create() {
        cam = new PerspectiveCamera(67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        cam.position.set(startPos);
        cam.lookAt(0, 100f, 0);
        cam.near = 0.01f;
        cam.far = 1000f;
        cam.update();
        cameraInputController = new CameraInputController(cam);
        modelBatch = new ModelBatch();
        environment = new Environment();
        environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1f));
        environment.add(new PointLight().set(Color.RED, startPos, 1f));
        assets = new AssetManager();
        assets.load("BaseMesh_Anim.g3dj", Model.class);
        loading = true;
        Gdx.input.setInputProcessor(cameraInputController);
    }

    @Override
    public void render() {
        ScreenUtils.clear(Color.WHITE);
        Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

        if (loading) {
            if (assets.update()) {
                model = assets.get("BaseMesh_Anim.g3dj", Model.class);
                instance = new ModelInstance(model);
                animationController = new AnimationController(instance);
                loading = false;
            } else {
                return;
            }
        }

        if (Gdx.input.isKeyPressed(Input.Keys.SPACE)) {
            animationController.animate(instance.animations.get(2).id, 50f);
        }
        modelBatch.begin(cam);
        modelBatch.render(instance, environment);
        animationController.update(Gdx.graphics.getDeltaTime());
        cam.update();
        modelBatch.end();
    }

    @Override
    public void dispose() {
        model.dispose();
        modelBatch.dispose();
    }
}
