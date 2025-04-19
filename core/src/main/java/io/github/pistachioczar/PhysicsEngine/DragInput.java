package io.github.pistachioczar.PhysicsEngine;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.math.Vector2;

public class DragInput implements InputProcessor {

    private final Vector2 initialMousePosition = new Vector2();
    private final Vector2 finalMousePosition = new Vector2();
    private boolean isDragging = false;
    private final Vector2 dragDifference = new Vector2();
    private boolean dragCompleted = false; // New flag



    public Vector2 getInitialMousePosition() {
        return initialMousePosition.cpy();
    }

    public Vector2 getFinalMousePosition() {
        return finalMousePosition.cpy();
    }

    public void resetDrag(){
        dragCompleted = false;

    }

    public boolean isDragging() {
        return isDragging;
    }

    public Vector2 getDragDifference() {
        return dragCompleted ? dragDifference.cpy() : null;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        if (pointer == 0 && button == Input.Buttons.LEFT) {
            initialMousePosition.set(screenX, screenY);
            isDragging = true;
            dragCompleted = false;
            return true;
        }
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return isDragging && pointer == 0;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        if (isDragging && pointer == 0 && button == Input.Buttons.LEFT) {
            finalMousePosition.set(screenX, screenY);
            isDragging = false;
            dragDifference.set(finalMousePosition).sub(initialMousePosition);
            dragDifference.x = -dragDifference.x;
            dragCompleted = true;
            return true;
        }
        return false;
    }

    @Override
    public boolean keyDown(int i) {
        return false;
    }

    @Override
    public boolean keyUp(int i) {
        return false;
    }

    @Override
    public boolean keyTyped(char c) {
        return false;
    }




    @Override
    public boolean touchCancelled(int i, int i1, int i2, int i3) {
        return false;
    }



    @Override
    public boolean mouseMoved(int i, int i1) {
        return false;
    }

    @Override
    public boolean scrolled(float v, float v1) {
        return false;
    }
}
