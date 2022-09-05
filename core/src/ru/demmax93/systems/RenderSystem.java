package ru.demmax93.systems;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import ru.demmax93.components.ModelComponent;

public class RenderSystem extends EntitySystem {
    private ImmutableArray<Entity> entities;
    private final ModelBatch batch;
    private final Environment environment;
    private final PerspectiveCamera camera;

    public RenderSystem(ModelBatch batch, Environment environment, PerspectiveCamera perspectiveCamera) {
        this.batch = batch;
        this.environment = environment;
        this.camera = perspectiveCamera;
    }

    // Event called when an entity is added to the engine
    public void addedToEngine(Engine e) {
        // Grabs all entities with desired components
        entities = e.getEntitiesFor(Family.all(ModelComponent.class).get());
    }

    public void update(float delta) {
        batch.begin(camera);
        for (int i = 0; i < entities.size(); i++) {
            ModelComponent mod = entities.get(i).getComponent(ModelComponent.class);
            batch.render(mod.instance, environment);
        }
        batch.end();
    }
}
