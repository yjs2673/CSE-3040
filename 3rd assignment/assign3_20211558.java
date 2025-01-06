package chap7;

import java.awt.*;
import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

public class assign3_20211558 extends Frame { // 크기가 1인 공이 충돌하면 사라집니다. 
    private Canvas canvas;
    private ArrayList<Ball> balls = new ArrayList<>();
    private boolean running = true;

    public assign3_20211558(String name) {
        super(name);
        canvas = new Canvas() { public void paint(Graphics g) { for(Ball ball : balls) ball.draw(g); }};
        canvas.setBackground(Color.WHITE);
        add("Center", canvas);
        addWindowListener(new WindowDestroy());
    }

    public void start() {
        int initsize = 16;
        int cX = 150;
        int cY = 150;
        int rad = 100;

        for(int i = 0; i < 5; i++) {
            double angle = Math.toRadians(72 * i);
            int x = cX + (int) (rad * Math.cos(angle));
            int y = cY + (int) (rad * Math.sin(angle));
            int dx = ThreadLocalRandom.current().nextInt(-3, 4);
            int dy = ThreadLocalRandom.current().nextInt(-3, 4);

            while(dx == 0 && dy == 0) {
                dx = ThreadLocalRandom.current().nextInt(-3, 4);
                dy = ThreadLocalRandom.current().nextInt(-3, 4);
            }
            balls.add(new Ball(canvas, initsize, x, y, dx, dy));
        }

        new Thread(() -> {
            while(running) {
                update();
                canvas.repaint();

                if(balls.isEmpty()) {
                    running = false;
                    System.out.println("Simulation complete. All balls have disappeared.");
                    System.exit(0);
                }

                try {
                    Thread.sleep(20);
                }
                catch(InterruptedException e) {
                    e.printStackTrace();
                }}}).start();
    }

    private void update() {
        ArrayList<Ball> newBalls = new ArrayList<>();
        for (int i = 0; i < balls.size(); i++) {
            Ball bA = balls.get(i);
            bA.move();

            for (int j = i + 1; j < balls.size(); j++) {
                Ball bB = balls.get(j);
                if (bA.isColliding(bB)) {
                	ArrayList<Ball> splitBalls1 = bA.split();
                	newBalls.addAll(splitBalls1);
                	ArrayList<Ball> splitBalls2 = bB.split();
                	newBalls.addAll(splitBalls2);

                    bA.setRemoved(true);
                    bB.setRemoved(true);
                }
            }

            if (!bA.isRemoved() && bA.getSize() >= 2) {
                newBalls.add(bA);
            }
        }
        balls = newBalls;
    }

    public static void main(String[] args) {
        assign3_20211558 frame = new assign3_20211558("Bounce Simulation");
        frame.setSize(300, 300);
        frame.setVisible(true);
        frame.start();
    }
}

class Ball {
    private Canvas box;
    private int x, y;
    private int dx, dy;
    private int size;
    private final int initialSize;
    private boolean removed = false;

    public Ball(Canvas box, int size, int x, int y, int dx, int dy) {
        this.box = box;
        this.size = size;
        this.initialSize = size;
        this.x = x;
        this.y = y;
        this.dx = dx;
        this.dy = dy;
    }

    public void draw(Graphics g) {
        g.setColor(Color.BLUE);
        g.fillOval(x, y, size, size);
    }

    public void move() {
        x += dx;
        y += dy;
        Dimension d = box.getSize();

        if(x < 0) {
            x = 0;
            dx = -dx;
        } 
        else if(x + size > d.width) {
            x = d.width - size;
            dx = -dx;
        }

        if(y < 0) {
            y = 0;
            dy = -dy;
        }
        else if (y + size > d.height) {
            y = d.height - size;
            dy = -dy;
        }
    }


    public boolean isColliding(Ball other) {
        int dx = this.x + this.size / 2 - (other.x + other.size / 2);
        int dy = this.y + this.size / 2 - (other.y + other.size / 2);
        int hypotenuse = dx * dx + dy * dy;
        int radSum = this.size / 2 + other.size / 2;
        return hypotenuse <= radSum * radSum;
    }

    public ArrayList<Ball> split() {
        ArrayList<Ball> newBalls = new ArrayList<>();
        int newSize = size / 2;
        if (newSize < 1) {
            return newBalls;
        }
        newBalls.add(new Ball(box, newSize, x + newSize / 2, y + newSize / 2, dx, dy));
        newBalls.add(new Ball(box, newSize, x - newSize / 2, y - newSize / 2, -dx, -dy));
        return newBalls;
    }

    public int getSize() { return size; }
    public int getInitialSize() { return initialSize; }
    public boolean isRemoved() { return removed; }
    public void setRemoved(boolean removed) { this.removed = removed; }
}

class WindowDestroy extends java.awt.event.WindowAdapter {
    public void windowClosing(java.awt.event.WindowEvent e) {
        System.exit(0);
    }
}
