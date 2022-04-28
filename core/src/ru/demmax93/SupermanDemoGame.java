package ru.demmax93;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.utils.ScreenUtils;
import ru.demmax93.screens.GameScreen;

public class SupermanDemoGame extends ApplicationAdapter {
    public Screen screen;

    public void setScreen(Screen screen) {
        if (this.screen != null) {
            this.screen.hide();
            this.screen.dispose();
        }
        this.screen = screen;
    }

    @Override
    public void create() {
        Assets.load();
        Assets.loadModel("BaseMesh_Anim.g3dj");
        setScreen(new GameScreen(this));
    }

    @Override
    public void render() {
        ScreenUtils.clear(Color.WHITE);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
        screen.render(Gdx.graphics.getDeltaTime());
    }

    @Override
    public void dispose() {
        Assets.dispose();
    }
}
