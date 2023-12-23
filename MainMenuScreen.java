package com.blooddrive.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;

public class MainMenuScreen implements Screen {

    Texture backgroundTexture;
    final TiledGame game;

    OrthographicCamera camera;
    Music mainMenuMusic;

    public MainMenuScreen(final TiledGame gam) {

        backgroundTexture = new Texture(Gdx.files.internal("MenuScreen.png"));

        game = gam;

        camera = new OrthographicCamera();
        camera.setToOrtho(false, TiledGame.GAMEWIDTH, TiledGame.GAMEHEIGHT);

        game.font.getData().setScale(1.5f); // Enlarge ugly font.

        // Load the main menu music
        mainMenuMusic = Gdx.audio.newMusic(Gdx.files.internal("MainMenuMusic.mp3"));
        mainMenuMusic.setLooping(true);  // Set the music to loop
        mainMenuMusic.play();  // Start playing the music
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0.2f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camera.update();
        game.batch.setProjectionMatrix(camera.combined);

        // Display a single keypress menu.
        game.batch.begin();


        game.batch.draw(backgroundTexture, 0, 0, TiledGame.GAMEWIDTH, TiledGame.GAMEHEIGHT);


        game.batch.end();



        if (Gdx.input.isKeyPressed(Input.Keys.SPACE)) {
            // Stop the music when transitioning to the next screen
            mainMenuMusic.stop();
            game.setScreen(new BloodDrive(game, this));
            dispose();
        }

        if (Gdx.input.isKeyPressed(Input.Keys.NUM_2)) {
            game.setScreen(new BackgroundStory(game));
            mainMenuMusic.stop();
        }

        if (Gdx.input.isKeyPressed(Input.Keys.NUM_1)) {
            game.setScreen(new HowToPlay(game));
            mainMenuMusic.stop();
        }
    }

    @Override
    public void resize(int width, int height) {
    }

    @Override
    public void show() {
    }

    @Override
    public void hide() {
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void dispose() {
        // Dispose of the music when the screen is disposed
        mainMenuMusic.dispose();
        backgroundTexture.dispose();

    }
}
