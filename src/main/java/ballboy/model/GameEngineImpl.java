package ballboy.model;

import java.util.List;

/**
 * Implementation of the GameEngine interface.
 * This provides a common interface for the entire game.
 */
public class GameEngineImpl implements GameEngine {
    private final List<Level> levels;
    private Level level;
    private int level_id;
    private boolean Game_finished;

    public GameEngineImpl(List<Level> levels, int currentLevel){
        this.levels = levels;
        this.level_id = currentLevel;
        this.level = levels.get(level_id);
        this.Game_finished = false;
    }

    public int getLevel_id(){
        return this.level_id;
    }

    public Level getCurrentLevel() {
        return level;
    }

    public void startLevel() {
        // TODO: Handle when multiple levels has been implemented
        return;
    }

    public boolean Game_Finished(){
        return this.Game_finished;
    }

    public boolean boostHeight() {
        return level.boostHeight();
    }

    public boolean dropHeight() {
        return level.dropHeight();
    }

    public boolean moveLeft() {
        return level.moveLeft();
    }

    public boolean moveRight() {
        return level.moveRight();
    }

    public void tick() {
        level.update();

        // when level finishes, we won't exit, but mark level finished to be true
        // if level finished, add 1 on level id
        if(level.is_levelfinish()){
            this.level_id ++;
            // if after addition, level id smaller than level size,
            // it means we still in the list of level
            if(this.level_id < this.levels.size()){
                this.level = this.levels.get(level_id);
                this.level.informAll();
            }else{
                // otherwise, game has finsihed.
                this.Game_finished = true;
            }
        }
    }


    @Override
    public void red_cheat() {
        this.level.r_cheat();
    }

    @Override
    public void blue_cheat() {
        this.level.b_cheat();
    }

    @Override
    public void green_cheat() {
        this.level.g_cheat();
    }
}