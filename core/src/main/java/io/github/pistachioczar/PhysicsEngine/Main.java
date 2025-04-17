package io.github.pistachioczar.PhysicsEngine;

import java.util.*;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Input.*;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Main extends ApplicationAdapter {

    ShapeRenderer shape;
    static int screenWidth = 1800;
    static int screenHeight = 800;
    ScreenViewport screen;
    List<Circle> circles;
    int meter = 5;
    Force force = new Force();
    Vec2 gravity = new Vec2(0,0);
    static float horizontalEnergyLoss = .93f;
    static float verticalEnergyLoss = .8f;

//    Vec2 gravity = new Vec2(0, -5*meter);
//    float verticalEnergyLoss = .8f;
//    float horizontalEnergyLoss = .98f;


    @Override
    public void create(){
        Gdx.graphics.setWindowedMode(screenWidth, screenHeight);
        circles = new ArrayList<>();
        shape = new ShapeRenderer();
        screen = new ScreenViewport();

    }

    @Override
    public void render(){
        ScreenUtils.clear(1,1,1,1);
        update(Gdx.graphics.getDeltaTime());

        shape.begin(ShapeRenderer.ShapeType.Filled);
        for (Circle circle : circles) {
            shape.setColor(Color.MAGENTA);
            shape.circle(circle.pos.x, circle.pos.y, circle.radius, 15);
        }
        shape.end();

    }

    @Override
    public void dispose(){
        shape.dispose();
    }

    public void update(double deltaTime){


        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)){
            circles = spawnBall(circles);
        }else if (Gdx.input.isKeyPressed(Keys.CONTROL_LEFT) && Gdx.input.isKeyJustPressed(Input.Keys.A)){
            gravity.y -= meter;
            System.out.println("test");
        }else if (Gdx.input.isKeyPressed(Keys.CONTROL_LEFT) && Gdx.input.isKeyJustPressed(Input.Keys.S)){
            if(gravity.y < 0){
                gravity.y += meter;
            } else {
                gravity.y = 0;
            }
        }

        for (Circle circle : circles) {
            objectUpdate(deltaTime, circle);
        }
    }

    @Override
    public void resize(int width, int height) {
        screen.update(screenWidth, screenHeight, true); // Update the viewport on resize
        screenWidth = (int) screen.getWorldWidth(); // Get the viewport width
        screenHeight = (int) screen.getWorldHeight(); // Get the viewport height
    }

    public void objectUpdate(double deltaTime, Circle circle){
        circle.pos.x += (float) (circle.velocity.x * deltaTime);
        circle.pos.y += (float) (circle.velocity.y * deltaTime);

        wallCollision(circle);

        int n = circles.size();
        for (int i = 0; i < n; i++) {
            for (int j = i + 1; j < n; j++) {
                circles.get(i).ObjectCollision(circles.get(j));
            }
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

    public List<Circle> spawnBall(List<Circle> circles){

            Circle circle;

            Vec2 pos = new Vec2((float) Math.floor(Math.random() * screenWidth), (float) Math.floor(Math.random() * screenHeight));
            int rad = (int)(10 + Math.random() * 20);
            Vec2 velocity = new Vec2((float) Math.floor(50 + Math.random() * 800), 50 + (float) Math.floor(Math.random() * 800));

            circle = new Circle(pos.x, pos.y, rad, (int) velocity.x, (int) velocity.y, 1);

             circles.add(circle);

        return circles;
    }


}
