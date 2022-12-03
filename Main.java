package solitaer_mb;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Screen;
import javafx.stage.Stage;

/**
 *
 * @author Michael Bauer
 */
public class Main extends Application {
    public static Pane pane = new Pane();
    
    public static void main(String[] args) {
        
        launch();
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        pane.setPrefSize(Screen.getPrimary().getBounds().getWidth() * 0.9, Screen.getPrimary().getBounds().getHeight() * 0.9);
        
        Scene scene = new Scene(pane);
        
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        
        initialize();

        primaryStage.show();

    }
    
    public static void initialize() {
        double menuBarHeight = 20.0;
        
        MenuBar mb = new MenuBar();
        mb.setPrefSize(pane.getPrefWidth(), menuBarHeight);
        pane.getChildren().add(mb);
        
        Menu game = new Menu();
        game.setText("game");
        mb.getMenus().add(game);
        
        MenuItem start = new MenuItem();
        start.setText("new");
        game.getItems().add(start);
        start.setOnAction((event) -> {
            CardLayout.resetGame();
        });
        
        Menu help = new Menu();
        help.setText("help");
        mb.getMenus().add(help);
        
        MenuItem about = new MenuItem();
        about.setText("about");
        help.getItems().add(about);
        
        VBox playfield = new VBox();
        playfield.setPrefSize(pane.getPrefWidth(), pane.getPrefHeight() - menuBarHeight);
        playfield.setLayoutY(menuBarHeight);
        pane.getChildren().add(playfield);
        
        HBox upper = new HBox();
        upper.setPrefSize(pane.getPrefWidth(), pane.getPrefHeight() * 0.4);
        playfield.getChildren().add(upper);
        
        HBox lower = new HBox();
        lower.setPrefSize(pane.getPrefWidth(), pane.getPrefHeight() - upper.getPrefHeight());
        playfield.getChildren().add(lower);
        
        CardLayout.setCardLayouts(upper, lower);
        
    }
    
    public static Pane getPane() {
        return pane;
    }
}
