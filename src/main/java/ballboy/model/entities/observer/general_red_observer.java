package ballboy.model.entities.observer;

import ballboy.model.Level;
import javafx.application.Platform;
import javafx.scene.text.Text;

import java.util.List;

public class general_red_observer implements Observer{

    private final List<RedObserver> subjects;
    private final Text text;

    public general_red_observer( List<RedObserver> ob, Text text){
        this.subjects = ob;
        for(RedObserver i: ob){
            i.attach(this);
        }
        this.text = text;
    }

    @Override
    public void update() {
        int totalScore = 0;
        for(RedObserver i: subjects){
            totalScore += i.getScore();
        }
        int finalScore = totalScore;
        Platform.runLater(() -> this.text.setText("TotalScore: " + finalScore));
    }
}
