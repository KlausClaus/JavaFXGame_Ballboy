package ballboy.model.entities.behaviour;

import ballboy.model.entities.DynamicEntity;
import ballboy.model.entities.utilities.Vector2D;

public class SquareCatBehaviourStrategy implements BehaviourStrategy {
    private final DynamicEntity ballboy;
    private final double speed;
    private final double radius;
    private final double side_buffer;
//    to calculate how long has square cat walked through this side
    private double walk_thorough;

    //identify which side we are going through now. 0 = top, 1 = left, 2 = bottom, 3 = right
    private int side_count;
//    private double hero_x;
//    private double hero_y;

    public SquareCatBehaviourStrategy(DynamicEntity ballboy, double speed, double radius){
        this.ballboy = ballboy;

        this.radius = radius;
        //since each side will be two times of its walking radius,
        //so I just make it 2*radius
        this.side_buffer = 2*radius;
        // inside the json file, we defined the speed to be
        // "how many seconds do we need to travel one cycle"
        // so we need to use the perimeter / the time it takes for one cycle
        // to get the distance it travels in one second.
        this.speed = 4*this.side_buffer/speed;
        //we make the square cat to start moving from top of ballboy
        //side count = 0 means start from top.
        this.side_count = 0;
//        if walk_through = side_buffer,
//        it means this side has been walked completely,
//        we should get turn into next side of walk path.
        this.walk_thorough = 0;


    }
    @Override
    public void behave(DynamicEntity entity, double frameDurationMilli) {
        double hero_x = this.ballboy.getPosition().getX() + 2/ this.ballboy.getHeight();
        double hero_y = this.ballboy.getPosition().getY() + 2/ this.ballboy.getWidth();

        //the distance that cat walk through will be 1500/speed per frame.
        this.walk_thorough += this.speed/1500 * frameDurationMilli;

        // when the cat went through the current edge.
        while(walk_thorough > side_buffer){
            // make the distance it passed to declined by the side length it just went through
            walk_thorough -= side_buffer;
//            make change on the side_count,
//            if it previously on the top, make it go to left;
            if(this.side_count == 0){
                this.side_count = 1;
                break;
            }
//            if it previously on the left, make it go to bottom;
            if(this.side_count == 1){
                this.side_count = 2;
                break;
            }
//            if it is on the bottom, make it go to right.
            if(this.side_count == 2){
                this.side_count = 3;
                break;
            }
//            if it previously on the right, make it go top.
            if(this.side_count == 3){
                this.side_count = 0;
                break;
            }

        }

        if(this.side_count == 0){
            entity.setPosition(new Vector2D(
                    hero_x + this.radius - this.walk_thorough,
                    hero_y  - this.radius
            ));
        }

        if(this.side_count == 1){
            entity.setPosition(new Vector2D(
                    hero_x - this.radius,
                    hero_y - this.radius + this.walk_thorough
            ));
        }

        if(this.side_count == 2){
            entity.setPosition(new Vector2D(
                    hero_x - this.radius + this.walk_thorough,
                    hero_y + this.radius
            ));
        }

        if(this.side_count == 3){
            entity.setPosition(new Vector2D(
                    hero_x+ this.radius,
                    hero_y + this.radius - this.walk_thorough
            ));
        }
    }
}

