package io.github.pistachioczar.PhysicsEngine;

import java.util.*;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Input.*;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Main extends ApplicationAdapter {

    ShapeRenderer shape;
    Batch batch;
    static int screenWidth;
    static int screenHeight;
    ScreenViewport screen;
    static List<Circle> circles;
    static int meter = 5;
    static Vec2 gravity = new Vec2(0,0);
    static boolean collisionEnergyLoss = false;
    static int ballRad = 2;
    static DragInput dragInput = new DragInput();
    static boolean pause = false;
    BitmapFont font;
    static ColorScheme[] colors;
    static int colorSelection;
    static PhysicsEngine engine;


    @Override
    public void create(){
        Gdx.graphics.setFullscreenMode(Gdx.graphics.getDisplayMode());
        screenWidth = Gdx.graphics.getWidth();
        screenHeight = Gdx.graphics.getHeight();
        circles = new ArrayList<>();
        shape = new ShapeRenderer();
        batch = new SpriteBatch();
        screen = new ScreenViewport();
        font = new BitmapFont();
        Gdx.input.setInputProcessor(dragInput);
        ColorScheme background1 = new ColorScheme(Color.BLACK, Color.GOLD);
        ColorScheme background2 = new ColorScheme(Color.WHITE, Color.BLACK);
        ColorScheme background3 = new ColorScheme(Color.BLACK, Color.WHITE);
        colors = new ColorScheme[] {background1, background2, background3};
        colorSelection = 0;
        engine = new PhysicsEngine(screenWidth, screenHeight, gravity, false, 1f, 1f);


    }

    @Override
    public void render(){
        ScreenUtils.clear(colors[colorSelection].BackgroundColor);
        update(Gdx.graphics.getDeltaTime());

        shape.begin(ShapeRenderer.ShapeType.Filled);
        for (Circle circle : engine.circles) {
            shape.setColor(colors[colorSelection].ObjectColor);
            shape.circle(circle.pos.x, circle.pos.y, circle.radius, 30);
        }
        renderBallSpawn(shape);
        if(pause){
            renderPause(shape);
        }

        shape.end();

        batch.begin();
        font.getData().setScale(2);
        font.draw(batch, Integer.toString(engine.circles.size()), 30, screenHeight - 30);
        font.draw(batch, "Energy Loss: " + engine.energyLoss, 30, screenHeight - 60);
        font.draw(batch, "Gravity: " + engine.gravity.y/-10, 30, engine.screenHeight - 90);
        batch.end();

    }

    public static void renderPause(ShapeRenderer shape){
        shape.setColor(Color.WHITE);
        float pauseWidth = 15;
        float pauseHeight = 50;

        Vector2 center = new Vector2((float) screenWidth / 2, (float) screenHeight / 2);
        shape.rect(center.x + 15, center.y - pauseHeight/2, pauseWidth,pauseHeight);
        shape.rect(center.x - 15, center.y - pauseHeight/2, pauseWidth,pauseHeight);
    }

    public static void renderBallSpawn(ShapeRenderer shape){
        if(dragInput.isDragging()){
            shape.setColor(Color.LIGHT_GRAY);
            shape.circle(dragInput.getInitialMousePosition().x, screenHeight - dragInput.getInitialMousePosition().y, engine.ballRad*meter);

            Vector2 currentMousePosition = new Vector2(Gdx.input.getX(), screenHeight - Gdx.input.getY());
            Vector2 initialMousePosition = new Vector2(dragInput.getInitialMousePosition().x, screenHeight - dragInput.getInitialMousePosition().y);
            shape.rectLine(initialMousePosition, currentMousePosition, ballRad);
        }
    }


    @Override
    public void dispose(){
        shape.dispose();
    }

    public void update(double deltaTime){


        keyPressDetection();
        if(!pause){
            engine.update(deltaTime);
        }
    }

    @Override
    public void resize(int width, int height) {
        screen.update(screenWidth, screenHeight, true);
    }


    public static void keyPressDetection(){

        if(!dragInput.isDragging() && dragInput.getDragDifference() != null){
            //Subtract y from screen height because the getY returns coords with top left origin, and it needs to be translated
            //to bottom left origin coordinates
            engine.addBall(ballRad, (int) dragInput.getInitialMousePosition().x, (int) (screenHeight - dragInput.getInitialMousePosition().y), dragInput.getDragDifference().scl(2));
            //makes it so drag difference goes back to null.
            dragInput.resetDrag();
        } else if(Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT)){
            engine.addBall(ballRad, Gdx.input.getX(), screenHeight - Gdx.input.getY(),new Vector2(0,0));
        }
        else if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)){
            if (collisionEnergyLoss) {
                engine.horizontalEnergyLoss = 1;
                engine.verticalEnergyLoss = 1;
                engine.energyLoss = false;
            } else {
                engine.horizontalEnergyLoss = .95f;
                engine.verticalEnergyLoss = .8f;
                engine.energyLoss = true;
            }
            engine.changeLoss();
        }else if (Gdx.input.isKeyJustPressed(Input.Keys.A)){
            engine.gravity.y -= 2*meter;
        }else if (Gdx.input.isKeyJustPressed(Input.Keys.S)){
            if(engine.gravity.y < 0){
                engine.gravity.y += 2*meter;
            } else {
                engine.gravity.y = 0;
            }
        }else if(Gdx.input.isKeyJustPressed(Input.Keys.P)) {
            pause = !pause;
        }else if(Gdx.input.isKeyJustPressed(Input.Keys.NUM_1)){
            engine.ballRad = 2;
        }else if(Gdx.input.isKeyJustPressed(Input.Keys.NUM_2)){
            engine.ballRad = 4;
        }else if(Gdx.input.isKeyJustPressed(Input.Keys.NUM_3)){
            engine.ballRad = 6;
        }else if(Gdx.input.isKeyJustPressed(Input.Keys.NUM_4)){
            engine.ballRad = 8;
        }else if(Gdx.input.isKeyJustPressed(Input.Keys.NUM_5)){
            engine.ballRad = 10;
        }else if(Gdx.input.isKeyJustPressed(Input.Keys.NUM_6)){
            engine.ballRad = 12;
        }else if(Gdx.input.isKeyJustPressed(Input.Keys.NUM_7)){
            engine.ballRad = 14;
        } else if(Gdx.input.isKeyJustPressed(Input.Keys.NUM_8)){
            engine.ballRad = 16;
        } else if(Gdx.input.isKeyJustPressed(Input.Keys.NUM_9)){
            engine.ballRad = 18;
        } else if(Gdx.input.isKeyJustPressed(Input.Keys.R)){
            engine.circles.clear();
        }else if(Gdx.input.isKeyJustPressed(Input.Keys.C)){
            colorSelection += 1;
            if(colorSelection > colors.length-1){
                colorSelection = 0;
            }
        }else if(Gdx.input.isKeyJustPressed(Input.Keys.Q)){
            Gdx.app.exit();
            System.exit(-1);
        }
    }



}
