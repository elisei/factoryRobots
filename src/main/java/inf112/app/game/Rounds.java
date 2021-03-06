package inf112.app.game;

import inf112.app.cards.CardSlot;
import inf112.app.cards.ICard;
import inf112.app.map.Map;
import inf112.app.objects.*;



import java.util.ArrayList;
import java.util.Collections;

public class Rounds {
    private final ArrayList<Robot> robots;
    private Map map;

    public Rounds() {
        this.robots = Map.getInstance().getRobotList();
        this.map = Map.getInstance();

    }


    /**
     * method for putting the players who have lost a life
     * back to their checkpoint
     */
    public void putBackPlayers(){
        for (Robot r : robots){
            if (!r.isDead() && r.hasLostLife()){
                r.backToCheckPoint();
                r.setLostLife(false);
            }
        }
    }

    /**
     * dealing right amount of cards to each robot
     */
    public void dealCards(){
        for (Robot r : robots){
            r.dealNewCards();
        }
    }

    /**
     * method for doing the actions in rights order for each of the cards
     * and triggering all the elements
     */
    public void doPhase(int phaseNum){
            ArrayList<Integer> cardsFromSlot = new ArrayList<>();
            for (Robot r : robots) {
                ICard card = r.getProgrammedCard(phaseNum - 1);
                if (card != null) {
                    cardsFromSlot.add(card.getPoint());
                }
            }
            cardsFromSlot.sort(Collections.reverseOrder());
            for (int i = 0; i < cardsFromSlot.size(); i++) {
                for (Robot r : robots) {
                    ICard card = r.getProgrammedCard(phaseNum - 1);
                    if (card == null) {
                        continue;
                    }
                    if (cardsFromSlot.get(i) == card.getPoint()) {
                        card.doAction(r);
                    }
                }
            }
                for (Robot r : robots) {
                    Conveyor conveyor = Conveyor.extractConveyorFromCell(r.getPos());
                    if (conveyor != null) {
                        conveyor.doAction(r);
                    }
                }
                for (Robot r : robots) {
                    ArrayList<IBoardElement> contents = map.getCellList().getCell(r.getPos()).getInventory().getElements();
                    for (IBoardElement elem : contents) {
                        if (elem instanceof Conveyor) {
                            continue;
                        } else if (elem instanceof Laser) {
                            continue;
                        } else {
                            elem.doAction(r);
                        }
                    }
                }
                map.fireLasers();



        if (phaseNum == 5){
            for (Robot r : robots){
                if (r.getPowerDownNextRound()){
                    r.setPowerDown(true);
                    r.setPowerDownNextRound(false);
                }else{
                    r.setPowerDown(false);
                }
                CardSlot[] slots = r.getProgrammedCards();
                CardSlot[] availableCards = r.getAvailableCards();
                r.wipeSlots(slots);
                r.wipeSlots(availableCards);
            }
        }

    }
}

