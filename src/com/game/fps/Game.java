package com.game.fps;

import java.awt.event.KeyEvent;

import com.game.fps.input.Controller;
import com.game.fps.input.InputHandler;

public class Game
{
    public int time;
    public Controller controls;

    /* For mouse coordinates */
    private int newX = 0;
    private int oldX = 0;

    public Game()
    {
        controls = new Controller();
    }

    public void tick(boolean[] key)
    {
        time++;

        boolean turnLeft;
        boolean turnRight;

        newX = InputHandler.mouseX;
        if(newX > oldX)
        {
            turnRight = true;
            turnLeft = false;
        }
        else if(newX < oldX)
        {
            turnLeft = true;
            turnRight = false;
        }
        else
        {
            turnRight = false;
            turnLeft = false;
        }
        oldX = newX;

        boolean forward = key[KeyEvent.VK_W];
        boolean back = key[KeyEvent.VK_S];
        boolean left = key[KeyEvent.VK_A];
        boolean right = key[KeyEvent.VK_D];

        controls.tick(forward, back, left, right, turnLeft, turnRight);
    }
}
