package solitaer_mb;

import java.util.ArrayList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

/**
 *
 * @author Michael Bauer
 */
public class CardLayout {
    private static final int CARD_COUNT = 7;
    private static double cardWidth;
    private static double cardHeight;
    
    private static double gap;
    private static double offset = 30.0;
    
    public static final ArrayList<CardStack> STACKS = new ArrayList();
    
    public static double getOffset() {
        return offset;
    }
    
    public static void setCardLayouts(HBox upper, HBox lower) {
        // set upper card stacks
        setSizes(upper);
        upper.setAlignment(Pos.CENTER);
        upper.setSpacing(gap);
        
        ArrayList<CardStack> stacksUpper = new ArrayList();
        Paint stackColor = Paint.valueOf("green");
        stacksUpper.add(new CardStack(cardWidth, cardHeight, stackColor, false));
        stacksUpper.get(0).shuffleCardStack();
        
        stackColor = Paint.valueOf("blue");
        for (int i = 0; i < 6; i++) {
            stacksUpper.add(new CardStack(cardWidth, cardHeight, stackColor, true));
            
            if (i == 1)
                stackColor = Paint.valueOf("red");
        }
        
        for (CardStack cs : stacksUpper)
            upper.getChildren().add(cs.getStackNode());

        // set lower card stacks
        lower.setAlignment(Pos.TOP_CENTER);
        lower.setSpacing(gap);
        
        ArrayList<CardStack> stacksLower = new ArrayList();
        stackColor = Paint.valueOf("pink");
        for (int i = 0; i < 7; i++)
            stacksLower.add(new CardStack(cardWidth, cardHeight, stackColor, true));
        
        for (CardStack cs : stacksLower)
            lower.getChildren().add(cs.getStackNode());
        
        STACKS.addAll(stacksUpper);
        STACKS.addAll(stacksLower);
        
        dealGameCards();
    }
    
    public static void resetGame() {
        if (STACKS.get(0).peekTopCard() != null)
            STACKS.get(0).peekTopCard().setOpen(false);
        
        for (int i = 1; i < STACKS.size(); i++) {
            while (!STACKS.get(i).isEmpty()) {
                STACKS.get(0).addToTop(STACKS.get(i).popTopCard());
                STACKS.get(0).peekTopCard().setStackId(0);
                STACKS.get(0).peekTopCard().setOpen(false);
                STACKS.get(0).peekTopCard().setOffset(0.0);
            }
        }
        
        STACKS.get(0).shuffleCardStack();
        dealGameCards();
    }
    
    private static void setSizes(HBox upper) {
        Image image = new Image("resources/backside1.PNG");
        gap = upper.getPrefHeight() / 8.0;
        cardHeight = upper.getPrefHeight() - gap * 2.0;
        cardWidth = cardHeight * (image.getWidth() / image.getHeight());
        
        // check if card stack width exceeds HBox width
        if ((gap * (CARD_COUNT + 1) + cardWidth * CARD_COUNT) > upper.getPrefWidth()) {
            gap = upper.getPrefWidth() / (4.0 * CARD_COUNT);
            cardWidth = (upper.getPrefWidth() - gap * (CARD_COUNT + 1)) / CARD_COUNT;
            cardHeight = cardWidth * (image.getHeight() / image.getWidth());
        }
    }
    
    private static void dealGameCards() {
        for (int i = 7; i < 14; i++) {
            STACKS.get(i).addToTop(STACKS.get(0).popTopCard());
            STACKS.get(i).peekTopCard().setOpen(true);
            STACKS.get(i).peekTopCard().setStackId(STACKS.get(i).getId());
            STACKS.get(i).peekTopCard().setOffset((i - 7) * offset);
            
            for (int j = i + 1; j < 14; j++) {
                STACKS.get(j).addToTop(STACKS.get(0).popTopCard());
                STACKS.get(j).peekTopCard().setStackId(STACKS.get(j).getId());
                STACKS.get(j).peekTopCard().setOffset((i - 7) * offset);
            }
        }        
    }
    
    public static void checkGameComplete() {
        for (int i = 3; i < 7; i++)
            if (!STACKS.get(i).peekTopCard().getTitle().equals("KING"))
                return;
            
        double w = Main.getPane().getPrefWidth() * 0.75;
        double h = Main.getPane().getPrefHeight() * 0.75;
        double x = (Main.getPane().getPrefWidth() - w) / 2.0;
        double y = (Main.getPane().getPrefHeight() - h) / 2.0;
        
        Rectangle rect = new Rectangle();
        rect.setWidth(Main.getPane().getPrefWidth());
        rect.setHeight(Main.getPane().getPrefHeight());
        rect.setFill(Paint.valueOf("black"));
        rect.setOpacity(0.4);
        Main.getPane().getChildren().add(rect);
        
        Pane gameComplete = new Pane();
        gameComplete.setPrefSize(w, h);
        gameComplete.setLayoutX(x);
        gameComplete.setLayoutY(y);
        gameComplete.setBackground(new Background(new BackgroundFill(Paint.valueOf("rgb(180, 220, 100)"), new CornerRadii(10.0), Insets.EMPTY)));
        Main.getPane().getChildren().add(gameComplete);
        
        Text congrats = new Text();
        congrats.setWrappingWidth(w * 0.8);
        congrats.setLayoutX(w * 0.1);
        congrats.setLayoutY(h * 0.2);
        congrats.setText("CONGRATULATIONS,\nYOU ARE AWESOME...\n\n...just like me ;P !");
        congrats.setFont(Font.font("Impact", h * 0.11));
        congrats.setFill(Paint.valueOf("white"));
        congrats.setTextAlignment(TextAlignment.CENTER);
        gameComplete.getChildren().add(congrats);
        
        Button ok = new Button();
        ok.setPrefSize(w * 0.75, h * 0.2);
        ok.setLayoutX(w * 0.125);
        ok.setLayoutY(h * 0.7);
        ok.setText("ALRIGHTY THEN!");
        ok.setFont(Font.font("Arial", h * 0.05));
        ok.setOnMouseClicked((event) -> {
            Main.getPane().getChildren().remove(gameComplete);
            Main.getPane().getChildren().remove(rect);
        });
        gameComplete.getChildren().add(ok);
    }
    
}
