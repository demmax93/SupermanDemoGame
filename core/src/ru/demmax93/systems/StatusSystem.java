package ru.demmax93.systems;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import ru.demmax93.GameWorld;
import ru.demmax93.components.StatusComponent;

public class StatusSystem extends EntitySystem {
    private ImmutableArray<Entity> entities;
    private final GameWorld gameWorld;

    public StatusSystem(GameWorld gameWorld) {
        this.gameWorld = gameWorld;
    }

    @Override
    public void addedToEngine(Engine engine) {
        entities = engine.getEntitiesFor(Family.all(StatusComponent.class).get());
    }

    @Override
    public void update(float deltaTime) {
        for (Entity entity : entities) {
            if (!entity.getComponent(StatusComponent.class).alive) {
                gameWorld.remove(entity);
            }
        }
    }
}
