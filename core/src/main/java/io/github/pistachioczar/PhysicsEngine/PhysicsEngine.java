package io.github.pistachioczar.PhysicsEngine;

import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;
import java.util.List;

public class PhysicsEngine {

    public Force force = new Force();
    public int screenWidth, screenHeight;
    public int meter = 5;
    public Vec2 gravity;
    public boolean energyLoss;
    public float indirectLoss;
    public float directLoss;
    public int ballRad = 2;
    public List<Circle> circles = new ArrayList<>();

    public PhysicsEngine(int screenWidth, int screenHeight, Vec2 gravity, boolean energyLoss, float indirectLoss, float directLoss) {
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
        this.gravity = gravity;
        this.energyLoss = energyLoss;
        this.indirectLoss = indirectLoss;
        this.directLoss = directLoss;
    }

    public void update(double deltaTime){
        for(Circle circle : circles){
            circle.pos.x += (float) (circle.velocity.x * deltaTime);
            circle.pos.y += (float) (circle.velocity.y * deltaTime);
            wallCollision(circle);

            for(Circle circle2 : circles){
                circle.ObjectCollision(circle2);
            }
            if(!circle.IsOnGround()){
                circle.velocity = (Vec2) force.gravity(circle.velocity, gravity);
            }
        }

    }

    public void wallCollision(Circle circle){
        float newDirectLoss = energyLoss ? computeLoss() : 1f;
        float newIndirectLoss = energyLoss ? .9f : 1f;

        if(circle.EdgeCollisionX(this.screenWidth)){
            circle.velocity.x *= -newDirectLoss;
            circle.velocity.y *= newIndirectLoss;

            if(circle.pos.x - circle.radius <= 0){
                circle.pos.x = circle.radius;
            }
            if(circle.pos.x + circle.radius >= this.screenWidth){
                circle.pos.x = this.screenWidth - (circle.radius);
            }
        }

        if(circle.EdgeCollisionY(this.screenHeight)){
            if(circle.IsOnGround()){
                circle.pos.y = circle.radius ;
            }else{
                circle.pos.y = this.screenHeight - (circle.radius + 2);
            }

            circle.velocity.y *= -newDirectLoss;
            circle.velocity.x *= newIndirectLoss;
        }
    }


    public void addBall(int x, int y, Vector2 velocity){
        Circle circle;
        float loss = 1f;

        if (!energyLoss){
            loss = .8f;
        }

        circle = new Circle(x, y, this.ballRad*meter, (int) velocity.x, (int) velocity.y, loss);
        this.circles.add(circle);
    }

    public void changeLoss() {
        if(energyLoss) {
            for (Circle circle : circles) {
                circle.collisionLoss = .8f;
            }
        } else {
            for (Circle circle : circles) {
                circle.collisionLoss = 1f;
            }
        }
        energyLoss = !energyLoss;
        this.directLoss = computeLoss();
        this.indirectLoss = .9f;
    }

    private float computeLoss() {
        float gravityMagnitude = gravity.len();
        float maxLoss = 0.9f;

        return 1f - Math.min(maxLoss, gravityMagnitude * 0.005f);
    }
}
