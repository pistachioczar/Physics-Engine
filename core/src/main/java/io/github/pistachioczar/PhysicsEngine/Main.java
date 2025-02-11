package io.github.pistachioczar.PhysicsEngine;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Main extends ApplicationAdapter {

    ShapeRenderer shape;
    int screenWidth = 1800;
    int screenHeight = 800;
    ScreenViewport screen;
    Circle[] circles;
    int meter = 5;
    Vec2 gravity = new Vec2(0,-50);
    Force force = new Force();
    float verticalEnergyLoss = .8f;
    float horizontalEnergyLoss = .99f;


    @Override
    public void create(){
        Gdx.graphics.setWindowedMode(screenWidth, screenHeight);
        circles = SpawnBallsRandom(30);

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

        if(circle.EdgeCollisionX(screenWidth)){
            circle.velocity.x *= -1;
            if(circle.pos.x - circle.radius <= 0){
                circle.pos.x = circle.radius;
            }
            if(circle.pos.x + circle.radius >= screenWidth){
                circle.pos.x = screenWidth - (circle.radius + 2);
            }
        }
        if(circle.EdgeCollisionY(screenHeight)){
            if(circle.IsOnGround()){
                circle.pos.y = circle.radius + 2;
            }else{
                circle.pos.y = screenHeight - (circle.radius+2);
            }
            circle.velocity.y *= -verticalEnergyLoss;
            circle.velocity.x *= horizontalEnergyLoss;
        }
        for (int i = 0; i < circles.length; i++) {
            for (int j = i + 1; j < circles.length; j++) {
                circles[i].ObjectCollision(circles[j]);
            }
        }

        if(!circle.IsOnGround()){
            circle.velocity = (Vec2) force.gravity(circle.velocity, gravity);
        }
    }

    public Circle[] SpawnBallsRandom(int amount){

        Circle[] circles = new Circle[amount];
        for(int i = 0; i < amount; i++){
            Circle circle;

            Vec2 pos = new Vec2((float) Math.floor(Math.random() * screenWidth), (float) Math.floor(Math.random() * screenHeight));
            int rad = (int)(20 + Math.random() * 10);
            Vec2 velocity = new Vec2((float) Math.floor(50 +Math.random() * 450), 50 + (float) Math.floor(Math.random() * 450));

            circle = new Circle(pos.x, pos.y, rad, (int) velocity.x, (int) velocity.y, gravity);

            circles[i] = circle;
        }

        return circles;
    }


}
