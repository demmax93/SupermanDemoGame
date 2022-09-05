package ru.demmax93;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.FloatAttribute;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.physics.bullet.Bullet;
import net.mgsx.gltf.scene3d.attributes.PBRCubemapAttribute;
import net.mgsx.gltf.scene3d.attributes.PBRTextureAttribute;
import net.mgsx.gltf.scene3d.lights.DirectionalLightEx;
import net.mgsx.gltf.scene3d.scene.SceneManager;
import net.mgsx.gltf.scene3d.scene.SceneSkybox;
import net.mgsx.gltf.scene3d.utils.IBLBuilder;
import ru.demmax93.components.CharacterComponent;
import ru.demmax93.managers.EntityFactory;
import ru.demmax93.systems.BulletSystem;
import ru.demmax93.systems.PlayerSystem;
import ru.demmax93.systems.RenderSystem;
import ru.demmax93.systems.StatusSystem;
import ru.demmax93.ui.GameUI;

public class GameWorld {
    private ModelBatch modelBatch;
    private PerspectiveCamera perspectiveCamera;
    private SceneManager sceneManager;
    private Cubemap diffuseCubemap;
    private Cubemap environmentCubemap;
    private Cubemap specularCubemap;
    private SceneSkybox skybox;

    private Engine engine;
    private Entity character;
    private BulletSystem bulletSystem;
    private final ModelBuilder modelBuilder = new ModelBuilder();
    private boolean isPlayerCreated = false;

    Model wallHorizontal = modelBuilder.createBox(400, 200, 1,
            new Material(ColorAttribute.createDiffuse(Color.WHITE), ColorAttribute.createSpecular(Color.RED), FloatAttribute
                    .createShininess(16f)), VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);
    Model wallVertical = modelBuilder.createBox(1, 200, 400,
            new Material(ColorAttribute.createDiffuse(Color.GREEN), ColorAttribute.createSpecular(Color.WHITE), FloatAttribute
                    .createShininess(16f)), VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);
    Model groundModel = modelBuilder.createBox(400, 1, 400,
            new Material(ColorAttribute.createDiffuse(Color.YELLOW), ColorAttribute.createSpecular(Color.BLUE), FloatAttribute
                    .createShininess(16f)), VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);

    public GameWorld(GameUI gameUI) {
        Bullet.init();
        initModelBatch();
        initPersCamera();
        initEnvironment();
        addSystems(gameUI);
        addEntities();
    }

    private void initEnvironment() {
        sceneManager = new SceneManager();
        sceneManager.setBatch(modelBatch);
        sceneManager.setCamera(perspectiveCamera);

        // setup light
        DirectionalLightEx light = new DirectionalLightEx();
        light.direction.set(100, -100, 100).nor();
        light.color.set(Color.YELLOW);
        sceneManager.environment.add(light);

        // setup quick IBL (image based lighting)
        IBLBuilder iblBuilder = IBLBuilder.createOutdoor(light);
        environmentCubemap = iblBuilder.buildEnvMap(2048);
        diffuseCubemap = iblBuilder.buildIrradianceMap(512);
        specularCubemap = iblBuilder.buildRadianceMap(10);
        iblBuilder.dispose();

        // This texture is provided by the library, no need to have it in your assets.
        Texture brdfLUT = new Texture(Gdx.files.classpath("net/mgsx/gltf/shaders/brdfLUT.png"));

        sceneManager.setAmbientLight(1f);
        sceneManager.environment.set(new PBRTextureAttribute(PBRTextureAttribute.BRDFLUTTexture, brdfLUT));
        sceneManager.environment.set(PBRCubemapAttribute.createSpecularEnv(specularCubemap));
        sceneManager.environment.set(PBRCubemapAttribute.createDiffuseEnv(diffuseCubemap));

        skybox = new SceneSkybox(environmentCubemap);
        sceneManager.setSkyBox(skybox);
    }

    private void initPersCamera() {
        perspectiveCamera = new PerspectiveCamera(Settings.FOV, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        perspectiveCamera.position.set(0, 0 ,0);
        perspectiveCamera.lookAt(0, 0, 0);
        perspectiveCamera.near = 0.01f;
        perspectiveCamera.far = 1000f;
        perspectiveCamera.update();
    }

    private void initModelBatch() {
        modelBatch = new ModelBatch();
    }

    private void addEntities() {
        createGround();
        createPlayer(0,0,0);
    }

    private void createPlayer(float x, float y, float z) {
        if (!isPlayerCreated) {
            character = EntityFactory.createPlayer(x, y, z);
            if (character == null) return;
            engine.addEntity(character);
            isPlayerCreated = true;
        }
    }

    private void createGround() {
        engine.addEntity(EntityFactory.createStaticEntity(groundModel, 0, 0, 0));
        engine.addEntity(EntityFactory.createStaticEntity(wallHorizontal, 0, 100, -200));
        engine.addEntity(EntityFactory.createStaticEntity(wallHorizontal, 0, 100, 200));
        engine.addEntity(EntityFactory.createStaticEntity(wallVertical, 200, 100, 0));
        engine.addEntity(EntityFactory.createStaticEntity(wallVertical, -200, 100, 0));
    }

    private void addSystems(GameUI gameUI) {
        engine = new Engine();
        engine.addSystem(new RenderSystem(sceneManager));
        engine.addSystem(bulletSystem = new BulletSystem());
        engine.addSystem(new PlayerSystem(this, gameUI, perspectiveCamera));
        engine.addSystem(new StatusSystem(this));
    }

    public void render(float delta) {
        if (!isPlayerCreated) {
            createPlayer(0,0,0);
        }
        renderWorld(delta);
        checkPause();
    }

    private void checkPause() {
        if (Settings.Paused) {
            engine.getSystem(PlayerSystem.class).setProcessing(false);
            engine.getSystem(StatusSystem.class).setProcessing(false);
            engine.getSystem(BulletSystem.class).setProcessing(false);
        } else {
            engine.getSystem(PlayerSystem.class).setProcessing(true);
            engine.getSystem(StatusSystem.class).setProcessing(true);
            engine.getSystem(BulletSystem.class).setProcessing(true);
        }
    }

    protected void renderWorld(float delta) {
        engine.update(delta);
    }

    public void resize(int width, int height) {
        sceneManager.updateViewport(width, height);
    }

    public void dispose() {
        bulletSystem.collisionWorld.removeAction(character.getComponent(CharacterComponent.class).characterController);
        bulletSystem.collisionWorld.removeCollisionObject(character.getComponent(CharacterComponent.class).ghostObject);
        bulletSystem.dispose();

        bulletSystem = null;

        wallHorizontal.dispose();
        wallVertical.dispose();
        groundModel.dispose();
        modelBatch.dispose();

        modelBatch = null;
        character.getComponent(CharacterComponent.class).characterController.dispose();
        character.getComponent(CharacterComponent.class).ghostObject.dispose();
        character.getComponent(CharacterComponent.class).ghostShape.dispose();
        sceneManager.dispose();
        environmentCubemap.dispose();
        diffuseCubemap.dispose();
        specularCubemap.dispose();
        skybox.dispose();
    }

    public void remove(Entity entity) {
        engine.removeEntity(entity);
        bulletSystem.removeBody(entity);
    }
}
