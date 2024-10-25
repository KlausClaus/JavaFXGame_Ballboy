package ballboy.model.factories;

import ballboy.ConfigurationParseException;
import ballboy.model.Entity;
import ballboy.model.Level;
import ballboy.model.entities.DynamicEntity;
import ballboy.model.entities.DynamicEntityImpl;
import ballboy.model.entities.behaviour.FloatingCloudBehaviourStrategy;
import ballboy.model.entities.behaviour.SquareCatBehaviourStrategy;
import ballboy.model.entities.collision.PassiveCollisionStrategy;
import ballboy.model.entities.collision.SquareCatCollisionStrategy;
import ballboy.model.entities.utilities.*;
import javafx.scene.image.Image;
import org.json.simple.JSONObject;

import java.awt.*;

public class SquareCatFactory implements EntityFactory {

    @Override
    public Entity createEntity(Level level, JSONObject config) {
        DynamicEntity ballboy = null;
        String imageName = (String) config.get("image");
        double height = ((Number) config.get("size")).doubleValue();
        double width = ((Number) config.get("size")).doubleValue();
        double speed = ((Number) config.get("speed")).doubleValue();
        double radius = ((Number) config.get("walk_radius")).doubleValue();

        for(Entity i: level.getEntities()){
            if(level.isHero(i)){
                ballboy = (DynamicEntity) i;
                break;
            }
        }
        double startX = 0;
        double startY = 0;

        Vector2D startingPosition = new Vector2D(startX, startY);

        KinematicState kinematicState = new KinematicStateImpl.KinematicStateBuilder()
                .setPosition(startingPosition)
                .build();


        AxisAlignedBoundingBox volume = new AxisAlignedBoundingBoxImpl(
                startingPosition,
                height,
                width
        );

        return new DynamicEntityImpl(
                kinematicState,
                volume,
                Entity.Layer.EFFECT,
                new Image(imageName),
                new SquareCatCollisionStrategy(level),
                new SquareCatBehaviourStrategy(ballboy, speed, radius),
                null
        );

    }
}
