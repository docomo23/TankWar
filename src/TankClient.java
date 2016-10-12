import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

//create a window
public class TankClient extends Frame {

	public static final int GAME_WIDTH = 1000, GAME_HEIGHT = 800;

	Tank myTank = new Tank(50, 400, 30, 30, this, Color.RED, false, Tank.Direction.STOP);

	Wall w1 = new Wall(80, 300, 30, 200, this);

	Wall w2 = new Wall(400, 100, 200, 30, this);

	Image offScreenImage = null;

	// collection of missiles
	List<Missile> mList = new ArrayList<Missile>();
	// collection of explode
	List<Explode> eList = new ArrayList<Explode>();
	// collection of enemy tanks
	List<Tank> tList = new ArrayList<Tank>();

	public void paint(Graphics g) {
		if (myTank.isAlive()) {
			// draw walls
			w1.draw(g);
			w2.draw(g);

			// draw all the missiles
			if (mList != null) {
				for (int i = 0; i < mList.size(); i++) {
					Missile m = mList.get(i);
					m.hitTanks(tList);
					m.hit(myTank);
					m.hitWall(w1);
					m.hitWall(w2);
					// delete it if out of border
					if (!m.isAlive())
						mList.remove(m);
					else
						m.draw(g);
				}
			}
			// draw all the explode
			for (int i = 0; i < eList.size(); i++) {
				Explode e = eList.get(i);
				e.draw(g);
			}

			// draw all the enemy tanks
			for (int i = 0; i < tList.size(); i++) {
				Tank t = tList.get(i);
				t.hitWall(w1);
				t.hitWall(w2);
				t.hitTanks(tList);
				t.draw(g);
			}
			// draw my tank
			myTank.hitWall(w1);
			myTank.hitWall(w2);
			myTank.hitTanks(tList);
			myTank.draw(g);

			// counting the number of missiles
			g.drawString("missiles count: " + mList.size(), 10, 40);
			// counting the number of enemy tanks
			g.drawString("tanks count: " + tList.size(), 10, 55);
			g.drawString("explodes count: " + eList.size(), 10, 70);
		} else {
			Gameover(g);
		}

	}

	// double buffer to eliminate flashing
	// repaint --> update --> paint
	// override update here
	public void update(Graphics g) {
		if (offScreenImage == null) {
			offScreenImage = this.createImage(GAME_WIDTH, GAME_HEIGHT);
		}
		Graphics gOffScreen = offScreenImage.getGraphics();
		// each time before painting, reset the background
		Color c = gOffScreen.getColor();
		gOffScreen.setColor(Color.WHITE);
		gOffScreen.fillRect(0, 0, GAME_WIDTH, GAME_HEIGHT);
		gOffScreen.setColor(c);
		// paint
		paint(gOffScreen);
		g.drawImage(offScreenImage, 0, 0, null);
	}

	public void launchFrame() {
		// initiate enemy tanks
		for (int j = 1; j < 12; j++) {
			for (int i = 1; i < 15; i++) {
				tList.add(new Tank(40 * i + 100, 50*j+100, 30, 30, this, Color.BLUE, true, Tank.Direction.D));
			}
		}
		this.setLocation(200, 100);
		this.setSize(GAME_WIDTH, GAME_HEIGHT);
		this.setTitle("坦克大战");
		this.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}

		});
		this.setResizable(false);

		// set background color into green
		this.setBackground(Color.WHITE);

		// keyboard input
		this.addKeyListener(new KeyMonitor());

		setVisible(true);

		new Thread(new PaintThread()).start();
	}

	public void Gameover(Graphics g) {
		g.drawString("Game Over !!!!", GAME_WIDTH/2, GAME_HEIGHT/2);
	}

	public static void main(String[] args) {
		TankClient tc = new TankClient();
		tc.launchFrame();

	}

	// thread used to repaint Tank
	private class PaintThread implements Runnable {
		public void run() {
			while (true) {
				// use repaint of the outside Class TankClient
				repaint();
				// repaint every 50 milliSecond
				try {
					Thread.sleep(50);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}

		}

	}

	// use keyboard to control my tank
	private class KeyMonitor extends KeyAdapter {
		// KeyEvent e is the key that is inputed

		public void keyReleased(KeyEvent e) {
			myTank.keyReleased(e);
		}

		public void keyPressed(KeyEvent e) {
			myTank.keyPressed(e);
		}

	}

}
