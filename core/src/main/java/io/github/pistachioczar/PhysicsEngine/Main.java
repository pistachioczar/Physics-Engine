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
    Force force = new Force();
    static Vec2 gravity = new Vec2(0,0);
    static boolean collisionEnergyLoss = false;
    static float  horizontalEnergyLoss = 1f;
    static float verticalEnergyLoss = 1f;
    static int ballRad = 2;
    static DragInput dragInput = new DragInput();
    static boolean pause = false;
    BitmapFont font;


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


    }

    @Override
    public void render(){
        ScreenUtils.clear(Color.BLACK);
        update(Gdx.graphics.getDeltaTime());

        shape.begin(ShapeRenderer.ShapeType.Filled);
        for (Circle circle : circles) {
            shape.setColor(Color.GOLD);
            shape.circle(circle.pos.x, circle.pos.y, circle.radius, 30);
        }
        renderBallSpawn(shape);
        if(pause){
            renderPause(shape);
        }

        shape.end();

        batch.begin();
        font.getData().setScale(2);
        font.draw(batch, Integer.toString(circles.size()), 30, screenHeight - 30);
        font.draw(batch, "Energy Loss: " + collisionEnergyLoss, 30, screenHeight - 60);
        font.draw(batch, "Gravity: " + gravity.y/-10, 30, screenHeight - 90);
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
            shape.circle(dragInput.getInitialMousePosition().x, screenHeight - dragInput.getInitialMousePosition().y, ballRad*meter);

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
            for (Circle circle : circles) {
                objectUpdate(deltaTime, circle);
            }
        }
    }

    @Override
    public void resize(int width, int height) {
        screen.update(screenWidth, screenHeight, true);
    }

    public void objectUpdate(double deltaTime, Circle circle){
        circle.pos.x += (float) (circle.velocity.x * deltaTime);
        circle.pos.y += (float) (circle.velocity.y * deltaTime);

        wallCollision(circle);

        for (Circle value : circles) {
            circle.ObjectCollision(value);
        }

        if(!circle.IsOnGround()){
            circle.velocity = (Vec2) force.gravity(circle.velocity, gravity);
        }
    }

    public static void wallCollision(Circle circle){
        if(circle.EdgeCollisionX(screenWidth)){
            circle.velocity.x *= -1;
            if(circle.pos.x - circle.radius <= 0){
                circle.pos.x = circle.radius;
            }
            if(circle.pos.x + circle.radius >= screenWidth){
                circle.pos.x = screenWidth - (circle.radius);
            }
        }
        if(circle.EdgeCollisionY(screenHeight)){
            if(circle.IsOnGround()){
                circle.pos.y = circle.radius ;
            }else{
                circle.pos.y = screenHeight - (circle.radius + 2);
            }
            circle.velocity.y *= -verticalEnergyLoss;
            circle.velocity.x *= horizontalEnergyLoss;
        }
    }

    public static void spawnBall(List<Circle> circles, int size, int x, int y, Vector2 velocity){
            Circle circle;
            float loss = 1f;

            if (!collisionEnergyLoss){
                loss = .8f;
            }

            circle = new Circle(x, y, size*meter, (int) velocity.x, (int) velocity.y, loss);
            circles.add(circle);
    }

    public static void changeLoss() {
        if(collisionEnergyLoss) {
            for (Circle circle : circles) {
                circle.collisionLoss = .8f;
            }
        } else {
            for (Circle circle : circles) {
                circle.collisionLoss = 1f;
            }
        }
    }

    public static void keyPressDetection(){

        if(!dragInput.isDragging() && dragInput.getDragDifference() != null){
            //Subtract y from screen height because the getY returns coords with top left origin, and it needs to be translated
            //to bottom left origin coordinates
            spawnBall(circles, ballRad, (int) dragInput.getInitialMousePosition().x, (int) (screenHeight - dragInput.getInitialMousePosition().y), dragInput.getDragDifference().scl(2));
            //makes it so drag difference goes back to null.
            dragInput.resetDrag();
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)){
            if (collisionEnergyLoss) {
                horizontalEnergyLoss = 1;
                verticalEnergyLoss = 1;
                collisionEnergyLoss = false;
            } else {
                horizontalEnergyLoss = .95f;
                verticalEnergyLoss = .8f;
                collisionEnergyLoss = true;
            }
            changeLoss();
        }else if (Gdx.input.isKeyJustPressed(Input.Keys.A)){
            gravity.y -= 2*meter;
        }else if (Gdx.input.isKeyJustPressed(Input.Keys.S)){
            if(gravity.y < 0){
                gravity.y += 2*meter;
            } else {
                gravity.y = 0;
            }
        }else if(Gdx.input.isKeyJustPressed(Input.Keys.P)) {
            pause = !pause;
        }else if(Gdx.input.isKeyJustPressed(Input.Keys.NUM_1)){
            ballRad = 2;
        }else if(Gdx.input.isKeyJustPressed(Input.Keys.NUM_2)){
            ballRad = 4;
        }else if(Gdx.input.isKeyJustPressed(Input.Keys.NUM_3)){
            ballRad = 6;
        }else if(Gdx.input.isKeyJustPressed(Input.Keys.NUM_4)){
            ballRad = 8;
        }else if(Gdx.input.isKeyJustPressed(Input.Keys.NUM_5)){
            ballRad = 10;
        }else if(Gdx.input.isKeyJustPressed(Input.Keys.NUM_6)){
            ballRad = 12;
        }else if(Gdx.input.isKeyJustPressed(Input.Keys.NUM_7)){
            ballRad = 14;
        } else if(Gdx.input.isKeyJustPressed(Input.Keys.NUM_8)){
            ballRad = 16;
        } else if(Gdx.input.isKeyJustPressed(Input.Keys.NUM_9)){
            ballRad = 18;
        } else if(Gdx.input.isKeyJustPressed(Input.Keys.R)){
            circles.clear();
        } else if(Gdx.input.isKeyJustPressed(Input.Keys.Q)){
            Gdx.app.exit();
            System.exit(-1);
        }
    }



}
