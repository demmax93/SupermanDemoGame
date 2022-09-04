package ru.demmax93.managers;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.physics.bullet.collision.*;
import com.badlogic.gdx.physics.bullet.dynamics.btKinematicCharacterController;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody;
import ru.demmax93.Assets;
import ru.demmax93.bullet.MotionState;
import ru.demmax93.components.BulletComponent;
import ru.demmax93.components.CharacterComponent;
import ru.demmax93.components.ModelComponent;
import ru.demmax93.components.PlayerComponent;

public class EntityFactory {

    public static Entity createStaticEntity(Model model, float x, float y, float z) {
        Entity entity = new Entity();
        ModelComponent modelComponent = new ModelComponent(model, x, y, z);
        entity.add(modelComponent);
        BulletComponent bulletComponent = createBulletComponentByModel(modelComponent, entity);
        entity.add(bulletComponent);
        return entity;
    }

    public static BulletComponent createBulletComponentByModel(ModelComponent modelComponent, Entity entity) {
        BoundingBox boundingBox = new BoundingBox();
        modelComponent.model.calculateBoundingBox(boundingBox);
        btCollisionShape col = new btBoxShape(new Vector3(boundingBox.getWidth() * 0.5f, boundingBox.getHeight() * 0.5f, boundingBox.getDepth() * 0.5f));
        BulletComponent bulletComponent = new BulletComponent();
        bulletComponent.bodyInfo = new btRigidBody.btRigidBodyConstructionInfo(0, null, col, Vector3.Zero);
        bulletComponent.body = new btRigidBody(bulletComponent.bodyInfo);
        bulletComponent.body.userData = entity;
        bulletComponent.motionState = new MotionState(modelComponent.instance.transform);
        ((btRigidBody) bulletComponent.body).setMotionState(bulletComponent.motionState);
        return bulletComponent;
    }

    public static CharacterComponent createCharacterComponentByModel(ModelComponent modelComponent, Entity entity) {
        CharacterComponent characterComponent = new CharacterComponent();
        characterComponent.ghostObject = new btPairCachingGhostObject();
        characterComponent.ghostObject.setWorldTransform(modelComponent.instance.transform);
        characterComponent.ghostShape = new btCapsuleShape(2f, 2f);
        characterComponent.ghostObject.setCollisionShape(characterComponent.ghostShape);
        characterComponent.ghostObject.setCollisionFlags(btCollisionObject.CollisionFlags.CF_CHARACTER_OBJECT);
        characterComponent.characterController = new btKinematicCharacterController(characterComponent.ghostObject, characterComponent.ghostShape, .35f);
        characterComponent.ghostObject.userData = entity;
        return characterComponent;
    }

    public static Entity createCharacter(String pathToModel, float x, float y, float z) {
        Model playerModel = Assets.getModel(pathToModel);
        if (playerModel == null) return null;
        Entity entity = new Entity();
        ModelComponent modelComponent = new ModelComponent(playerModel, x, y, z);
        entity.add(modelComponent);
        CharacterComponent characterComponent = createCharacterComponentByModel(modelComponent, entity);
        entity.add(characterComponent);
        BulletComponent bulletComponent = createBulletComponentByModel(modelComponent, entity);
        entity.add(bulletComponent);
        return entity;
    }

    public static Entity createPlayer(float x, float y, float z) {
        Entity entity = createCharacter("BaseMesh_Anim.g3dj", x, y, z);
        if (entity == null) return null;
        entity.add(new PlayerComponent());
        return entity;
    }
}
