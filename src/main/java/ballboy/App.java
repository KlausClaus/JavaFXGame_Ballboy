package ballboy;

import ballboy.model.Entity;
import ballboy.model.GameEngine;
import ballboy.model.GameEngineImpl;
import ballboy.model.Level;
import ballboy.model.entities.observer.*;
import ballboy.model.entities.observer.*;
import ballboy.model.entities.observer.general_blue_observer;
import ballboy.model.factories.*;
import ballboy.model.levels.LevelImpl;
import ballboy.model.levels.PhysicsEngine;
import ballboy.model.levels.PhysicsEngineImpl;
import ballboy.view.GameWindow;
import javafx.application.Application;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*
 * Application root.
 *
 * Wiring of the dependency graph should be done manually in the start method.
 */
public class App extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        Map<String, String> params = getParameters().getNamed();

        String s = "Java 11 sanity check";
        if (s.isBlank()) {
            throw new IllegalStateException("You must be running Java 11+. You won't ever see this exception though" +
                    " as your code will fail to compile on Java 10 and below.");
        }

        ConfigurationParser configuration = new ConfigurationParser();
        JSONObject parsedConfiguration = null;
        try {
            parsedConfiguration = configuration.parseConfig("config.json");
        } catch (ConfigurationParseException e) {
            System.out.println(e);
            System.exit(-1);
        }

        final double frameDurationMilli = 17;
        PhysicsEngine engine = new PhysicsEngineImpl(frameDurationMilli);

        EntityFactoryRegistry entityFactoryRegistry = new EntityFactoryRegistry();
        entityFactoryRegistry.registerFactory("cloud", new CloudFactory());
        entityFactoryRegistry.registerFactory("enemy", new EnemyFactory());
        entityFactoryRegistry.registerFactory("background", new StaticEntityFactory(Entity.Layer.BACKGROUND));
        entityFactoryRegistry.registerFactory("static", new StaticEntityFactory(Entity.Layer.FOREGROUND));
        entityFactoryRegistry.registerFactory("finish", new FinishFactory());
        entityFactoryRegistry.registerFactory("hero", new BallboyFactory());
        entityFactoryRegistry.registerFactory("cat", new SquareCatFactory());


        int levelIndex = ((Number) parsedConfiguration.get("currentLevelIndex")).intValue();
        JSONArray levelConfigs = (JSONArray) parsedConfiguration.get("levels");

        Text RedScore = new Text(50, 50, "RedScore: 0");
        Text GreenScore = new Text(50, 75, "GreenScore: 0");
        Text BlueScore = new Text(50, 100, "BlueScore: 0");
        Text AllRed_Score = new Text(50, 125, "Total Red Score: 0");
        Text AllGreen_Score = new Text(50, 150, "Total Green Score: 0");
        Text AllBlue_Score = new Text(50, 175, "Total Blue Score: 0");

        List<RedObserver> RedObserver = new ArrayList<>();
        List<GreenObserver> GreenObserver = new ArrayList<>();
        List<BlueObserver> BlueObserver = new ArrayList<>();
        List<Level> levels = new ArrayList<>();
        for(Object config: levelConfigs){
            Level level = new LevelImpl((JSONObject) config, engine, entityFactoryRegistry, frameDurationMilli);
            levels.add(level);

            RedObserver.add(new RedObserver(level,RedScore));
            GreenObserver.add(new GreenObserver(level,GreenScore));
            BlueObserver.add(new BlueObserver(level,BlueScore));

        }

        Observer globalObserver_r = new general_red_observer(RedObserver, AllRed_Score);
        Observer globalObserver_g = new general_green_observer(GreenObserver, AllGreen_Score);
        Observer globalObserver_b = new general_blue_observer(BlueObserver, AllBlue_Score);


        GameEngine gameEngine = new GameEngineImpl(levels, levelIndex);

//        Text levelid = new Text(50, 25, "Current Level: " + String.valueOf(gameEngine.getLevel_id()));
        GameWindow window = new GameWindow(gameEngine, 640, 400, frameDurationMilli);
        window.getPane().getChildren().add(RedScore);
        window.getPane().getChildren().add(GreenScore);
        window.getPane().getChildren().add(BlueScore);
//        window.getPane().getChildren().add(AllRed_Score);
//        window.getPane().getChildren().add(AllGreen_Score);
//        window.getPane().getChildren().add(AllBlue_Score);
        window.run();

        primaryStage.setTitle("Ballboy");
        primaryStage.setScene(window.getScene());
        primaryStage.setResizable(false);
        primaryStage.show();

        window.run();
    }
}
