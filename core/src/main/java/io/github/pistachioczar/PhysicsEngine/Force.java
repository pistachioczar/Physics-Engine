package io.github.pistachioczar.PhysicsEngine;

import com.badlogic.gdx.math.Vector2;

public class Force {


    public Vector2 gravity(Vec2 vector, Vec2 gravity){
        vector.add(gravity);
        return vector;
    }

}
