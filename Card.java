package solitaer_mb;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;

/**
 *
 * @author Michael Bauer
 */
public class Card {
    private static double IMAGE_WIDTH;
    private static double IMAGE_HEIGHT;
    
    private final ImageView CARD = new ImageView();
    private final Image OPEN;
    private final Image HIDDEN = new Image("resources/backside1.PNG");
    private final String TITLE;
    private final String SIGN;
    private final int VALUE;
    
    private int stackId;
    private boolean isCardOpen;
    
    public Card(String filepath, String title, String sign, int value, int stackId, double imageWidth, double imageHeight) throws Exception {
        if ((IMAGE_WIDTH < 1.0) || (IMAGE_HEIGHT < 1.0)) {
            if ((imageWidth < 1.0) || (imageHeight < 1.0))
                throw new Exception("invalid size value");
            
            this.IMAGE_WIDTH = imageWidth;
            this.IMAGE_HEIGHT = imageHeight;
        }
        
        this.TITLE = title;
        this.SIGN = sign;
        this.VALUE = value;
        this.OPEN = new Image(filepath);
        this.stackId = stackId;
        
        this.CARD.setFitWidth(IMAGE_WIDTH);
        this.CARD.setFitHeight(IMAGE_HEIGHT);
        this.CARD.setImage(HIDDEN);
        this.CARD.setOnMouseDragged((event) -> {
            event.setDragDetect(true);
        });
        
        this.CARD.setOnDragDetected((MouseEvent event) -> {
            if (isCardOpen && ((getStackId() < 3) || (getStackId() > 6))) {
                System.out.println("drag detected: " + stackId);
                Dragboard db = CARD.startDragAndDrop(TransferMode.ANY);
                ClipboardContent content = new ClipboardContent();
                content.putString(getCardId());
                db.setContent(content);
            }
        });
        
        this.isCardOpen = false;
        
    }
    
    public String getCardId() {
        return VALUE + "_" + SIGN + "_" + TITLE + "_" + stackId;
    }
    
    public String getTitle() {
        return TITLE;
    }
    
    public String getSign() {
        return SIGN;
    }
    
    public int getValue() {
        return VALUE;
    }
    
    public int getStackId() {
        return stackId;
    }
    
    public CardStack getParentStack() {
        return CardLayout.STACKS.get(stackId);
    }
    
    public void setStackId(int stackId) {
        this.stackId = stackId;
    }

    public ImageView getCardImage() {
        return CARD;
    }

    public void setOpen(boolean open) {
        this.isCardOpen = open;
        
        if (open)
            CARD.setImage(OPEN);
        else
            CARD.setImage(HIDDEN);
    }
    
    public boolean isOpen() {
        return isCardOpen;
    }
    
    public void setOffset(double offset) {
        CARD.setLayoutY(offset);
    }
}
