package ru.demmax93;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.FloatAttribute;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.Bullet;
import ru.demmax93.components.CharacterComponent;
import ru.demmax93.managers.EntityFactory;
import ru.demmax93.systems.BulletSystem;
import ru.demmax93.systems.PlayerSystem;
import ru.demmax93.systems.RenderSystem;
import ru.demmax93.systems.StatusSystem;
import ru.demmax93.ui.GameUI;

public class GameWorld {
    private static final float FOV = 67F;
    private ModelBatch modelBatch;
    private Environment environment;
    private PerspectiveCamera perspectiveCamera;

    private Engine engine;
    private Entity character;
    public BulletSystem bulletSystem;
    public ModelBuilder modelBuilder = new ModelBuilder();
    private boolean isPlayerCreated = false;
    private final Vector3 startPos = new Vector3(2f, 200f, -180f);

    Model wallHorizontal = modelBuilder.createBox(40, 20, 1,
            new Material(ColorAttribute.createDiffuse(Color.WHITE), ColorAttribute.createSpecular(Color.RED), FloatAttribute
                    .createShininess(16f)), VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);
    Model wallVertical = modelBuilder.createBox(1, 20, 40,
            new Material(ColorAttribute.createDiffuse(Color.GREEN), ColorAttribute.createSpecular(Color.WHITE), FloatAttribute
                    .createShininess(16f)), VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);
    Model groundModel = modelBuilder.createBox(40, 1, 40,
            new Material(ColorAttribute.createDiffuse(Color.YELLOW), ColorAttribute.createSpecular(Color.BLUE), FloatAttribute
                    .createShininess(16f)), VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);

    public GameWorld(GameUI gameUI) {
        Bullet.init();
        initEnvironment();
        initModelBatch();
        initPersCamera();
        addSystems(gameUI);
        createGround();
        addEntities();
    }

    private void initEnvironment() {
        environment = new Environment();
        environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.3f, 0.3f, 0.3f, 1.f));
    }

    private void initPersCamera() {
        perspectiveCamera = new PerspectiveCamera(FOV, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        perspectiveCamera.position.set(startPos);
        perspectiveCamera.lookAt(0, 100f, 0);
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
            character = EntityFactory.createPlayer(bulletSystem, x, y, z);
            if (character == null) return;
            engine.addEntity(character);
            isPlayerCreated = true;
        }
    }

    private void createGround() {
        engine.addEntity(EntityFactory.createStaticEntity(groundModel, 0, 0, 0));
        engine.addEntity(EntityFactory.createStaticEntity(wallHorizontal, 0, 10, -20));
        engine.addEntity(EntityFactory.createStaticEntity(wallHorizontal, 0, 10, 20));
        engine.addEntity(EntityFactory.createStaticEntity(wallVertical, 20, 10, 0));
        engine.addEntity(EntityFactory.createStaticEntity(wallVertical, -20, 10, 0));
    }

    private void addSystems(GameUI gameUI) {
        engine = new Engine();
        engine.addSystem(new RenderSystem(modelBatch, environment));
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
        modelBatch.begin(perspectiveCamera);
        engine.update(delta);
        modelBatch.end();
    }

    public void resize(int width, int height) {
        perspectiveCamera.viewportHeight = height;
        perspectiveCamera.viewportWidth = width;
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
    }

    public void remove(Entity entity) {
        engine.removeEntity(entity);
        bulletSystem.removeBody(entity);
    }
}
