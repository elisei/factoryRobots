package inf112.app.objects;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.tiles.StaticTiledMapTile;
import com.badlogic.gdx.math.Vector2;
import inf112.app.cards.CardSlot;
import inf112.app.cards.ICard;
import inf112.app.game.CardUI;
import inf112.app.game.GameSounds;
import inf112.app.map.Direction;
import inf112.app.map.Map;
import inf112.app.map.Position;
import inf112.app.map.Direction.Rotation;

import java.util.ArrayList;

/**
 * This class is a representation of the robots
 * on the board
 */
public class Robot implements ILaserInteractor, IBoardElement {
    private Map map;
    private Position pos;
    private Vector2 vectorPos;
    private Flag lastVisited;
    private Laser laser;
    private int damageTokens;
    private int lives;
    private boolean powerDown;
    private boolean isDead;
    private Position checkPoint;
    private CardSlot[] availableCards;
    private CardSlot[] programmedCards;
    private boolean doneProgramming;
    private boolean hasLostLife;
    private boolean powerDownNextRound;

    //Player sprites
    private TiledMapTileLayer.Cell normalPlayer;
    private TiledMapTileLayer.Cell winningPlayer;
    private TiledMapTileLayer.Cell loosingPlayer;

    private GameSounds sound;

    public Robot(Position pos, String charName){
        this.map = Map.getInstance();

        this.pos = pos;
        vectorPos = new Vector2(pos.getXCoordinate(),pos.getYCoordinate());

        loadPlayerSprites(charName);

        map.registerRobot(this);

        lastVisited = null;
        damageTokens = 0;
        lives = 3;
        powerDown = false;
        laser = new Laser(this,false);
        isDead = false;
        doneProgramming = false;

        initializeCardsSlots();
    }

    private void initializeCardsSlots(){
        programmedCards = new CardSlot[5];
        availableCards = new CardSlot[9];
        for(int i = 0; i<9; i++){
            if(i < 5){
                programmedCards[i] = new CardSlot("bottom");
            }
            availableCards[i] = new CardSlot("side");
        }
    }

    /**
     * Method to move the robot forward in the direction it is facing
     * @param steps Number of steps the robot should take
     */
    public void move(int steps){
        while(steps!=0){
            steps -= 1;
            moveAndPush(this,getPos().getDirection());
        }
    }

    /**
     * Move the robot in a certain direction without turning
     * Only moves one step
     * @param dir Direction to move in
     */
    public void move(Direction dir){
        Position copy = pos.copyOf();
        copy.setDirection(dir);
        boolean valid = map.validMove(copy);
        copy.moveInDirection();
        if(valid && map.robotInTile(copy) == null) {
            updatePosition(this,dir);
        }
    }

    /**
     * Method to change the direction of the robot
     * @param r Enum for which direction the robot should turn, either LEFT or RIGHT
     */
    public void turn(Rotation r){
        pos.getDirection().turn(r);
        rotateSprites(r);
    }

    public Position getPos() {
        return pos;
    }

    /**
     * Rotates the visual representation of the robot
     * @param rot The direction the robot should rotate
     */
    private void rotateSprites(Rotation rot) {
        int orientation = normalPlayer.getRotation();
        switch(rot){
            case LEFT:
                orientation = (orientation + 1) % 4;
                break;
            case RIGHT:
                orientation -= 1;
                orientation = (orientation < 0) ? 3 : orientation;
                break;
            default: throw new IllegalArgumentException("Invalid rotation enum in rotateSprites");
        }
        normalPlayer.setRotation(orientation);
        loosingPlayer.setRotation(orientation);
        winningPlayer.setRotation(orientation);
    }

    public TiledMapTileLayer.Cell getNormal() {
        return normalPlayer;
    }

    public TiledMapTileLayer.Cell getWinner() {
        return winningPlayer;
    }

    public TiledMapTileLayer.Cell getLooser() {
        return loosingPlayer;
    }


    /**
     * Loads the different character sprites so that LibGDX can use them
     * @param charName Name of the character, will be used in the filepath to the spritesheet
     */
    public void loadPlayerSprites(String charName){
        String path = "assets/" + charName + ".png";
        //Loading and splitting player sprites
        Texture spriteMap = new Texture(path);
        TextureRegion[][] sprites = TextureRegion.split(spriteMap,300,300);
        //Assigning individual sprites
        normalPlayer = new TiledMapTileLayer.Cell().setTile(new StaticTiledMapTile(sprites[0][0]));
        loosingPlayer = new TiledMapTileLayer.Cell().setTile(new StaticTiledMapTile(sprites[0][1]));
        winningPlayer = new TiledMapTileLayer.Cell().setTile(new StaticTiledMapTile(sprites[0][2]));
    }

    /**
     * @param r Needed since the method is recursive
     * @param dir Used to maintain the orientation of the original robot
     * @return true if Robot has moved, false if not
     */
     public boolean moveAndPush(Robot r, Direction dir) {
         Position newPos = r.getPos().copyOf();
         newPos.setDirection(dir);
         if(map.validMove(newPos)) {
             newPos.moveInDirection();
             IBoardElement nextCell = checkContentOfCell(newPos);
             if (nextCell == null || nextCell instanceof Wall) {
                 updatePosition(r,dir);
                 return true;
             } else if (nextCell instanceof Robot) {
                 Robot next = (Robot) nextCell;
                 boolean canMove = moveAndPush(next,dir);
                 if (canMove) {
                     updatePosition(r,dir);
                 }
                 return canMove;
             }
             return false;
         }
         return false;
     }

    /**
     * The method that all other move methods should call to change the robots position. <br>
     * Does not check if the move is valid, but makes sure that map inventory and vector position
     * is updated in addition to the players position.
     * @param dir The direction the robot should move
     */
     private void updatePosition(Robot robot, Direction dir){
         Position oldPos = robot.getPos().copyOf();
         Direction old = oldPos.getDirection().copyOf();
         robot.getPos().setDirection(dir);
         robot.getPos().moveInDirection();
         robot.getPos().setDirection(old);
         map.getCellList().getCell(oldPos).getInventory().getElements().remove(robot);
         map.getCellList().getCell(robot.getPos()).getInventory().addElement(robot);
         vectorPos.set(robot.getPos().getXCoordinate(), robot.getPos().getYCoordinate());
     }

    /**
     * Method used to check if there is either a wall or a Robot in a cell
     * If there is both, then we prefer the robot
     * @param position The position of the cell we want to check
     * @return The element found in the cell, either a robot or a wall
     */
    public IBoardElement checkContentOfCell(Position position) {
        ArrayList<IBoardElement> newCell = map.getCellList().getCell(position).getInventory().getElements();
        IBoardElement elem = null;
        for (IBoardElement e : newCell) {
            if(e instanceof Robot){
                elem = e;
            } else if(e instanceof Wall){
                if(elem == null){
                    elem = e;
                }

            }
        }
        return elem;
    }

    @Override
    public void doAction(Robot robot) {
        fireLaser();
    }

    public Flag getVisitedFlag() {
        return lastVisited;
    }

    public void setVisitedFlag(Flag flag) {
        this.lastVisited = flag;
    }

    public int getDamageTokens() {return damageTokens; }

    public void addDamageTokens(int dealDamage) {
        damageTokens += dealDamage;
        if (damageTokens >= 10) {
            lives--;
            hasLostLife = true;
            damageTokens = 0;
            isDead = lives <= 0;
            try {
                sound.deathSound();
            } catch (NullPointerException ignored){ // Catch exception for test classes

            }
        } else {
            try {
                sound.takeDamage();
            } catch (NullPointerException ignored){ // Catch exception for test classes

            }
        }
        CardUI.getInstance().updateDamageTokens(damageTokens);
    }

    /**
     * method for deaing the right amount of cards compared to damageTokens
     */
    public void dealNewCards() {
        wipeSlots(availableCards);
        wipeSlots(programmedCards);
        if (powerDown){
            return;
        }

        for (int i = 0; i<9-damageTokens; i++) {
            availableCards[i].addCard(Map.getInstance().getDeck().getCard());
        }
    }

    public void wipeSlots(CardSlot[] slotList){
        for(CardSlot slot : slotList){
            slot.removeCard();
        }
    }


    /**
     * Method used by repairStation to remove damageTokens from robot.
     * @param amount How many damageTokens should be removed
     */
    public void removeDamageTokens(int amount) {
        damageTokens -= amount;
        if (damageTokens < 0) damageTokens = 0;
        CardUI.getInstance().updateDamageTokens(damageTokens);
    }

    public int getLives() { return lives; }

    public boolean hasLostLife() {
        return hasLostLife;
    }

    public void setLostLife(boolean lostLife){
        hasLostLife = lostLife;
    }

    /**
     * method used to find out if robot is dead or alive
     * @return boolean true if dead, false if alive
     */
    public boolean isDead(){
        return isDead;
    }

    /**
     * Sets a new checkpoint for the robot
     * @param p
     */
    public void setCheckPoint(Position p){
        this.checkPoint = p;
        try {
            sound.checkpoint();
        } catch (NullPointerException ignored){ // Preventing error in test classes

        }
    }

    public void backToCheckPoint(){
        this.pos = checkPoint;
    }


    /**
     * Used to set the powerStatus of the robot(powerDown)
     * @param powerDown
     */
    public void setPowerDown(boolean powerDown) {
        this.powerDown = powerDown;
        damageTokens = 0;
    }

    public void setPowerDownNextRound(boolean powerDownNextRound){
        this.powerDownNextRound = powerDownNextRound;
    }

    public boolean getPowerDownNextRound(){
        return powerDownNextRound;
    }

    public boolean getPowerDown() { return powerDown; }

    /**
     * Method for initialising the moves depicted on the cards
     * in the programming slots
     */
    public void initiateRobotProgramme() {
        for(CardSlot slot : programmedCards) {
            ICard card = null;
            if (slot.isLocked()){
                card = slot.getCard();
            } else {
                card = slot.removeCard();
            }
            if (card != null) {
                card.doAction(this);
            }
        }
    }

    /**
     * Sets a players programmed card into its register.
     * @param index The register index to set the card for.
     * @param card The card to set.
     */
    public void setProgrammedCard(int index, ICard card) {
        if (index >= 0 && index < 5) {
            programmedCards[index].addCard(card);
        } else {
            throw new IllegalArgumentException("Index must be between 0 and 4");
        }
    }

    /**
     * returns a programmed card form a spesific position in
     * the register array
     * @param index
     * @return
     */
   public ICard getProgrammedCard(int index){
        if (index >= 0 && index < 5){
            return programmedCards[index].getCard();
        }else {
            throw new IllegalArgumentException("Index must be between 0 and 4");
        }
   }

    public boolean doneProgramming(){
       map.incrementDoneProgramming();
       return doneProgramming;
    }

    @Override
    public void fireLaser() {
        laser.fire();
    }

    public CardSlot[] getAvailableCards() {
        return availableCards;
    }

    public CardSlot[] getProgrammedCards() {
        return programmedCards;
    }
}
