package ru.demmax93.systems;

import com.badlogic.ashley.core.*;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g3d.utils.AnimationController;
import com.badlogic.gdx.math.Vector3;
import ru.demmax93.GameWorld;
import ru.demmax93.Settings;
import ru.demmax93.components.CharacterComponent;
import ru.demmax93.components.ModelComponent;
import ru.demmax93.components.PlayerComponent;
import ru.demmax93.ui.GameUI;

public class PlayerSystem extends EntitySystem implements EntityListener {
    private Entity player;
    private PlayerComponent playerComponent;
    private CharacterComponent characterComponent;
    private ModelComponent modelComponent;
    public AnimationController animationController;
    private final GameUI gameUI;
    private final Vector3 tmp = new Vector3();
    private final Camera camera;
    private final GameWorld gameWorld;

    public PlayerSystem(GameWorld gameWorld, GameUI gameUI, Camera camera) {
        this.camera = camera;
        this.gameWorld = gameWorld;
        this.gameUI = gameUI;
    }

    @Override
    public void addedToEngine(Engine engine) {
        engine.addEntityListener(Family.all(PlayerComponent.class).get(), this);
    }

    @Override
    public void update(float delta) {
        if (player == null) return;
        if (animationController == null) animationController = new AnimationController(modelComponent.instance);
        updateMovement(delta);
        updateStatus();
        checkGameOver();
    }

    private void updateMovement(float delta) {
        animationController.paused = true;
        if (Gdx.input.isKeyPressed(Input.Keys.UP)) {
            animationController.animate("Root|Walk_loop",0.5f);
            modelComponent.instance.transform.translate(0, 0, 2);
            animationController.paused = false;
        }

        if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
            animationController.animate("Root|Walk_loop",0.5f);
            modelComponent.instance.transform.translate(0, 0, -2);
            animationController.paused = false;
        }

        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            animationController.animate("Root|Walk_loop",0.5f);
            modelComponent.instance.transform.rotate(0, modelComponent.instance.transform.getScaleY(), 0, 1);
            animationController.paused = false;
        }

        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            animationController.animate("Root|Walk_loop",0.5f);
            modelComponent.instance.transform.rotate(0, modelComponent.instance.transform.getScaleY(), 0, -1);
            animationController.paused = false;
        }

        camera.update();
        animationController.update(delta);
    }

    private void updateStatus() {
        gameUI.healthWidget.setValue(playerComponent.health);
    }

    private void checkGameOver() {
        if (playerComponent.health <= 0 && !Settings.Paused) {
            Settings.Paused = true;
            gameUI.gameOverWidget.gameOver();
        }
    }

    @Override
    public void entityAdded(Entity entity) {
        player = entity;
        playerComponent = entity.getComponent(PlayerComponent.class);
        characterComponent = entity.getComponent(CharacterComponent.class);
        modelComponent = entity.getComponent(ModelComponent.class);
    }

    @Override
    public void entityRemoved(Entity entity) {
    }
}
