//Jodan Elysee
package com.blooddrive.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.Rectangle;


public class BloodDrive implements Screen {
    final private BitmapFont font;
    Array<Rectangle> skeletons;
    Array<Rectangle> nurses;
    Array<Rectangle> doctors;
    Array<Rectangle> enemies;
    Array<Rectangle> explosions;
    final TiledGame game;
    float nextEnemySpawnTime;
    float nextEnemySpawnTimeElapsed;
    float nextSkeletonSpawnTime;
    float nextSkeletonSpawnTimeElapsed;
    float nextNurseSpawnTime;
    float nextNurseSpawnTimeElapsed;
    float nextDoctorSpawnTime;
    float nextDoctorSpawnTimeElapsed;
    float explosionDuration = 1;
    float explosionDurationElapsed;

    float score = 0.0f;
    OrthographicCamera camera;
    SpriteBatch batch;
    Texture floorTexture;
    Texture helpMeTexture;
    Music firstLevelMusic;
    // Animated character variables
    Texture characterSheet;
    Texture enemySheet;
    Texture skeletonSheet;
    Texture nurseSheet;
    Texture doctorSheet;
    Texture explosionSheet;
    Texture crosshairTexture;
    // Animation Variables
    Animation<TextureRegion> skeletonWalkLeftAnimation;
    Animation<TextureRegion> explosionAnimation;
    Animation<TextureRegion> nurseWalkDownAnimation;
    Animation<TextureRegion> doctorWalkUpAnimation;
    Animation<TextureRegion> characterAnimation;
    Animation<TextureRegion> walkRightAnimation;
    Animation<TextureRegion> enemyWalkRightAnimation;
    float stateTime;
    //Positions
    float explosionX, explosionY;
    float characterX, characterY;
    float enemyX, enemyY;
    float skeletonX, skeletonY;
    float nurseX, nurseY;
    float doctorX, doctorY;
    // boolean for crosshair logic
    boolean isMouseOnScreen;
    //Sound Effects
    Sound bulletSound;
    //Sizes for textures
    float characterWidth = 70f;
    float characterHeight = 70f;
    float enemyWidth = 100f;
    float enemyHeight = 100f;
    float skeletonWidth = 100f;
    float skeletonHeight = 100f;
    float nurseHeight = 100f;
    float nurseWidth = 100f;
    float doctorWidth = 100f;
    float doctorHeight = 100f;
    float crosshairWidth = 40f;
    float crosshairHeight = 40f;
    float explosionHeight = 100f;
    float explosionWidth = 100f;
    // Define the boundaries for the barriers
    float barrierLeft = 0;
    float barrierRight = TiledGame.GAMEWIDTH - characterWidth;
    float barrierTop = TiledGame.GAMEHEIGHT - characterHeight;
    float barrierBottom = 0;
    // Lock position
    float lockPositionX = 365;
    float lockPositionY = 215;
    float dcLockPositionX = 355;
    // Enemy spawn position
    float enemySpawnX = 0;
    float enemySpawnY = 215;
    boolean enemySpawned = false;
    boolean skeletonSpawned = false;
    boolean nurseSpawned = false;
    boolean doctorSpawned = false;
    float skeletonSpawnX = 700;
    float skeletonSpawnY = 215;
    float nurseSpawnX = 355;
    float nurseSpawnY = 400;
    float doctorSpawnX = 355;
    float doctorSpawnY = 0;
    // Flag to track if the delay for music has passed
    boolean musicDelayPassed = false;
    float musicDelayTimer = 3.0f;
    float delay = 5.0f;
    float timeSurvived = 0.0f;
    // Game over variables
    private boolean gameOver = false;

    float speedFactor = 1;
    boolean monstersSlowed;
    float monstersSlowedTimer;
    float monstersSlowedDuration = 5;
    public BloodDrive(final TiledGame gam, final MainMenuScreen mainMenuScreen) {
        //Array for monster spawning
        skeletons = new Array<>();
        doctors = new Array<>();
        nurses = new Array<>();
        enemies = new Array<>();
        explosions = new Array<>();
        //Monsters will spawn after 8 seconds
        nextSkeletonSpawnTime = 8;
        nextDoctorSpawnTime = 16;
        nextNurseSpawnTime = 24;
        nextEnemySpawnTime = 32;

        game = gam;

        //set camera
        camera = new OrthographicCamera();
        camera.setToOrtho(false, TiledGame.GAMEWIDTH, TiledGame.GAMEHEIGHT);

        batch = new SpriteBatch();
        font = new BitmapFont();

        // Load the floor image
        floorTexture = new Texture(Gdx.files.internal("floor.png"));

        firstLevelMusic = Gdx.audio.newMusic(Gdx.files.internal("firstLevelMusic.mp3"));

        // Load sprite sheets
        explosionSheet = new Texture(Gdx.files.internal("explosionSheet.png"));
        enemySheet = new Texture(Gdx.files.internal("EnemySpriteSheet.png"));
        characterSheet = new Texture(Gdx.files.internal("CharacterSpriteSheet.png"));
        skeletonSheet = new Texture(Gdx.files.internal("SkeletonSpriteSheet.png"));
        nurseSheet = new Texture(Gdx.files.internal("NurseSpriteSheet.png"));
        doctorSheet = new Texture(Gdx.files.internal("DoctorSpriteSheet.png"));

        // Define the regions for each animation frame
        TextureRegion[][] exp = TextureRegion.split(explosionSheet, explosionSheet.getWidth() / 12, explosionSheet.getHeight());
        TextureRegion[][] ske = TextureRegion.split(skeletonSheet, skeletonSheet.getWidth() / 16, skeletonSheet.getHeight());
        TextureRegion[][] enm = TextureRegion.split(enemySheet, enemySheet.getWidth() / 16, enemySheet.getHeight());
        TextureRegion[][] tmp = TextureRegion.split(characterSheet, characterSheet.getWidth() / 16, characterSheet.getHeight());
        TextureRegion[][] nur = TextureRegion.split(nurseSheet, nurseSheet.getWidth() / 16, nurseSheet.getHeight());
        TextureRegion[][] doc = TextureRegion.split(doctorSheet, doctorSheet.getWidth() / 16, doctorSheet.getHeight());

        TextureRegion[] explosionFrames = new TextureRegion[12];
        TextureRegion[] skeletonWalkFrames = new TextureRegion[16];
        TextureRegion[] skeletonWalkLeftFrames = new TextureRegion[4];
        TextureRegion[] enemyWalkFrames = new TextureRegion[16];
        TextureRegion[] enemyWalkRightFrames = new TextureRegion[4];
        TextureRegion[] walkFrames = new TextureRegion[16];
        TextureRegion[] walkRightFrames = new TextureRegion[4];
        TextureRegion[] nurseWalkFrames = new TextureRegion[16];
        TextureRegion[] nurseWalkDownFrames = new TextureRegion[4];
        TextureRegion[] doctorWalkFrames = new TextureRegion[16];
        TextureRegion[] doctorWalkUpFrames = new TextureRegion[4];

        int index = 0;

        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                walkFrames[index++] = tmp[0][i * 4 + j];
            }
        }

        index = 0;

        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                enemyWalkFrames[index++] = enm[0][i * 4 + j];
            }
        }

        index = 0;

        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                skeletonWalkFrames[index++] = ske[0][i * 4 + j];
            }
        }

        index = 0;

        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                nurseWalkFrames[index++] = nur[0][i * 4 + j];
            }
        }

        index = 0;

        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                doctorWalkFrames[index++] = doc[0][i * 4 + j];
            }
        }

        index = 0;

        for (int j = 0; j < 12; j++) {
            explosionFrames[index++] = exp[0][j];
        }


        nurseWalkDownFrames[0] = nurseWalkFrames[0];
        nurseWalkDownFrames[1] = nurseWalkFrames[1];
        nurseWalkDownFrames[2] = nurseWalkFrames[2];
        nurseWalkDownFrames[3] = nurseWalkFrames[3];

        doctorWalkUpFrames[0] = doctorWalkFrames[8];
        doctorWalkUpFrames[1] = doctorWalkFrames[9];
        doctorWalkUpFrames[2] = doctorWalkFrames[10];
        doctorWalkUpFrames[3] = doctorWalkFrames[11];

        skeletonWalkLeftFrames[0] = skeletonWalkFrames[14];
        skeletonWalkLeftFrames[1] = skeletonWalkFrames[13];
        skeletonWalkLeftFrames[2] = skeletonWalkFrames[12];
        skeletonWalkLeftFrames[3] = skeletonWalkFrames[11];


        enemyWalkRightFrames[0] = enemyWalkFrames[4];
        enemyWalkRightFrames[1] = enemyWalkFrames[5];
        enemyWalkRightFrames[2] = enemyWalkFrames[6];
        enemyWalkRightFrames[3] = enemyWalkFrames[7];

        walkRightFrames[0] = walkFrames[8];
        walkRightFrames[1] = walkFrames[9];
        walkRightFrames[2] = walkFrames[10];
        walkRightFrames[3] = walkFrames[11];

        // Create the animation with a frame duration of 0.25 seconds
        explosionAnimation = new Animation<>(0.05f, explosionFrames);
        skeletonWalkLeftAnimation = new Animation<>(0.25f, skeletonWalkLeftFrames);
        characterAnimation = new Animation<>(0.25f, walkFrames);
        walkRightAnimation = new Animation<>(0.25f, walkRightFrames);
        enemyWalkRightAnimation = new Animation<>(0.25f, enemyWalkRightFrames);
        nurseWalkDownAnimation = new Animation<>(0.25f, nurseWalkDownFrames);
        doctorWalkUpAnimation = new Animation<>(0.25f, doctorWalkUpFrames);
        stateTime = 0f;  // Initialize the stateTime

        // Initial character position
        nurseX = 0;
        nurseY = 215;

        doctorX = 0;
        doctorY = 215;

        skeletonX = 0;
        skeletonY = 215;

        characterX = 0;
        characterY = 215;

        enemyX = 0;
        enemyY = 215;

        // Load the crosshair image
        crosshairTexture = new Texture(Gdx.files.internal("Crosshair-White.png"));

        // Load the bullet sound
        bulletSound = Gdx.audio.newSound(Gdx.files.internal("Bullet.mp3"));

        // Set up the input processor for mouse events
        Gdx.input.setInputProcessor(new InputAdapter() {
            @Override
            public boolean mouseMoved(int screenX, int screenY) {
                // Update the flag when the mouse is on the screen
                isMouseOnScreen = true;
                return false;
            }

            @Override
            public boolean touchDown(int screenX, int screenY, int pointer, int button) {
                // Play bullet sound when the mouse is clicked
                int expX = screenX - (int)(explosionWidth/2);
                int expY = Gdx.graphics.getHeight() - screenY - (int)(explosionHeight/2);
                spawnExplosion(expX, expY);
                bulletSound.play();
                return true;
            }
        });
    }


    private void spawnEnemy() {
        System.out.println("Enemy spawn!");
        Rectangle enemy = new Rectangle();
        enemy.x = enemySpawnX;
        enemy.y = enemySpawnY;
        enemy.width  = enemyWidth;
        enemy.height = enemyHeight;
        enemies.add(enemy);
        nextEnemySpawnTime = MathUtils.random(2, 3);
        nextEnemySpawnTimeElapsed = 0;
    }

    private void spawnNurse() {
        System.out.println("Nurse spawn!");
        Rectangle nurse = new Rectangle();
        nurse.x = nurseSpawnX;
        nurse.y = nurseSpawnY;
        nurse.width  = nurseWidth;
        nurse.height = nurseHeight;
        nurses.add(nurse);
        nextNurseSpawnTime = MathUtils.random(2, 3);
        nextNurseSpawnTimeElapsed = 0;
    }

    private void spawnSkeleton() {
        System.out.println("Skeleton spawn!");
        Rectangle skeleton = new Rectangle();
        skeleton.x = skeletonSpawnX;
        skeleton.y = skeletonSpawnY;
        skeleton.width  = skeletonWidth;
        skeleton.height = skeletonHeight;
        skeletons.add(skeleton);
        nextSkeletonSpawnTime = MathUtils.random(2, 3);
        nextSkeletonSpawnTimeElapsed = 0;
    }

    private void spawnDoctor() {
        System.out.println("Doctor spawn!");
        Rectangle doctor = new Rectangle();
        doctor.x = doctorSpawnX;
        doctor.y = doctorSpawnY;
        doctor.width  = doctorWidth;
        doctor.height = doctorHeight;
        doctors.add(doctor);
        nextDoctorSpawnTime = MathUtils.random(2, 3);
        nextDoctorSpawnTimeElapsed = 0;
    }
    private void spawnExplosion(int screenX, int screenY) {
        Rectangle explosion = new Rectangle();
        explosion.x = screenX;
        explosion.y = screenY;
        explosion.width = explosionWidth;
        explosion.height = explosionHeight;
        explosions.add(explosion);
        explosionDurationElapsed = 0;
        if (explosions.size > 1)
            explosions.removeIndex(0);
    }

    @Override
    public void render(float delta) {
        float speed = 500 * delta;  // Adjust the speed based on delta for smooth movement
        //float speedFactor = 0.5f;
        Gdx.gl.glClearColor(0, 0, 0.2f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        handleInput(delta);

        camera.update();
        batch.setProjectionMatrix(camera.combined);

        batch.begin();

        batch.draw(floorTexture, 0, 0, TiledGame.GAMEWIDTH, TiledGame.GAMEHEIGHT);

        if (!gameOver) {
            if (characterX < 360) {
                batch.draw(walkRightAnimation.getKeyFrame(stateTime, true),
                        Math.max(barrierLeft, Math.min(characterX, barrierRight)),
                        Math.max(barrierBottom, Math.min(characterY, barrierTop)),
                        characterWidth,
                        characterHeight);
            } else {
                batch.draw(characterAnimation.getKeyFrame(stateTime, true),
                        Math.max(barrierLeft, Math.min(characterX, barrierRight)),
                        Math.max(barrierBottom, Math.min(characterY, barrierTop)),
                        characterWidth,
                        characterHeight);

                if (characterX >= lockPositionX && characterY >= lockPositionY && !musicDelayPassed) {
                    musicDelayTimer -= delta;
                    if (musicDelayTimer > 0) {
                        font.draw(batch, "Protect Jimmy From The Monsters!", 280, 200);
                        font.draw(batch, "They are coming in " + (int) musicDelayTimer + " seconds", 297, 180);
                    }
                    if (musicDelayTimer < 0) {
                        musicDelayPassed = true;
                        firstLevelMusic.play();
                        firstLevelMusic.setLooping(true);
                        for (Rectangle enemy : enemies) {
                            enemyX = enemy.x;
                            enemyY = enemy.y;
                            batch.draw(enemyWalkRightAnimation.getKeyFrame(stateTime, true),
                                    Math.max(barrierLeft, Math.min(enemyX, barrierRight)),
                                    Math.max(barrierBottom, Math.min(enemyY, barrierTop)),
                                    enemyWidth,
                                    enemyHeight);

                            enemyX += speed * 2;
                            enemyX = Math.max(barrierLeft, Math.min(enemyX, barrierRight));
                        }
                        for (Rectangle skeleton : skeletons) {
                            skeletonX = skeleton.x;
                            skeletonY = skeleton.y;
                            batch.draw(skeletonWalkLeftAnimation.getKeyFrame(stateTime, true),
                                    skeletonX,
                                    Math.max(barrierBottom, Math.min(skeletonY, barrierTop)),
                                    skeletonWidth,
                                    skeletonHeight);

                            skeletonX -= speed * 2;
                        }
                        for (Rectangle nurse : nurses) {
                            nurseX = nurse.x;
                            nurseY = nurse.y;
                            batch.draw(nurseWalkDownAnimation.getKeyFrame(stateTime, true),
                                    nurseX,
                                    Math.max(barrierBottom, Math.min(nurseY, barrierTop)),
                                    nurseWidth,
                                    nurseHeight);

                            nurseY -= speed * 2;
                        }
                        for (Rectangle doctor : doctors) {
                            doctorX = doctor.x;
                            doctorY = doctor.y;
                            batch.draw(doctorWalkUpAnimation.getKeyFrame(stateTime, true),
                                    doctorX,
                                    Math.max(barrierBottom, Math.min(doctorY, barrierTop)),
                                    doctorWidth,
                                    doctorHeight);
                        }

                        skeletonX = skeletonSpawnX;
                        skeletonY = skeletonSpawnY;
                        skeletonSpawned = true;
                        enemyX = enemySpawnX;
                        enemyY = enemySpawnY;
                        nurseX = nurseSpawnX;
                        nurseY = nurseSpawnY;
                        nurseSpawned = true;
                        doctorX = doctorSpawnX;
                        doctorY = doctorSpawnY;
                        doctorSpawned = true;
                        enemySpawned = true;
                    }
                }

                if (doctorSpawned) {
                    for (Rectangle doctor : doctors) {
                        doctorX = doctor.x;
                        doctorY = doctor.y;

                        // Check if the enemy has reached the game over position
                        if (doctorX >= dcLockPositionX && doctorX <= dcLockPositionX + 5 && doctorY >= lockPositionY && doctorY <= lockPositionY + 5) {
                            gameOver = true;
                            game.setScreen(new GameOverScreen(game, this)); //pass reference to BloodDrive to GameOverScreen
                            firstLevelMusic.stop();
                        }

                        // Check if the mouse is pressed and if it's within the enemy's bounds
                        if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
                            float mouseX = Gdx.input.getX();
                            float mouseY = Gdx.graphics.getHeight() - Gdx.input.getY();

                            if (!(mouseX >= characterX && mouseX <= characterX + characterWidth &&
                                    mouseY >= characterY && mouseY <= characterY + characterHeight)) {
                                if (mouseX >= doctorX && mouseX <= doctorX + doctorWidth &&
                                        mouseY >= doctorY && mouseY <= doctorY + doctorHeight) {
                                    score += 1;
                                    doctors.removeValue(doctor, true);
                                }
                            }
                        }

                        // Only render the enemy if it's still spawned
                        if (doctorSpawned) {
                            batch.draw(doctorWalkUpAnimation.getKeyFrame(stateTime, true),
                                    doctorX,
                                    Math.max(barrierBottom, Math.min(doctorY, barrierTop)),
                                    doctorWidth,
                                    doctorHeight);

                            doctorY += speed * delta * 15 * speedFactor;
                            doctor.y = doctorY;
                        }
                    }
                }

                if (nurseSpawned) {
                    for (Rectangle nurse : nurses) {
                        nurseX = nurse.x;
                        nurseY = nurse.y;

                        // Check if the enemy has reached the game over position
                        if (nurseX >= dcLockPositionX && nurseX <= dcLockPositionX + 5 && nurseY >= lockPositionY && nurseY <= lockPositionY + 5) {
                            gameOver = true;
                            // game.setScreen(new GameOverScreen(game));
                            game.setScreen(new GameOverScreen(game, this)); //pass reference to BloodDrive to GameOverScreen
                            firstLevelMusic.stop();
                        }

                        // Check if the mouse is pressed and if it's within the enemy's bounds
                        if (Gdx.input.isButtonPressed(Input.Buttons.LEFT)) {
                            float mouseX = Gdx.input.getX();
                            float mouseY = Gdx.graphics.getHeight() - Gdx.input.getY();

                            if (!(mouseX >= characterX && mouseX <= characterX + characterWidth &&
                                    mouseY >= characterY && mouseY <= characterY + characterHeight)) {
                                if (mouseX >= nurseX && mouseX <= nurseX + nurseWidth &&
                                        mouseY >= nurseY && mouseY <= nurseY + nurseHeight) {
                                    score += 1;
                                    nurses.removeValue(nurse, true);
                                }
                            }
                        }

                        // Only render the enemy if it's still spawned
                        if (nurseSpawned) {
                            batch.draw(nurseWalkDownAnimation.getKeyFrame(stateTime, true),
                                    nurseX,
                                    Math.max(barrierBottom, Math.min(nurseY, barrierTop)),
                                    nurseWidth,
                                    nurseHeight);

                            nurseY -= speed * delta * 15 * speedFactor;
                            nurse.y = nurseY;
                        }
                    }
                }

                if (skeletonSpawned) {
                    for (Rectangle skeleton : skeletons) {
                        skeletonX = skeleton.x;
                        skeletonY = skeleton.y;

                        // Check if the enemy has reached the game over position
                        if (skeletonX >= lockPositionX && skeletonX <= lockPositionX + 5 && skeletonY >= lockPositionY && skeletonY <= lockPositionY) {
                            gameOver = true;
                            game.setScreen(new GameOverScreen(game, this)); //pass reference to BloodDrive to GameOverScreen
                            firstLevelMusic.stop();
                        }

                        // Check if the mouse is pressed and if it's within the enemy's bounds
                        if (Gdx.input.isButtonPressed(Input.Buttons.LEFT)) {
                            float mouseX = Gdx.input.getX();
                            float mouseY = Gdx.graphics.getHeight() - Gdx.input.getY();

                            if (!(mouseX >= characterX && mouseX <= characterX + characterWidth &&
                                    mouseY >= characterY && mouseY <= characterY + characterHeight)) {
                                if (mouseX >= skeletonX && mouseX <= skeletonX + skeletonWidth &&
                                        mouseY >= skeletonY && mouseY <= skeletonY + skeletonHeight) {
                                    score += 1;
                                    skeletons.removeValue(skeleton, true);
                                }
                            }
                        }

                        // Only render the enemy if it's still spawned
                        if (skeletonSpawned) {
                            batch.draw(skeletonWalkLeftAnimation.getKeyFrame(stateTime, true),
                                    skeletonX,
                                    Math.max(barrierBottom, Math.min(skeletonY, barrierTop)),
                                    skeletonWidth,
                                    skeletonHeight);

                            skeletonX -= speed * delta * 15 * speedFactor;
                            skeleton.x = skeletonX;
                        }
                    }
                }

                if (enemySpawned) {
                    for (Rectangle enemy : enemies) {
                        enemyX = enemy.x;
                        enemyY = enemy.y;

                        // Check if the enemy has reached the game over position
                        if (enemyX >= lockPositionX && enemyX <= lockPositionX + 5 && enemyY >= lockPositionY && enemyY <= lockPositionY + 5) {
                            gameOver = true;
                            game.setScreen(new GameOverScreen(game, this)); //pass reference to BloodDrive to GameOverScreen
                            firstLevelMusic.stop();
                        }


                        // Check if the mouse is pressed and if it's within the enemy's bounds
                        if (Gdx.input.isButtonPressed(Input.Buttons.LEFT)) {
                            float mouseX = Gdx.input.getX();
                            float mouseY = Gdx.graphics.getHeight() - Gdx.input.getY();

                            if (!(mouseX >= characterX && mouseX <= characterX + characterWidth &&
                                    mouseY >= characterY && mouseY <= characterY + characterHeight)) {
                                if (mouseX >= enemyX && mouseX <= enemyX + enemyWidth &&
                                        mouseY >= enemyY && mouseY <= enemyY + enemyHeight) {
                                    score += 1;
                                    enemies.removeValue(enemy, true);
                                }
                            }
                        }

                        // Only render the enemy if it's still spawned
                        if (enemySpawned) {
                            batch.draw(enemyWalkRightAnimation.getKeyFrame(stateTime, true),
                                    Math.max(barrierLeft, Math.min(enemyX, barrierRight)),
                                    Math.max(barrierBottom, Math.min(enemyY, barrierTop)),
                                    enemyWidth,
                                    enemyHeight);

                            enemyX += speed * delta * 15 + speedFactor;
                            enemy.x = enemyX;
                        }
                    }
                }
            }

            // Draw the message in a white font at the top left
            game.font.setColor(Color.WHITE);
            game.font.draw(batch, "Hit the ESC key to return to the main menu.", 20, TiledGame.GAMEHEIGHT - 20);

            // Draw the crosshair if the mouse is on the screen and character is at or past (360, 0)
            if (isMouseOnScreen && characterX >= 360) {
                float mouseX = Gdx.input.getX() - crosshairWidth / 2f;
                float mouseY = TiledGame.GAMEHEIGHT - Gdx.input.getY() - crosshairHeight / 2f;

                float clampedMouseX = Math.max(barrierLeft, Math.min(mouseX, barrierRight));
                float clampedMouseY = Math.max(barrierBottom, Math.min(mouseY, barrierTop));

                batch.draw(crosshairTexture, clampedMouseX, clampedMouseY, crosshairWidth, crosshairHeight);
            }
            explosionDurationElapsed += delta;
            for (Rectangle explosion : explosions) {
                explosionX = explosion.x;
                explosionY = explosion.y;
                if (explosionDurationElapsed < explosionDuration) {
                    batch.draw(explosionAnimation.getKeyFrame(stateTime, true),
                            Math.max(barrierLeft, Math.min(explosionX, barrierRight)),
                            Math.max(barrierBottom, Math.min(explosionY, barrierTop)),
                            explosionWidth,
                            explosionHeight);
                }
            }
        }

        if (monstersSlowed) {
            font.draw(batch, "The Monsters are slowed down for " + (int) delay + " seconds", 250, 200);
        }

        batch.end();

        stateTime += delta;  // Accumulate elapsed animation time

        // Capture or release the cursor based on mouse presence and character position
        if (isMouseOnScreen && characterX >= 360 && !gameOver) {
            Gdx.input.setCursorCatched(true);  // Capture the cursor
        } else {
            Gdx.input.setCursorCatched(false);  // Release the cursor
        }

        nextEnemySpawnTimeElapsed += delta;
        if (nextEnemySpawnTimeElapsed >= nextEnemySpawnTime)
            spawnEnemy();

        nextSkeletonSpawnTimeElapsed += delta;
        if (nextSkeletonSpawnTimeElapsed >= nextSkeletonSpawnTime)
            spawnSkeleton();

        nextNurseSpawnTimeElapsed += delta;
        if (nextNurseSpawnTimeElapsed >= nextNurseSpawnTime)
            spawnNurse();

        nextDoctorSpawnTimeElapsed += delta;
        if (nextDoctorSpawnTimeElapsed >= nextDoctorSpawnTime)
            spawnDoctor();

        timeSurvived += delta;

        //Start monster slowdown
        if (score > 0 && score % 20 == 0)  {
            monstersSlowed = true;
            speedFactor = 0.5f;
            monstersSlowedTimer = 0;
            delay = 5;
        }

        //End monster slowdown
        monstersSlowedTimer += delta;
        delay -= delta;
        if (monstersSlowedTimer > monstersSlowedDuration) {
            monstersSlowed = false;
            speedFactor = 1;
        }

    }

    private void handleInput(float delta) {
        float characterSpeed = 70 * delta;

        if (!gameOver) {
            characterX += characterSpeed;
            characterX = Math.max(barrierLeft, Math.min(characterX, barrierRight));

            if (characterX >= lockPositionX && characterY >= lockPositionY) {
                characterY = lockPositionY;
                characterX = lockPositionX;
            }

            if (Gdx.input.isKeyPressed(Input.Keys.ESCAPE)) {
                game.setScreen(new MainMenuScreen(game));
                firstLevelMusic.stop();
                bulletSound.stop();
            }
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
        batch.dispose();
        floorTexture.dispose();
        firstLevelMusic.dispose();
        characterSheet.dispose();
        enemySheet.dispose();
        skeletonSheet.dispose();
        nurseSheet.dispose();
        doctorSheet.dispose();
        crosshairTexture.dispose();
        bulletSound.dispose();
        helpMeTexture.dispose();
        explosionSheet.dispose();
    }
}
