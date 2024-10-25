package ballboy.model.entities.observer;

import ballboy.model.Level;
import javafx.application.Platform;
import javafx.scene.text.Text;

import java.util.List;

public class general_green_observer implements Observer{

    private final List<GreenObserver> subjects;
    private final Text text;

    public general_green_observer(List<GreenObserver> ob, Text text){
        this.subjects = ob;
        for(GreenObserver i: ob){
            i.attach(this);
        }
        this.text = text;

    }

    @Override
    public void update() {
        int totalScore = 0;
        for(GreenObserver i: subjects){
            totalScore += i.getScore();
        }
        int finalScore = totalScore;
        Platform.runLater(() -> this.text.setText("TotalScore: " + finalScore));
    }
}
