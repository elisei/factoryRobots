package inf112.app.game;

import inf112.app.GdxTestRunner;
import inf112.app.map.Map;
import inf112.app.objects.Direction.Rotation;
import inf112.app.objects.Player;
import inf112.app.objects.Position;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

@RunWith(GdxTestRunner.class)
public class MoveCardTest {
    MoveCard moveOne;
    MoveCard moveTwo;
    MoveCard moveThree;
    MoveCard backUp;
    Map map;
    Player player;

    @Before
    public void setUp() throws Exception {
        moveOne = new MoveCard(150,1);
        moveTwo = new MoveCard(150, 2);
        moveThree = new MoveCard(150,3);
        backUp = new MoveCard(150,true);

        map = new Map("testMap");
        player = new Player(2,2,map);
    }

    @Test
    public void doActionMoveOneTest() {
        Position oldPos = player.getCharacter().getPos().copyOf();
        oldPos.moveInDirection();
        moveOne.doAction(player);
        assertEquals("Failure, positions should be the same",oldPos,player.getCharacter().getPos());
    }

    @Test
    public void doActionMoveTwoTest() {
        Position oldPos = player.getCharacter().getPos().copyOf();
        oldPos.moveInDirection();
        oldPos.moveInDirection();
        moveTwo.doAction(player);
        assertEquals("Failure, positions should be the same",oldPos,player.getCharacter().getPos());
    }

    @Test
    public void doActionMoveThreeTest() {
        Position oldPos = player.getCharacter().getPos().copyOf();
        for(int i = 0; i<3; i++){
            oldPos.moveInDirection();
        }
        moveThree.doAction(player);
        assertEquals("Failure, positions should be the same",oldPos,player.getCharacter().getPos());
    }
    @Test
    public void doActionBackUpTest() {
        Position oldPos = player.getCharacter().getPos().copyOf();
        oldPos.getDirection().turn(Rotation.LEFT);
        oldPos.getDirection().turn(Rotation.LEFT);
        oldPos.moveInDirection();
        oldPos.getDirection().turn(Rotation.LEFT);
        oldPos.getDirection().turn(Rotation.LEFT);
        backUp.doAction(player);
        assertEquals("Failure, positions should be the same",oldPos,player.getCharacter().getPos());
    }

}