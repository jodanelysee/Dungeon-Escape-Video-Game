// GameOverScreen.java
package com.blooddrive.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.GL20;

public class GameOverScreen implements Screen {
    BloodDrive bloodscreen;
    private final TiledGame game;
    private float score;
    private float timeSurvived;
    Music gameOverMusic;

    public GameOverScreen(final TiledGame game, BloodDrive bs) {
        this.game = game;
        bloodscreen = bs;
        score = bs.score;
        timeSurvived = bs.timeSurvived - 8;
        gameOverMusic = Gdx.audio.newMusic(Gdx.files.internal("GameOver.mp3"));
    }

    @Override
    public void show() {
        // Setup resources or initialization when the screen is shown
    }

    @Override
    public void render(float delta) {
        gameOverMusic.play();
        // Clear the screen
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Render game over message or any other content

        // Example: Display a message
        game.batch.begin();
        game.font.draw(game.batch, "Game Over!", 345, 310);
        game.font.draw(game.batch, "The monsters killed Jimmy!", 275, 280);
        game.font.draw(game.batch, "You survived for " +(int)timeSurvived+ " seconds and killed a total of " +(int)score+ " enemies", 120, 250);
        game.font.draw(game.batch, "Hit the ESC key to return to the main menu", 195, 220);
        game.batch.end();

        // Handle input to return to the main menu or perform other actions
        if (Gdx.input.isKeyPressed(Input.Keys.ESCAPE)) {
            // Stop the music when transitioning to the next screen
            game.setScreen(new MainMenuScreen(game));
            gameOverMusic.stop();
            dispose();
        }
    }

    @Override
    public void resize(int width, int height) {
        // Handle resizing, if needed
    }

    @Override
    public void pause() {
        // Handle pausing, if needed
    }

    @Override
    public void resume() {
        // Handle resuming, if needed
    }

    @Override
    public void hide() {
        // Clean up resources when the screen is hidden
    }

    @Override
    public void dispose() {
        // Dispose of resources, if needed
        gameOverMusic.dispose();

    }
}
