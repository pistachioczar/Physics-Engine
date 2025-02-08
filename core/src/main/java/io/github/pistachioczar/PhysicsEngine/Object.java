package io.github.pistachioczar.PhysicsEngine;

import com.badlogic.gdx.math.Vector2;

public class Object {
    Vec2 pos = new Vec2();

    Object(int xPos, int yPos) {
        pos.x = xPos;
        pos.y = yPos;
    }

    public Object() {
        pos.x = 0;
        pos.y = 0;
    }

    public void SetPos(int x, int y){
        pos.x = x;
        pos.y = y;
    }
}
