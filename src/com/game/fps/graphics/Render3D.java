package com.game.fps.graphics;

import com.game.fps.Game;

public class Render3D extends Render
{
    public double[] zBuffer;
    private double renderDistance = 5000;

    public Render3D(int width, int height)
    {
        super(width, height);
        zBuffer = new double[width * height];
    }

    /* Renders floor and ceiling */
    public void floor(Game game)
    {
        /* Floor/ceiling height variables */
        double ceilingPosition = 8.0;
        double floorPosition = 10.0;

        /* Rotation variables */
        double rotation = game.controls.rotation;// game.time / 100.0;
        double cosine = Math.cos(rotation);
        double sine = Math.sin(rotation);

        double forward = game.controls.z;
        double right = game.controls.x;

        for(int y = 0; y < height; y++)
        {
            double ceiling = (y - height / 2.0) / height; /* apparent size of tiles */
            double z = floorPosition / ceiling; /* Sets depth into the screen */

            /* Sets height of ceiling */
            if(ceiling < 0)
            {
                z = ceilingPosition / -ceiling;
            }

            for(int x = 0; x < width; x++)
            {
                double depth = (x - width / 2.0) / height; /* sets depth of view proportions */
                depth *= z; /* shrinks apparent height of ceiling further away */
                double xx = depth * cosine + z * sine;
                double yy = z * cosine - depth * sine;
                int yPix = (int)(yy + forward);
                int xPix = (int)(xx + right);
                zBuffer[x + y * width] = z;
                pixels[x + y * width] = ((xPix & 15) * 16) | ((yPix & 15) * 16) << 8; /* bitwise operators shift colors */
            }
        }
    }

    public void renderDistanceLimiter()
    {
        for(int i = 0; i < width * height; i++)
        {
            int color = pixels[i];
            int brightness = (int)(renderDistance / zBuffer[i]);

            if(brightness < 0)
            {
                brightness = 0;
            }
            if(brightness > 255)
            {
                brightness = 255;
            }

            int r = (color >> 16) & 0xff;
            int g = (color >> 8) & 0xff;
            int b = (color) & 0xff;

            r = r * brightness / 255;
            g = g * brightness / 255;
            b = b * brightness / 255;

            pixels[i] = r << 16 | g << 8 | b;
        }
    }
}
