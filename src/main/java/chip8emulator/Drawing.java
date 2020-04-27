
package chip8emulator;

import java.awt.Canvas;
import java.awt.Graphics;
import java.awt.Color;
import javax.swing.JFrame;
import java.util.*;

import java.awt.image.*;
import java.awt.Image;

public class Drawing extends Canvas {
    private static final long serialVersionUID = 1L;
    private int pixel_multiplication = 16;
    private Keychecker kc;
    public boolean[] mygfx = new boolean[64 * 32];
    public BufferedImage bf = null;
    public Chip8 cpDrive;

    Drawing() {
        super();
        Arrays.fill(this.mygfx, false);
        this.setSize(64 * this.pixel_multiplication, 32 * this.pixel_multiplication);
        this.cpDrive = Chip8.initialize();
        this.kc = new Keychecker(cpDrive.key);
        this.addKeyListener(this.kc);
        this.addImageBuffer();
    }

    public void run() {
        while (true) {
            try {
                Thread.sleep(1);
                // Thread.sleep(16);
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
            }
            this.cpDrive.emulate_cycle();

            if (this.cpDrive.drawFlag) {
                paint(this.getGraphics());
                this.cpDrive.drawFlag = false;
            }
        }
    }

    private void addImageBuffer() {
        this.bf = new BufferedImage(this.getWidth(), this.getHeight(), BufferedImage.TYPE_INT_RGB);
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("CHIP8 Emulator");
        Drawing canvas = new Drawing();
        frame.add(canvas);
        frame.pack();
        frame.setVisible(true);

        canvas.cpDrive.load_game("./roms/Pong (1 player).ch8");
        canvas.run();
    }

    private void drawPixel(Graphics g, int x, int y) {
        g.setColor(Color.WHITE);
        g.fillRect(y * this.pixel_multiplication, x * this.pixel_multiplication, this.pixel_multiplication,
                this.pixel_multiplication);
    }

    public void drawPixelMap(Graphics g, boolean[][] gfx) {
        int i, j;
        for (i = 0; i < 32; ++i)
            for (j = 0; j < 64; ++j)
                if (gfx[i][j]) {
                    this.drawPixel(g, i, j);
                }
    }

    public Image getBufferedGameState() {
        BufferedImage bufferedImage = new BufferedImage(this.getWidth(), this.getHeight(), BufferedImage.TYPE_INT_RGB);
        Graphics g = bufferedImage.getGraphics();

        setBackground(Color.BLACK);
        this.drawPixelMap(g, this.cpDrive.gfx);

        return bufferedImage;
    }

    public void paint(Graphics g) {
        Image img = this.getBufferedGameState();
        g.drawImage(img, 0, 0, this);
    }
}
