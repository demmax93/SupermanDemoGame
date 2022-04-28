package ru.demmax93.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import ru.demmax93.GameWorld;
import ru.demmax93.Settings;
import ru.demmax93.SupermanDemoGame;
import ru.demmax93.ui.GameUI;

public class GameScreen implements Screen {
    SupermanDemoGame game;
    GameUI gameUI;
    GameWorld gameWorld;

    public GameScreen(SupermanDemoGame game) {
        this.game = game;
        this.gameUI = new GameUI(game);
        this.gameWorld = new GameWorld(gameUI);
        Settings.Paused = false;
        Gdx.input.setInputProcessor(gameUI.stage);
        Gdx.input.setCursorCatched(false);
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        /** Updates */
        gameUI.update(delta);
        /** Draw */
        gameWorld.render(delta);
        gameUI.render();
    }

    @Override
    public void resize(int width, int height) {
        gameUI.resize(width, height);
        gameWorld.resize(width, height);
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        gameWorld.dispose();
        gameUI.dispose();
    }
}
