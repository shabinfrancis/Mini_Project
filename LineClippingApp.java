package MiniProject;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

public class LineClippingApp extends JFrame {
    private List<Line2D.Double> lines = new ArrayList<>();
    private Rectangle2D.Double clippingWindow;
    private boolean isClipping = false;
    private LineDrawingAlgorithm currentAlgorithm = LineDrawingAlgorithm.DDA; // Default algorithm

    public enum LineDrawingAlgorithm {
        DDA,
        BRESENHAM
    }

    public LineClippingApp() {
        setTitle("VIRTUAL LAB ");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        LineClippingPanel drawingPanel = new LineClippingPanel();
        getContentPane().add(drawingPanel, BorderLayout.CENTER);

        JPanel controlPanel = new JPanel();
        JButton addLineButton = new JButton("Add Line");
        JButton setClippingWindowButton = new JButton("Set Clipping Window");
        JButton clipButton = new JButton("Clip Lines");
        JButton clearButton = new JButton("Clear");
        JButton chooseAlgorithmButton = new JButton("Choose Algorithm");

        controlPanel.add(addLineButton);
        controlPanel.add(setClippingWindowButton);
        controlPanel.add(clipButton);
        controlPanel.add(clearButton);
        controlPanel.add(chooseAlgorithmButton);

        addLineButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!isClipping) {
                    // Get user input for line coordinates using JOptionPane
                    double x1 = Double.parseDouble(JOptionPane.showInputDialog("Enter x1:"));
                    double y1 = Double.parseDouble(JOptionPane.showInputDialog("Enter y1:"));
                    double x2 = Double.parseDouble(JOptionPane.showInputDialog("Enter x2:"));
                    double y2 = Double.parseDouble(JOptionPane.showInputDialog("Enter y2:"));

                    // Create Line2D.Double and add to the lines list
                    Line2D.Double line = new Line2D.Double(x1, y1, x2, y2);
                    lines.add(line);
                    drawingPanel.repaint();
                }
            }
        });

        setClippingWindowButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!isClipping) {
                    double x = Double.parseDouble(JOptionPane.showInputDialog("Enter x:"));
                    double y = Double.parseDouble(JOptionPane.showInputDialog("Enter y:"));
                    double width = Double.parseDouble(JOptionPane.showInputDialog("Enter width:"));
                    double height = Double.parseDouble(JOptionPane.showInputDialog("Enter height:"));

                    clippingWindow = new Rectangle2D.Double(x, y, width, height);
                    drawingPanel.repaint();
                }
            }
        });

        clipButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!isClipping) {
                    isClipping = true;
                    drawingPanel.setClippingLines(clipLines());
                    drawingPanel.repaint();
                }
            }
        });

        clearButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                lines.clear();
                clippingWindow = null;
                isClipping = false;
                drawingPanel.repaint();
            }
        });

        chooseAlgorithmButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String[] options = {"DDA", "Bresenham"};
                String selectedAlgorithm = (String) JOptionPane.showInputDialog(
                        LineClippingApp.this,
                        "Choose Line Drawing Algorithm:",
                        "Choose Algorithm",
                        JOptionPane.QUESTION_MESSAGE,
                        null,
                        options,
                        options[0]);

                if (selectedAlgorithm != null) {
                    if (selectedAlgorithm.equals("DDA")) {
                        currentAlgorithm = LineDrawingAlgorithm.DDA;
                    } else if (selectedAlgorithm.equals("Bresenham")) {
                        currentAlgorithm = LineDrawingAlgorithm.BRESENHAM;
                    }
                }
            }
        });

        getContentPane().add(controlPanel, BorderLayout.SOUTH);
    }

    private class LineClippingPanel extends JPanel {
        private List<Line2D.Double> clippingLines = new ArrayList<>();
        private static final int GRID_SIZE = 20; // Size of the grid cells

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            Graphics2D g2d = (Graphics2D) g;

            // Draw grid
            g2d.setColor(Color.LIGHT_GRAY);
            for (int i = 0; i <= getWidth(); i += GRID_SIZE) {
                g2d.drawLine(i, 0, i, getHeight());
            }
            for (int i = 0; i <= getHeight(); i += GRID_SIZE) {
                g2d.drawLine(0, i, getWidth(), i);
            }

            if (clippingWindow != null) {
                g2d.setColor(Color.RED);
                g2d.draw(clippingWindow);
            }

            g2d.setColor(Color.BLUE);
            for (Line2D.Double line : lines) {
                if (currentAlgorithm == LineDrawingAlgorithm.DDA) {
                    drawDDALine(g2d, line);
                } else {
                    drawBresenhamLine(g2d, line);
                }
            }

            if (isClipping) {
                g2d.setColor(Color.GREEN);
                for (Line2D.Double clippedLine : clippingLines) {
                    g2d.draw(clippedLine);
                }
            }
        }

        public void setClippingLines(List<Line2D.Double> clippingLines) {
            this.clippingLines = clippingLines;
        }

        // DDA Line Drawing Algorithm
        private void drawDDALine(Graphics2D g2d, Line2D.Double line) {
            double x1 = line.getX1();
            double y1 = line.getY1();
            double x2 = line.getX2();
            double y2 = line.getY2();

            double dx = x2 - x1;
            double dy = y2 - y1;

            double steps = Math.max(Math.abs(dx), Math.abs(dy));

            double xIncrement = dx / steps;
            double yIncrement = dy / steps;

            double x = x1;
            double y = y1;

            for (int i = 0; i <= steps; i++) {
                g2d.drawLine((int) x, (int) y, (int) x, (int) y);
                x += xIncrement;
                y += yIncrement;
            }
        }

        // Bresenham Line Drawing Algorithm
        private void drawBresenhamLine(Graphics2D g2d, Line2D.Double line) {
            int x1 = (int) line.getX1();
            int y1 = (int) line.getY1();
            int x2 = (int) line.getX2();
            int y2 = (int) line.getY2();

            int dx = Math.abs(x2 - x1);
            int dy = Math.abs(y2 - y1);

            int sx = (x1 < x2) ? 1 : -1;
            int sy = (y1 < y2) ? 1 : -1;

            int err = dx - dy;

            while (true) {
                g2d.drawLine(x1, y1, x1, y1);

                if (x1 == x2 && y1 == y2) {
                    break;
                }

                int e2 = 2 * err;

                if (e2 > -dy) {
                    err = err - dy;
                    x1 = x1 + sx;
                }

                if (e2 < dx) {
                    err = err + dx;
                    y1 = y1 + sy;
                }
            }
        }
    }

    private List<Line2D.Double> clipLines() {
        List<Line2D.Double> clippedLines = new ArrayList<>();
        if (clippingWindow != null) {
            for (Line2D.Double line : lines) {
                if (clippingWindow.intersectsLine(line)) {
                    Line2D.Double clippedLine;
                    if (currentAlgorithm == LineDrawingAlgorithm.DDA) {
                        clippedLine = clipDDALine(line);
                    } else {
                        clippedLine = clipBresenhamLine(line);
                    }
                    if (clippedLine != null) {
                        clippedLines.add(clippedLine);
                    }
                }
            }
        }
        return clippedLines;
    }

    private Line2D.Double clipDDALine(Line2D.Double line) {
        double x1 = line.getX1();
        double y1 = line.getY1();
        double x2 = line.getX2();
        double y2 = line.getY2();

        double xMin = clippingWindow.getMinX();
        double yMin = clippingWindow.getMinY();
        double xMax = clippingWindow.getMaxX();
        double yMax = clippingWindow.getMaxY();

        // Clip against the left edge
        if (x1 < xMin && x2 < xMin) {
            return null; // Line is completely outside
        } else if (x1 < xMin) {
            y1 = y1 + (y2 - y1) * (xMin - x1) / (x2 - x1);
            x1 = xMin;
        } else if (x2 < xMin) {
            y2 = y1 + (y2 - y1) * (xMin - x1) / (x2 - x1);
            x2 = xMin;
        }

        // Clip against the right edge
        if (x1 > xMax && x2 > xMax) {
            return null; // Line is completely outside
        } else if (x1 > xMax) {
            y1 = y1 + (y2 - y1) * (xMax - x1) / (x2 - x1);
            x1 = xMax;
        } else if (x2 > xMax) {
            y2 = y1 + (y2 - y1) * (xMax - x1) / (x2 - x1);
            x2 = xMax;
        }

        // Clip against the top edge
        if (y1 < yMin && y2 < yMin) {
            return null; // Line is completely outside
        } else if (y1 < yMin) {
            x1 = x1 + (x2 - x1) * (yMin - y1) / (y2 - y1);
            y1 = yMin;
        } else if (y2 < yMin) {
            x2 = x1 + (x2 - x1) * (yMin - y1) / (y2 - y1);
            y2 = yMin;
        }

        // Clip against the bottom edge
        if (y1 > yMax && y2 > yMax) {
            return null; // Line is completely outside
        } else if (y1 > yMax) {
            x1 = x1 + (x2 - x1) * (yMax - y1) / (y2 - y1);
            y1 = yMax;
        } else if (y2 > yMax) {
            x2 = x1 + (x2 - x1) * (yMax - y1) / (y2 - y1);
            y2 = yMax;
        }

        return new Line2D.Double(x1, y1, x2, y2);
    }


    private Line2D.Double clipBresenhamLine(Line2D.Double line) {
        double x1 = line.getX1();
        double y1 = line.getY1();
        double x2 = line.getX2();
        double y2 = line.getY2();

        double xMin = clippingWindow.getMinX();
        double yMin = clippingWindow.getMinY();
        double xMax = clippingWindow.getMaxX();
        double yMax = clippingWindow.getMaxY();

        // Check if the line is completely outside the clipping window
        if ((x1 < xMin && x2 < xMin) || (x1 > xMax && x2 > xMax) || (y1 < yMin && y2 < yMin) || (y1 > yMax && y2 > yMax)) {
            return null; // Line is completely outside
        }

        // Initialize variables for Bresenham algorithm
        int x = (int) x1;
        int y = (int) y1;
        int dx = (int) (x2 - x1);
        int dy = (int) (y2 - y1);

        int sx = (x1 < x2) ? 1 : -1;
        int sy = (y1 < y2) ? 1 : -1;

        // Bresenham algorithm adapted for clipping
        int err = dx - dy;

        while (true) {
            // Check if the current point is inside the clipping window
            if (x >= xMin && x <= xMax && y >= yMin && y <= yMax) {
                break;
            }

            // Calculate the next point using Bresenham
            int e2 = 2 * err;

            if (e2 > -dy) {
                err -= dy;
                x += sx;
            }

            if (e2 < dx) {
                err += dx;
                y += sy;
            }
        }

        // Clip the line based on the intersection points
        double clippedX1 = Math.max(xMin, Math.min(xMax, x1));
        double clippedY1 = Math.max(yMin, Math.min(yMax, y1));
        double clippedX2 = Math.max(xMin, Math.min(xMax, x2));
        double clippedY2 = Math.max(yMin, Math.min(yMax, y2));

        return new Line2D.Double(clippedX1, clippedY1, clippedX2, clippedY2);
    }


    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                LineClippingApp app = new LineClippingApp();
                app.setVisible(true);
            }
        });
    }
}