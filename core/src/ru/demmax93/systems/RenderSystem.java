package ru.demmax93.systems;

import com.badlogic.ashley.core.*;
import net.mgsx.gltf.scene3d.scene.SceneManager;
import ru.demmax93.components.ModelComponent;

public class RenderSystem extends EntitySystem implements EntityListener {
    private final SceneManager sceneManager;

    public RenderSystem(SceneManager sceneManager) {
        this.sceneManager = sceneManager;
    }

    public void addedToEngine(Engine engine) {
        engine.addEntityListener(Family.all(ModelComponent.class).get(), this);
    }

    @Override
    public void entityAdded(Entity entity) {
        ModelComponent component = entity.getComponent(ModelComponent.class);
        sceneManager.addScene(component.scene);
    }

    @Override
    public void entityRemoved(Entity entity) {
        ModelComponent component = entity.getComponent(ModelComponent.class);
        sceneManager.removeScene(component.scene);
    }

    public void update(float delta) {
        sceneManager.update(delta);
        sceneManager.render();
    }
}
