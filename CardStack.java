package solitaer_mb;

import java.util.ArrayList;
import java.util.Collections;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Paint;

/**
 *
 * @author Michael Bauer
 */
public class CardStack {
    private static final String[] TITLES = {
        "ACE", "2", "3", "4", "5", "6", "7", "8", "9", "10", "JACK", "QUEEN", "KING"
    };
    private static final String[] SIGNS = {
        "CLUBS", "DIAMONDS", "HEARTS", "SPADES"     // Kreuz, Karo, Herz, Pik
    };
    private static int nextId = 0;
    
    private final int ID;
    private final ArrayList<Card> CARDS = new ArrayList();
    private final Pane STACK = new Pane();

    public CardStack(double width, double height, Paint background, boolean emptyStack) {
        ID = nextId++;
        
        STACK.setMaxSize(width, height);
        STACK.setMinSize(width, height);
        STACK.setBorder(new Border(new BorderStroke(background, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(2.0))));

        if (ID == 0) {
            STACK.setOnMouseClicked((event) -> {
                if (CARDS.size() > 1) {
                    backToStack();
                    showHelperCards();
                } else if (CARDS.size() == 1) {
                    CARDS.get(0).setOpen(true);
                }
            });
        } else if (ID > 2) {
            STACK.setOnDragOver((event) -> {
                event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
            });
            STACK.setOnDragDropped((event) -> {
                Dragboard db = event.getDragboard();
                if (db.hasString()) {
                    System.out.println(db.getString());
                    CardStack foreignStack = CardLayout.STACKS.get(Integer.valueOf(db.getString().split("_")[3]));
                    Card card = foreignStack.peekCard(db.getString());

                    if (isDropAllowed(card)) {
                        ArrayList<Card> move = foreignStack.popCards(db.getString());
                        
                        for (Card c : move) {
                            addToTop(c);
                            c.setStackId(ID);
                        }
                        
                        if ((foreignStack.peekTopCard() != null) && (foreignStack.getId() != 0))
                            foreignStack.peekTopCard().setOpen(true);
                        
                        System.out.println("Dropped: " + db.getString() + " from " + foreignStack.getId() + " on " + ID);
                        event.setDropCompleted(true);
                    }
                } else {
                    event.setDropCompleted(false);
                }
                event.consume();
                
                CardLayout.checkGameComplete();
            });
        }

        if (!emptyStack) {
            for (int i = 0; i < SIGNS.length; i++) {
                for (int j = 0; j < TITLES.length; j++) {
                    String filepath = "resources/k-" + TITLES[j].toLowerCase() + "_of_" + SIGNS[i].toLowerCase() + ".png";

                    try {
                        CARDS.add(new Card(filepath, TITLES[j], SIGNS[i], j + 1, ID, width, height));
                    } catch (Exception ex) {
                        Logger.getLogger(CardStack.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
            
            resetStack();
        }

    }
    
    public int getId() {
        return ID;
    }
    
    public Pane getStackNode() {
        return STACK;
    }
    
    public void shuffleCardStack() {
        Collections.shuffle(CARDS);
        resetStack();
    }
    
    public void addToTop(Card card) {
        CARDS.add(card);
        
        if (ID > 6)
            card.setOffset(STACK.getChildren().size() * CardLayout.getOffset());
        else
            card.setOffset(0.0);
        
        STACK.getChildren().add(card.getCardImage());
    }
    
    public void addToBottom(Card card) {
        CARDS.add(0, card);
        
        card.setOffset(0.0);
        
        STACK.getChildren().add(0, card.getCardImage());
    }
    
    public Card peekBottomCard() {
        if (CARDS.isEmpty())
            return null;
        
        return CARDS.get(0);
    }
    
    public Card popBottomCard() {
        if (CARDS.isEmpty())
            return null;
        
        STACK.getChildren().remove(0);
        return CARDS.remove(0);
    }
    
    public Card peekTopCard() {
        if (CARDS.isEmpty())
            return null;
        
        return CARDS.get(CARDS.size() - 1);
    }
    
    public Card popTopCard() {
        if (CARDS.isEmpty())
            return null;
        
        int index = CARDS.size() - 1;
        
        STACK.getChildren().remove(index);
        return CARDS.remove(index);
    }
    
    public Card peekCard(String cardId) {
        for (Card c : CARDS)
            if (c.getCardId().equals(cardId))
                return c;
        
        return null;
    }
    
    public ArrayList<Card> popCards(String cardId) {
        ArrayList<Card> cards = new ArrayList();
        int index = -1;
        
        while (++index < CARDS.size())
            if (CARDS.get(index).getCardId().equals(cardId))
                break;
        
        while (index < CARDS.size()) {
            cards.add(CARDS.remove(index));
            STACK.getChildren().remove(index);
        }
        
        return cards;        
    }
    
    private void resetStack() {
        STACK.getChildren().clear();
        
        for (Card c : CARDS)
            STACK.getChildren().add(c.getCardImage());
    }
    
    private void backToStack() {
        for (int i = 2; i >= 0; i--) {
            CardStack stack = CardLayout.STACKS.get(i);
            if (!stack.isEmpty()) {
                if (stack.peekTopCard().isOpen()) {
                    stack.peekTopCard().setOpen(false);
                    stack.peekTopCard().setStackId(0);
                    CardLayout.STACKS.get(0).addToBottom(stack.popTopCard());
                }
            }
        }
    }
    
    private void showHelperCards() {
        if (CARDS.size() > 2) {
            for (int i = 0; i < 3; i++) {
                CardLayout.STACKS.get(2 - i).addToTop(popTopCard());
                CardLayout.STACKS.get(2 - i).peekTopCard().setOpen(true);
                CardLayout.STACKS.get(2 - i).peekTopCard().setStackId(2 - i);
            } 
        } else if (CARDS.size() > 1) {
            CardLayout.STACKS.get(1).addToTop(popTopCard());
            CardLayout.STACKS.get(1).peekTopCard().setOpen(true);
            CardLayout.STACKS.get(1).peekTopCard().setStackId(1);
            CARDS.get(CARDS.size() - 1).setOpen(true);
        } else {
            CARDS.get(CARDS.size() - 1).setOpen(true);
        }
    }
    
    public boolean isEmpty() {
        return CARDS.isEmpty();
    }
    
    public boolean isTopCard(Card card) {
        return peekTopCard().equals(card);
    }
    
    private boolean isDropAllowed(Card card) {
        if ((ID > 2) && (ID < 7)) {
            System.out.println("CARD VALUE = " + card.getValue());
            return ((((peekTopCard() == null) && (card.getValue() == 1)) || 
                (peekTopCard().getSign().equals(card.getSign()) && (peekTopCard().getValue() == card.getValue() - 1))) &&
                    (card.getParentStack().isTopCard(card)));
        }
        
        if (ID > 6) {
            return (((peekTopCard() == null) && (card.getTitle().equals("KING"))) ||
                ((peekTopCard().getValue() == (card.getValue() + 1)) && (!isSameColor(peekTopCard(), card))));
        }
        
        return false;
    }
    
    private boolean isSameColor(Card card1, Card card2) {
        if (card1.getSign().equals("CLUBS") || card1.getSign().equals("SPADES")) {
            if (card2.getSign().equals("CLUBS") || card2.getSign().equals("SPADES"))
                return true;
        } else if (card2.getSign().equals("HEARTS") || card2.getSign().equals("DIAMONDS"))
            return true;
        
        return false;
    }
}
