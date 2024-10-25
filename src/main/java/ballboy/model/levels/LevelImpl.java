package ballboy.model.levels;

import ballboy.ConfigurationParseException;
import ballboy.model.Entity;
import ballboy.model.Level;
import ballboy.model.entities.ControllableDynamicEntity;
import ballboy.model.entities.DynamicEntity;
import ballboy.model.entities.StaticEntity;
import ballboy.model.entities.observer.*;
import ballboy.model.entities.utilities.Vector2D;
import ballboy.model.factories.EntityFactory;
import javafx.application.Platform;
import javafx.scene.paint.Color;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.awt.*;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Queue;
import java.util.stream.Collectors;

/**
 * Level logic, with abstract factor methods.
 */
public class LevelImpl implements Level {

    private final List<Entity> entities = new ArrayList<>();
    private final PhysicsEngine engine;
    private final EntityFactory entityFactory;
    private ControllableDynamicEntity<DynamicEntity> hero;
    private Entity finish;
    private double levelHeight;
    private double levelWidth;
    private double levelGravity;
    private double floorHeight;
    private Color floorColor;
    private boolean r_cheat = false;
    private boolean g_cheat = false;
    private boolean b_cheat = false;
    private boolean finished = false;
    private Entity squareCat;
    private int red_score = 0;
    private int blue_score = 0;
    private int green_score = 0;
    private List<Observer> observers = new ArrayList<>();

    private final double frameDurationMilli;

    /**
     * A callback queue for post-update jobs. This is specifically useful for scheduling jobs mid-update
     * that require the level to be in a valid state.
     */
    private final Queue<Runnable> afterUpdateJobQueue = new ArrayDeque<>();

    public LevelImpl(
            JSONObject levelConfiguration,
            PhysicsEngine engine,
            EntityFactory entityFactory,
            double frameDurationMilli) {
        this.engine = engine;
        this.entityFactory = entityFactory;
        this.frameDurationMilli = frameDurationMilli;
        initLevel(levelConfiguration);
    }

    @Override
    public void plus_red_score(int i){
        this.red_score += i;
        this.informAll();
    }

    @Override
    public void plus_green_score(int i){
        this.green_score += i;
        this.informAll();
    }

    @Override
    public void plus_blue_score(int i){
        this.blue_score += i;
        this.informAll();
    }

    /**
     * Instantiates a level from the level configuration.
     *
     * @param levelConfiguration The configuration for the level.
     */
    private void initLevel(JSONObject levelConfiguration) {
        this.levelWidth = ((Number) levelConfiguration.get("levelWidth")).doubleValue();
        this.levelHeight = ((Number) levelConfiguration.get("levelHeight")).doubleValue();
        this.levelGravity = ((Number) levelConfiguration.get("levelGravity")).doubleValue();

        JSONObject floorJson = (JSONObject) levelConfiguration.get("floor");
        this.floorHeight = ((Number) floorJson.get("height")).doubleValue();
        String floorColorWeb = (String) floorJson.get("color");
        this.floorColor = Color.web(floorColorWeb);

        JSONArray generalEntities = (JSONArray) levelConfiguration.get("genericEntities");
        for (Object o : generalEntities) {
            this.entities.add(entityFactory.createEntity(this, (JSONObject) o));
        }

        JSONObject heroConfig = (JSONObject) levelConfiguration.get("hero");
        double maxVelX = ((Number) levelConfiguration.get("maxHeroVelocityX")).doubleValue();

        Object hero = entityFactory.createEntity(this, heroConfig);
        if (!(hero instanceof DynamicEntity)) {
            throw new ConfigurationParseException("hero must be a dynamic entity");
        }
        DynamicEntity dynamicHero = (DynamicEntity) hero;
        Vector2D heroStartingPosition = dynamicHero.getPosition();
        this.hero = new ControllableDynamicEntity<>(dynamicHero, heroStartingPosition, maxVelX, floorHeight,
                levelGravity);
        this.entities.add(this.hero);

       JSONObject PetConfig = (JSONObject) levelConfiguration.get("cat");
       this.squareCat = entityFactory.createEntity(this, PetConfig);
       this.entities.add(this.squareCat);

        JSONObject finishConfig = (JSONObject) levelConfiguration.get("finish");
        this.finish = entityFactory.createEntity(this, finishConfig);
        this.entities.add(finish);


    }

    @Override
    public List<Entity> getEntities() {
        return Collections.unmodifiableList(entities);
    }

    private List<DynamicEntity> getDynamicEntities() {
        return entities.stream().filter(e -> e instanceof DynamicEntity).map(e -> (DynamicEntity) e).collect(
                Collectors.toList());
    }

    private List<StaticEntity> getStaticEntities() {
        return entities.stream().filter(e -> e instanceof StaticEntity).map(e -> (StaticEntity) e).collect(
                Collectors.toList());
    }

    @Override
    public double getLevelHeight() {
        return this.levelHeight;
    }

    @Override
    public double getLevelWidth() {
        return this.levelWidth;
    }

    @Override
    public double getHeroHeight() {
        return hero.getHeight();
    }

    @Override
    public double getHeroWidth() {
        return hero.getWidth();
    }

    @Override
    public double getFloorHeight() {
        return floorHeight;
    }

    @Override
    public Color getFloorColor() {
        return floorColor;
    }

    @Override
    public double getGravity() {
        return levelGravity;
    }

    @Override
    public void update() {
        List<DynamicEntity> dynamicEntities = getDynamicEntities();

        dynamicEntities.stream().forEach(e -> {
            e.update(frameDurationMilli, levelGravity);
        });

        for (int i = 0; i < dynamicEntities.size(); ++i) {
            DynamicEntity dynamicEntityA = dynamicEntities.get(i);

            for (int j = i + 1; j < dynamicEntities.size(); ++j) {
                DynamicEntity dynamicEntityB = dynamicEntities.get(j);

                if (dynamicEntityA.collidesWith(dynamicEntityB)) {
                    dynamicEntityA.collideWith(dynamicEntityB);
                    dynamicEntityB.collideWith(dynamicEntityA);
                    if (!isHero(dynamicEntityA) && !isHero(dynamicEntityB)) {
                        engine.resolveCollision(dynamicEntityA, dynamicEntityB);
                    }
                }
            }

            //if the entity is square cat, I won't let it collide with any Static Entity
//            I need to check if the Dynamic Entity collided to the static entity is square cat or not,
//            if it is not square cat, the collision will happen,
//            if it is square cat, just ignore it.
            if(dynamicEntityA != this.squareCat) {
                for (StaticEntity staticEntity : getStaticEntities()) {
                    if (dynamicEntityA.collidesWith(staticEntity)) {
                        dynamicEntityA.collideWith(staticEntity);
                        engine.resolveCollision(dynamicEntityA, staticEntity, this);
                    }
                }
            }
        }
        // remove square cat from dynamic eneity,
        // in order to make square cat won't enforce with world limit
        dynamicEntities.remove((this.squareCat));
        // square cat won't collide with floor.
        dynamicEntities.stream().forEach(e -> engine.enforceWorldLimits(e, this));

        afterUpdateJobQueue.forEach(j -> j.run());
        afterUpdateJobQueue.clear();

    }

    @Override
    public double getHeroX() {
        return hero.getPosition().getX();
    }

    @Override
    public double getHeroY() {
        return hero.getPosition().getY();
    }

    @Override
    public boolean boostHeight() {
        return hero.boostHeight();
    }

    @Override
    public boolean dropHeight() {
        return hero.dropHeight();
    }

    @Override
    public boolean moveLeft() {
        return hero.moveLeft();
    }

    @Override
    public boolean moveRight() {
        return hero.moveRight();
    }

    @Override
    public boolean isHero(Entity entity) {
        return entity == hero;
    }

    @Override
    public boolean isFinish(Entity entity) {
        return this.finish == entity;
    }

    @Override
    public void resetHero() {
        afterUpdateJobQueue.add(() -> this.hero.reset());
    }

    @Override
    public void finish() {
//        Platform.exit();
        this.finished = true;
    }

    @Override
    public void destroy(Entity something){
        this.entities.remove(something);
    }

    @Override
    public boolean is_levelfinish(){
        return this.finished;
    }

    @Override
    public void addObserver(Observer o){
        this.observers.add(o);
    }

    @Override
    public void informAll() {
        for(Observer i: this.observers){
            i.update();
        }
    }

    @Override
    public int getRedScore() {
        return this.red_score;
    }

    @Override
    public int getGreenScore() {
        return this.green_score;
    }

    @Override
    public int getBlueScore() {
        return this.blue_score;
    }

    @Override
    public void r_cheat() {
        this.r_cheat = !this.r_cheat;
    }

    @Override
    public void b_cheat() {
        this.b_cheat = !this.b_cheat;
    }

    @Override
    public void g_cheat() {
        this.g_cheat = !this.g_cheat;
    }

    @Override
    public boolean isr_cheating() {
        return r_cheat;
    }

    @Override
    public boolean isg_cheating() {
        return g_cheat;
    }

    @Override
    public boolean isb_cheating() {
        return b_cheat;
    }


}
