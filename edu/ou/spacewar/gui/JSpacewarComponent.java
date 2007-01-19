package edu.ou.spacewar.gui;

import java.awt.*;
import java.util.LinkedList;
import java.util.ArrayList;
import javax.swing.JComponent;

import edu.ou.spacewar.simulator.Object2D;
import edu.ou.spacewar.simulator.Space;
import edu.ou.utils.Vector2D;

public class JSpacewarComponent extends JComponent {
    private static final long serialVersionUID = 1L;

    public static final Color TEXT_COLOR = new Color(0, 218, 159);

    public static final BasicStroke THIN_STROKE = new BasicStroke(1, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
    public static final BasicStroke STROKE = new BasicStroke(3, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
    public static final BasicStroke THICK_STROKE = new BasicStroke(7, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);

    private final ArrayList<Shadow2D> shadows;
    private final LinkedList<Shadow2D> usershadows;

    private boolean showshadows;
    private int width, height;

    public JSpacewarComponent(Space space) {
        this.shadows = new ArrayList<Shadow2D>();
        this.usershadows = new LinkedList<Shadow2D>();
        this.showshadows = true;
        this.width = (int) space.getWidth();
        this.height = (int) space.getHeight();

        this.setPreferredSize(new Dimension(width, height));
        this.setBackground(Color.BLACK);
        this.setForeground(Color.BLACK);
    }

    public void initialize(Space space) {
        this.width = (int) space.getWidth();
        this.height = (int) space.getHeight();
        this.setPreferredSize(new Dimension(width, height));

        for (Object2D obj : space.getObjects()) {
            shadows.add(obj.getShadow());
        }
    }

    public void addShadow(Shadow2D shadow) {
        assert(shadow != null);
        usershadows.add(shadow);
    }

    public void removeShadow(Shadow2D shadow) {
        assert(shadow != null);
        usershadows.remove(shadow);
    }

    public void clearUserShadows() {
        usershadows.clear();
    }

    public void clearAllShadows() {
        usershadows.clear();
        shadows.clear();
    }

    public void toggleShadows() {
        showshadows = !showshadows;
    }

    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D graphics = (Graphics2D) g;
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        synchronized (this.shadows) {
            for (Shadow2D shadow : this.shadows) {
                synchronized (shadow) {
                    if (!shadow.drawMe()) {
                        continue;
                    }

                    drawShadow(shadow, graphics);
                }
            }
        }

        if (showshadows) {
            synchronized (this.usershadows) {
                for (Shadow2D shadow : this.usershadows) {
                    synchronized (shadow) {
                        if (!shadow.drawMe()) {
                            continue;
                        }

                        drawShadow(shadow, graphics);
                    }
                }
            }
        }
    }

    private void drawShadow(Shadow2D shadow, Graphics2D graphics) {
        Vector2D position = shadow.getRealPosition();
        float x = position.getX();
        float y = position.getY();

        if (x < shadow.getHalfWidth()) {
            //goes off screen to left
            shadow.setDrawPosition(new Vector2D(x + width, y));
            shadow.draw(graphics);
            if (y < shadow.getHalfHeight()) {
                //also goes off screen to the bottom
                shadow.setDrawPosition(new Vector2D(x, y + height));
                shadow.draw(graphics);
                shadow.setDrawPosition(new Vector2D(x + width, y + height));
                shadow.draw(graphics);
            } else if (y >= height - shadow.getHalfHeight()) {
                //also goes off screen to the top
                shadow.setDrawPosition(new Vector2D(x, y - height));
                shadow.draw(graphics);
                shadow.setDrawPosition(new Vector2D(x + width, y - height));
                shadow.draw(graphics);
            }
        } else if (x >= width - shadow.getHalfWidth()) {
            //goes off screen to right
            shadow.setDrawPosition(new Vector2D(x - width, y));
            shadow.draw(graphics);
            if (y < shadow.getHalfHeight()) {
                //goes off screen to bottom
                shadow.setDrawPosition(new Vector2D(x, y + height));
                shadow.draw(graphics);
                shadow.setDrawPosition(new Vector2D(x - width, y + height));
                shadow.draw(graphics);
            } else if (y >= height - shadow.getHalfHeight()) {
                //also goes off screen to the top
                shadow.setDrawPosition(new Vector2D(x, y - height));
                shadow.draw(graphics);
                shadow.setDrawPosition(new Vector2D(x - width, y - height));
                shadow.draw(graphics);
            }
        } else if (y < shadow.getHalfHeight()) {
            //goes off screen to bottom
            shadow.setDrawPosition(new Vector2D(x, y + height));
            shadow.draw(graphics);
        } else if (y >= height - shadow.getHalfHeight()) {
            //goes off screen to top
            shadow.setDrawPosition(new Vector2D(x, y - height));
            shadow.draw(graphics);
        }

        shadow.setDrawPosition(position);
        shadow.draw(graphics);
        shadow.cleanUp();
    }
}


