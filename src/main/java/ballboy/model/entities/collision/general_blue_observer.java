package ballboy.model.entities.observer;

import ballboy.model.Level;
import javafx.application.Platform;
import javafx.scene.text.Text;

import java.util.List;

public class general_blue_observer implements Observer{

    private final List<BlueObserver> subjects;
    private final Text text;

    public general_blue_observer(List<BlueObserver> ob, Text text){
        this.subjects = ob;
        for(BlueObserver i: ob){
            i.attach(this);
        }
        this.text = text;
    }

    @Override
    public void update() {
        int totalScore = 0;
        for(BlueObserver i: subjects){
            totalScore += i.getScore();
        }
        int finalScore = totalScore;
        Platform.runLater(() -> this.text.setText("TotalScore: " + finalScore));
    }
}
