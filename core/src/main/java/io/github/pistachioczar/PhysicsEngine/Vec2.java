package io.github.pistachioczar.PhysicsEngine;

import com.badlogic.gdx.math.Vector2;

public class Vec2 extends Vector2 {

    public Vec2(float x, float y) {
        super(x, y);
    }

    public Vec2() {
        this.x = 0;
        this.y = 0;
    }

    public Vec2(Vec2 vector) {
        this.x = vector.x;
        this.y = vector.y;
    }


    public static Vec2 Sub(Vec2 pos1, Vec2 pos2) {
        return new Vec2(pos1.x - pos2.x, pos1.y - pos2.y);
    }

    /**
     * Multiplies this vector by a scalar value
     * @param scalar The scalar value to multiply by
     * @return This vector for chaining
     */
    public Vec2 Multiply(float scalar) {
        this.x *= scalar;
        this.y *= scalar;
        return this;
    }

    @Override
    public Vec2 cpy() {
        return new Vec2(this.x, this.y);
    }

}
