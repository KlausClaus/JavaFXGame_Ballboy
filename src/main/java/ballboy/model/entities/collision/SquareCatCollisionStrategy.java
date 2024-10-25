package ballboy.model.entities.collision;

import ballboy.model.Entity;
import ballboy.model.Level;
import ballboy.model.entities.DynamicEntity;

import java.util.Locale;

public class SquareCatCollisionStrategy implements CollisionStrategy {
    private final Level level;

    public SquareCatCollisionStrategy(Level level) {
        this.level = level;
    }
    @Override
    public void collideWith(Entity enemy, Entity hitEntity) {
//        only Ballboy, Finish and Enemy are FOREGROUND Entity.
//        We also have isHero and isFinish function in Level
//        to identify whether the input entity is hero or finish.
        if(!level.isHero(hitEntity)
                && !level.isFinish(hitEntity)
                && hitEntity.getLayer() == Entity.Layer.FOREGROUND){
//            if the hitEntity is not hero or Finish, and it belongs to FOREGROUND,
//            then it is enemy, so remove that entity

            if(hitEntity instanceof DynamicEntity){
                if(((DynamicEntity) hitEntity).getcolor().toUpperCase(Locale.ROOT).equals("BLUE")){
                    this.level.plus_blue_score(1000);
//                    System.out.println("blue score: " + level.getBlueScore());
                }

                if(((DynamicEntity) hitEntity).getcolor().toUpperCase(Locale.ROOT).equals("RED")){
                    this.level.plus_red_score(1000);
//                    System.out.println("red score: " + level.getRedScore());
                }

                if(((DynamicEntity) hitEntity).getcolor().toUpperCase(Locale.ROOT).equals("GREEN")){
                    this.level.plus_green_score(1000);
//                    System.out.println("green score: " + level.getGreenScore());
                }

            }
            this.level.destroy(hitEntity);
            // plus score on the current level


        }

    }
}