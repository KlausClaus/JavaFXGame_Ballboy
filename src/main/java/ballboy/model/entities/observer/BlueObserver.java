package ballboy.model.entities.observer;

import ballboy.model.Level;
import javafx.application.Platform;
import javafx.scene.text.Text;

public class BlueObserver implements Observer{

    private final Level level;
    private final Text text;
    private int score;

    private Observer ob;

    public BlueObserver(Level level, Text text){
        this.level = level;
        this.text = text;
        this.level.addObserver(this);
    }

    @Override
    public void update() {
        this.score = level.getBlueScore();
        Platform.runLater(() -> this.text.setText("Blue Score: " + this.score));
//        this.inform();
    }
    public int getScore() {
        return this.score;
    }

    public void attach(Observer ob){
        this.ob = ob;
    }

    public void inform(){
        this.ob.update();
    }


}
