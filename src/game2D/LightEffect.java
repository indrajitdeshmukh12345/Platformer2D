package game2D;

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class LightEffect {
    private int screenWidth;
    private int screenHeight;
    private List<LightSource> lightSources = new ArrayList<>();
    private boolean effectOn = true;

    public int getLightSourceCount() {
        return lightSources.size();
    }

    // Inner class to store position and radius
    private static class LightSource {
        Point2D position;
        float radius;

        LightSource(float x, float y, float radius) {
            this.position = new Point2D.Float(x, y);
            this.radius = radius;
        }
    }

    public LightEffect(int screenWidth, int screenHeight) {
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
    }

    public void addLightSource(float x, float y, float radius) {
        lightSources.add(new LightSource(x, y, radius));
    }
    public void removeLightSource(int index) {
        lightSources.remove(index);
    }

    public void updateLightSource(int index, float x, float y, float radius, int xOffset, int yOffset) {
        if (index >= 0 && index < lightSources.size()) {
            LightSource light = lightSources.get(index);
            light.position.setLocation(x + xOffset, y + yOffset);
            light.radius = radius;
        }
    }

    public void draw(Graphics2D g) {
        if (!effectOn) {
            g.setColor(new Color(0, 0, 0, 255)); // Full black
            return;
        }

        // Create a transparent black overlay
        BufferedImage lightMap = new BufferedImage(screenWidth, screenHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = lightMap.createGraphics();
        g2.setColor(new Color(0, 0, 0, 255)); // Full black
        g2.fillRect(0, 0, screenWidth, screenHeight);

        // Apply radial light effect for each light source
        for (LightSource light : lightSources) {
            float radius = light.radius;
            Point2D center = light.position;

            // Ensure smooth gradient
            float[] dist = {0.0f, 1.0f};
            Color[] colors = {
                    new Color(0, 0, 0, 255),new Color(0, 0, 0, 0),   // Fully transparent at center
                      // Fully black at edges
            };

            RadialGradientPaint radialGradient = new RadialGradientPaint(center, radius, dist, colors);
            g2.setPaint(radialGradient);
            g2.setComposite(AlphaComposite.DstOut); // Correctly applies the transparency effect
            g2.fillOval((int) (center.getX() - radius), (int) (center.getY() - radius), (int) (radius * 2), (int) (radius * 2));
        }

        g2.dispose();

        // Draw the final light map on the screen
        g.drawImage(lightMap, 0, 0, null);
    }

    public void setEffectOn(boolean on) {
        this.effectOn = on;
    }

    public boolean isEffectOn() {
        return effectOn;
    }
}
