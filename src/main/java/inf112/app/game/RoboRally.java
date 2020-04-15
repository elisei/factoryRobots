package inf112.app.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.kotcrab.vis.ui.VisUI;
import inf112.app.map.Map;

import inf112.app.screens.LoadingMenuScreen;
import inf112.app.screens.PauseGameScreen;

public class RoboRally extends Game {
    public SpriteBatch batch;

    private Player player;
    protected Stage stage;
    protected StretchViewport viewport;
    protected Screen lastScreen;

    public Texture backgroundImg;

    public AssetManager manager;

    private String mapName;

    public Music backgroundMusic;

    public Sounds sounds;

    @Override
    public void create() {
        batch = new SpriteBatch();
        manager = new AssetManager();
        sounds = new Sounds(manager);

        backgroundMusic = Gdx.audio.newMusic(Gdx.files.internal("assets/Sounds/BackGroundSong.wav"));
        backgroundMusic.setVolume(0.1f);
        backgroundMusic.play();
        backgroundMusic.setLooping(true);

        backgroundImg = new Texture(Gdx.files.internal("assets/game-menu.png"));

        viewport = new StretchViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        viewport.apply();

        stage = new Stage(viewport, batch); // Create new stage to share with each screen
        Gdx.input.setInputProcessor(stage); // Define InputProcessor on the stage
        this.setScreen(new LoadingMenuScreen(this, viewport, stage));
    }

    @Override
    public void dispose() {
        // Dispose of all object if any of the screens closes
        batch.dispose();
        stage.dispose();
        VisUI.dispose();
        backgroundImg.dispose();
        backgroundMusic.dispose();
        manager.dispose();
    }

    @Override
    public void render() {
        super.render();
    }

    /**
     * Sets the screen to PauseGameScreen
     */
    @Override
    public void pause() {
        if (!(this.getScreen() instanceof PauseGameScreen)){
            lastScreen = this.getScreen();
        }
        this.backgroundMusic.pause();
        this.setScreen(new PauseGameScreen(this, viewport, stage));
    }

    /**
     * Resumes the last used screen
     */
    @Override
    public void resume() {
        if (lastScreen != null){
            this.setScreen(this.lastScreen);
        }
        this.backgroundMusic.play();
    }

    public void setMapName(String mapName){
        this.mapName = mapName;
    }

    public String getMapName(){
        return this.mapName;
    }
    public void setMap(String name){
        Map.setInstance(name);
    }
    public void setPlayer(int x, int y){
        player = new Player(x, y);
    }

    public Player getPlayer(){
        return this.player;
    }

}
