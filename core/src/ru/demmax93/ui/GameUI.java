package ru.demmax93.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.viewport.FitViewport;
import ru.demmax93.Assets;
import ru.demmax93.SupermanDemoGame;

public class GameUI {
    private final SupermanDemoGame game;
    public Stage stage;
    public HealthWidget healthWidget;
    private PauseWidget pauseWidget;
    public GameOverWidget gameOverWidget;
    private Label fpsLabel;

    public GameUI(SupermanDemoGame game) {
        this.game = game;
        stage = new Stage(new FitViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight()));
        setWidgets();
        configureWidgets();
    }

    public void setWidgets() {
        healthWidget = new HealthWidget();
        pauseWidget = new PauseWidget(game, stage);
        gameOverWidget = new GameOverWidget(game, stage);
        fpsLabel = new Label("", Assets.skin);
    }

    public void configureWidgets() {
        healthWidget.setSize(140, 25);
        healthWidget.setPosition(Gdx.graphics.getWidth() / 2 - healthWidget.getWidth() / 2, 0);
        pauseWidget.setSize(64, 64);
        pauseWidget.setPosition(Gdx.graphics.getWidth() - pauseWidget.getWidth(), Gdx.graphics.getHeight() - pauseWidget.getHeight());
        gameOverWidget.setSize(280, 100);
        gameOverWidget.setPosition(Gdx.graphics.getWidth() / 2 - 280 / 2, Gdx.graphics.getHeight() / 2);

        fpsLabel.setPosition(0, 10);

        stage.addActor(healthWidget);
        stage.setKeyboardFocus(pauseWidget);
        stage.addActor(fpsLabel);
    }

    public void update(float delta) {
        fpsLabel.setText("FPS: " + Gdx.graphics.getFramesPerSecond());
        stage.act(delta);
    }

    public void render() {
        stage.draw();
    }

    public void resize(int width, int height) {
        stage.getViewport().update(width, height);
    }

    public void dispose() {
        stage.dispose();
    }
}
