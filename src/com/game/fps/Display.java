package com.game.fps;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;

import javax.swing.JFrame;

import com.game.fps.graphics.Screen;
import com.game.fps.input.InputHandler;

public class Display extends Canvas implements Runnable
{
    private static final long serialVersionUID = 1L;

    public static final int WIDTH = 600;
    public static final int HEIGHT = 480;
    // public static final int WIDTH = 800;
    // public static final int HEIGHT = 600;
    public static final String TITLE = "FPS Test";

    private Thread thread;
    private Game game;
    private boolean running = false;
    private BufferedImage img;
    private Screen screen;
    private int[] pixels;
    private InputHandler input;
    private int fps;
    
    /* Debugging toggles */
    private boolean displayFPS = true;

    public Display()
    {
        Dimension size = new Dimension(WIDTH, HEIGHT);
        setPreferredSize(size);
        setMinimumSize(size);
        setMaximumSize(size);

        screen = new Screen(WIDTH, HEIGHT);
        game = new Game();
        this.img = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
        pixels = ((DataBufferInt)img.getRaster().getDataBuffer()).getData();

        input = new InputHandler();
        addKeyListener(input);
        addFocusListener(input);
        addMouseListener(input);
        addMouseMotionListener(input);
    }

    private void start()
    {
        if(running)
        {
            return;
        }
        else
        {
            running = true;
            thread = new Thread(this);
            thread.start();
        }
    }

    private void stop()
    {
        if(!running)
        {
            return;
        }
        else
        {
            running = false;

            try
            {
                thread.join();
            }
            catch(Exception ex)
            {
                ex.printStackTrace();
                System.exit(0);
            }
        }
    }

    /* Main game loop and frames/second counter */
    public void run()
    {
        int frames = 0;
        double unprocessedSeconds = 0;
        long previousTime = System.nanoTime();
        double secondsPerTick = 1 / 60.0;
        int tickCount = 0;
        boolean ticked = false;
        requestFocus();
        
        while(running)
        {
            long currentTime = System.nanoTime(); /* Time in nanoseconds */
            long passedTime = currentTime - previousTime; /* Duration of last iteration */
            previousTime = currentTime;
            unprocessedSeconds += passedTime / 1000000000.0; /* passedTime converted to seconds */

            /* advances game clock if needed */
            while(unprocessedSeconds > secondsPerTick)
            {
                tick();
                unprocessedSeconds -= secondsPerTick;
                ticked = true;
                tickCount++;

                /* Calculates frames per second */
                if(tickCount % 60 == 0)
                {
                    fps = frames;
                    previousTime += 1000;
                    frames = 0;
                }
            }

            frames = updateScreen(frames, ticked);            
        }
    }

    /* Renders screen and increments frame count */
    private int updateScreen(int frames, boolean ticked)
    {
        if(ticked)
        {
            render();
            frames++;
        }
        render();
        frames++;

        return frames;
    }

    private void tick()
    {
        game.tick(input.key); /* advances game clock */
    }

    private void render()
    {
        BufferStrategy bs = this.getBufferStrategy();

        if(bs == null)
        {
            createBufferStrategy(3);
            return;
        }

        screen.render(game);

        for(int i = 0; i < WIDTH * HEIGHT; i++)
        {
            pixels[i] = screen.pixels[i];
        }

        Graphics g = bs.getDrawGraphics();
        g.drawImage(img, 0, 0, WIDTH, HEIGHT, null);
        
        if(displayFPS)
        {
            showFPS(g);
        }
        
        g.dispose();
        bs.show();
    }

    private void showFPS(Graphics g)
    {
        g.setFont(new Font("Verdana", 0, 25));
        g.setColor(Color.YELLOW);
        g.drawString(fps + "fps", 7, 25);
    }

    public static void main(String[] args)
    {
        BufferedImage cursor = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
        Cursor blank = Toolkit.getDefaultToolkit().createCustomCursor(cursor, new Point(0, 0), "blank");
        Display game = new Display();
        JFrame frame = new JFrame();

        frame.add(game);
        frame.pack();
        frame.getContentPane().setCursor(blank);
        frame.setTitle(TITLE);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false); /* TODO maybe change to true */
        frame.setLocationRelativeTo(null);
        frame.setAlwaysOnTop(true); /* TODO remove when finished */
        frame.setVisible(true);

        System.out.println("Running...");

        game.start();
    }
}
