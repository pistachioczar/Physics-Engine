package io.github.pistachioczar.PhysicsEngine;

public class Circle extends Object{
    int radius;
    int eps = 1;
    Vec2 velocity;
    float kg;
    float kineticEnergy;
    float potentialEnergy;
    float totalEnergy;
    float momentum;

    //Constructor with input
    Circle(float xPos, float yPos, int rad, int speedX, int speedY, Vec2 gravity) {
        pos.x = xPos;
        pos.y = yPos;
        radius = rad;
        velocity = new Vec2(speedX, speedY);
        kg  = (float) (Math.PI * (radius * radius));
        kineticEnergy = (float) (.5 * kg * velocity.len());
        potentialEnergy = kg*pos.y;
        totalEnergy = potentialEnergy + kineticEnergy;
        momentum = kg * velocity.len();
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
            float j = -(1 + 1) * velocityAlongNormal; // 1.0f is restitution coefficient
//            float j = -(1 + .7f) * velocityAlongNormal; // 1.0f is restitution coefficient

            j /= 1/this.kg + 1/circle.kg;

            // Apply impulse
            Vec2 impulse = normal.cpy().Multiply(j);
            this.velocity.add(impulse.cpy().Multiply(-1.0f/this.kg));
            circle.velocity.add(impulse.cpy().Multiply(1.0f/circle.kg));
        }
    }



}
