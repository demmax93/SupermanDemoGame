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
    private AnimationController animationController;
    private final GameUI gameUI;
    private final Camera camera;
    private final GameWorld gameWorld;
    private final Vector3 currentPosition = new Vector3();
    private float cameraPitch = Settings.CAMERA_START_PITCH;
    private float angleAroundPlayer = 0f;

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
        updateCameraMovement();
        updateStatus();
        checkGameOver();
    }

    private void updateMovement(float delta) {
        boolean isMoveKeyPressed = false;
        boolean isPlayerRunning = Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT);
        float defaultTransitionTime = 0.5f;
        if (Gdx.input.isKeyPressed(Input.Keys.UP)) {
            animationController.animate(getMovingAnimation(isPlayerRunning), defaultTransitionTime);
            modelComponent.instance.transform.translate(0, 0, getMovingSpeed(isPlayerRunning));
            isMoveKeyPressed = true;
        }

        if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
            animationController.animate(getMovingAnimation(isPlayerRunning), defaultTransitionTime);
            modelComponent.instance.transform.translate(0, 0, -getMovingSpeed(isPlayerRunning));
            isMoveKeyPressed = true;
        }

        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            animationController.animate(getMovingAnimation(isPlayerRunning), defaultTransitionTime);
            modelComponent.instance.transform.rotate(Vector3.Y, getMovingSpeed(isPlayerRunning));
            angleAroundPlayer += getMovingSpeed(isPlayerRunning);
            isMoveKeyPressed = true;
        }

        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            animationController.animate(getMovingAnimation(isPlayerRunning), defaultTransitionTime);
            modelComponent.instance.transform.rotate(Vector3.Y, -getMovingSpeed(isPlayerRunning));
            angleAroundPlayer -= getMovingSpeed(isPlayerRunning);
            isMoveKeyPressed = true;
        }

        if (!isMoveKeyPressed) {
            animationController.animate("Root|Idle", defaultTransitionTime);
        }

        modelComponent.instance.transform.getTranslation(currentPosition);
        animationController.update(delta);
    }
    
    private String getMovingAnimation(boolean isPlayerRunning) {
        return isPlayerRunning ? "Root|Run_loop" : "Root|Walk_loop";
    }

    private float getMovingSpeed(boolean isPlayerRunning) {
        return isPlayerRunning ? 4f : 1f;
    }

    private void updateCameraMovement() {
        float horDistance = (float) (Settings.CAMERA_DISTANCE_FROM_PLAYER * Math.cos(Math.toRadians(cameraPitch)));
        float verDistance = (float) (Settings.CAMERA_DISTANCE_FROM_PLAYER * 2 * Math.sin(Math.toRadians(cameraPitch)));
        calculateCameraPitch();
        camera.position.set(
                currentPosition.x - (float)(horDistance * Math.sin(Math.toRadians(angleAroundPlayer))),
                currentPosition.y + verDistance,
                currentPosition.z - (float)(horDistance * Math.cos(Math.toRadians(angleAroundPlayer))));
        camera.lookAt(currentPosition.x, currentPosition.y + 100f, currentPosition.z);
        camera.up.set(Vector3.Y);
        camera.update();
    }

    private void calculateCameraPitch() {
        float pitchChange = -Gdx.input.getDeltaY() * Settings.CAMERA_PITCH_FACTOR;
        cameraPitch += pitchChange;

        if (cameraPitch < Settings.CAMERA_MIN_PITCH) {
            cameraPitch = Settings.CAMERA_MIN_PITCH;
        } else if (cameraPitch > Settings.CAMERA_MAX_PITCH) {
            cameraPitch = Settings.CAMERA_MAX_PITCH;
        }
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
        player = null;
        playerComponent = null;
        characterComponent = null;
        modelComponent = null;
    }
}
