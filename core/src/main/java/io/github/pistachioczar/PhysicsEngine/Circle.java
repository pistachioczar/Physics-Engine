package io.github.pistachioczar.PhysicsEngine;

public class Circle extends Object{
    public int radius;
    public int eps = 1;
    public Vec2 velocity;
    public float kg;
    public float kineticEnergy;
    public float potentialEnergy;
    public float totalEnergy;
    public float momentum;
    public float collisionLoss;
    //Constructor with input
    Circle(float xPos, float yPos, int rad, int speedX, int speedY, float collisionLoss) {
        this.pos.x = xPos;
        this.pos.y = yPos;
        this.radius = rad;
        this.velocity = new Vec2(speedX, speedY);
        this.kg  = (float) (Math.PI * (radius * radius));
        this.kineticEnergy = (float) (.5 * kg * velocity.len());
        this.potentialEnergy = kg*pos.y;
        this.totalEnergy = potentialEnergy + kineticEnergy;
        this.momentum = kg * velocity.len();
        this.collisionLoss = collisionLoss;

    }

    //Default constructor
    Circle(){
        super();
        radius = 50;
        pos.x = 0;
        pos.y = 0;
        velocity = new Vec2(0,0);
    }

    //edge collision with side walls
    public boolean EdgeCollisionX(int screenWidth) {
        return this.pos.x + radius >= screenWidth - eps || this.pos.x - radius < eps;
    }

    //edge collision with roof or floor
    public boolean EdgeCollisionY(int screenHeight) {
        return this.pos.y + radius >= screenHeight - eps || this.pos.y - radius <= eps;
    }

    //checks if object is on ground.
    public boolean IsOnGround() {
        return this.pos.y - radius <= eps ;
    }

    /*
        Handles collisions between a circle and another circle using vector math.
        It calculates the normal vector between them and their relative velocities.
     */
    public void ObjectCollision(Circle circle) {
        float dist = this.pos.dst(circle.pos);

        if (dist <= (circle.radius + this.radius)) {
            Vec2 normal = (Vec2) Vec2.Sub(circle.pos, this.pos).nor(); // Normalized collision normal
            Vec2 relativeVelocity = Vec2.Sub(circle.velocity, this.velocity);

            float velocityAlongNormal = relativeVelocity.dot(normal);

            // If objects are moving apart, skip collision response
            if (velocityAlongNormal > 0) return;

            //  impulse scalar
            float j = -(1 + collisionLoss) * velocityAlongNormal; // collision loss is a number between 0 and 1 where 1 means no energy is lost and 0 means all is lost
            j /= 1/this.kg + 1/circle.kg;

            // Apply impulse
            Vec2 impulse = normal.cpy().Multiply(j);
            this.velocity.add(impulse.cpy().Multiply(-1.0f/this.kg));
            circle.velocity.add(impulse.cpy().Multiply(1.0f/circle.kg));
        }
    }



}
