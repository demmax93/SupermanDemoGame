package ru.demmax93.systems;

import com.badlogic.ashley.core.*;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.collision.*;
import com.badlogic.gdx.physics.bullet.dynamics.btConstraintSolver;
import com.badlogic.gdx.physics.bullet.dynamics.btDiscreteDynamicsWorld;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody;
import com.badlogic.gdx.physics.bullet.dynamics.btSequentialImpulseConstraintSolver;
import ru.demmax93.components.BulletComponent;
import ru.demmax93.components.CharacterComponent;

public class BulletSystem extends EntitySystem implements EntityListener {
    public final btCollisionConfiguration collisionConfiguration;
    public final btCollisionDispatcher dispatcher;
    public final btBroadphaseInterface broadphase;
    public final btConstraintSolver solver;
    public final btDiscreteDynamicsWorld collisionWorld;
    private final btGhostPairCallback ghostPairCallback;

    @Override
    public void addedToEngine(Engine engine) {
        engine.addEntityListener(Family.all(BulletComponent.class).get(), this);
        engine.addEntityListener(Family.all(CharacterComponent.class).get(), this);
    }

    public BulletSystem() {
        collisionConfiguration = new btDefaultCollisionConfiguration();
        dispatcher = new btCollisionDispatcher(collisionConfiguration);
        broadphase = new btAxisSweep3(new Vector3(-1000, -1000, -1000), new Vector3(1000, 1000, 1000));
        solver = new btSequentialImpulseConstraintSolver();
        collisionWorld = new btDiscreteDynamicsWorld(dispatcher, broadphase, solver, collisionConfiguration);
        ghostPairCallback = new btGhostPairCallback();
        broadphase.getOverlappingPairCache().setInternalGhostPairCallback(ghostPairCallback);
        this.collisionWorld.setGravity(new Vector3(0, -0.5f, 0));
        collisionWorld.applyGravity();
    }

    @Override
    public void update(float deltaTime) {
        collisionWorld.stepSimulation(deltaTime);
    }

    public void dispose() {
        collisionWorld.dispose();
        if (solver != null) solver.dispose();
        if (broadphase != null) broadphase.dispose();
        if (dispatcher != null) dispatcher.dispose();
        if (collisionConfiguration != null) collisionConfiguration.dispose();
        ghostPairCallback.dispose();
    }

    @Override
    public void entityAdded(Entity entity) {
        BulletComponent bulletComponent = entity.getComponent(BulletComponent.class);
        if (bulletComponent != null) {
            collisionWorld.addRigidBody((btRigidBody) bulletComponent.body);
            collisionWorld.addCollisionObject(bulletComponent.body,
                    (short) btBroadphaseProxy.CollisionFilterGroups.StaticFilter,
                    (short) (btBroadphaseProxy.CollisionFilterGroups.AllFilter));
        }
        CharacterComponent characterComponent = entity.getComponent(CharacterComponent.class);
        if (characterComponent != null) {
            collisionWorld.addCharacter(characterComponent.characterController);
            collisionWorld.addCollisionObject(characterComponent.ghostObject,
                    (short) btBroadphaseProxy.CollisionFilterGroups.CharacterFilter,
                    (short) (btBroadphaseProxy.CollisionFilterGroups.AllFilter));
            collisionWorld.addAction(characterComponent.characterController);
        }
    }

    public void removeBody(Entity entity) {
        BulletComponent comp = entity.getComponent(BulletComponent.class);
        if (comp != null)
            collisionWorld.removeCollisionObject(comp.body);
        CharacterComponent character = entity.getComponent(CharacterComponent.class);
        if (character != null) {
            collisionWorld.removeAction(character.characterController);
            collisionWorld.removeCollisionObject(character.ghostObject);
        }
    }

    @Override
    public void entityRemoved(Entity entity) {
        removeBody(entity);
    }
}
