import java.awt.*;
import java.util.*;
import java.awt.event.*;
import java.util.*;

public class Tank {
	// movement
	public static final int xMove = 5;
	public static final int yMove = 5;

	// direction and size
	public int tankX, tankY;
	public int tankWidth, tankHeight;

	private int oldX, oldY;

	// implement eight direction movement for the tank
	private boolean bL = false, bU = false, bR = false, bD = false;

	public boolean stop = false;
	
	// blood bar
	private int bloodbar = 100;

	public int getBloodbar() {
		return bloodbar;
	}

	public void setBloodbar(int bloodbar) {
		this.bloodbar = bloodbar;
	}
	
	private Bloodbar bb = new Bloodbar();

	enum Direction {
		L, LU, U, RU, R, RD, D, LD, STOP
	};

	private Direction dir = Direction.STOP;
	private Direction cannonDir = Direction.D;

	// modify tc to fire missile
	TankClient tc;

	// the color of tank
	Color tankColor;

	// enemy or not
	boolean isEnemy;

	// survive or not
	private boolean alive = true;

	// all the tanks share a random initiator
	private static Random r = new Random();

	// random walking enemy can walk 3 ~ 11+3 steps
	private int step = r.nextInt(12) + 3;

	public boolean isAlive() {
		return alive;
	}

	public void setAlive(boolean alive) {
		this.alive = alive;
	}

	public Tank(int tankX, int tankY, int tankWidth, int tankHeight, boolean isEnemy) {
		this.tankX = tankX;
		this.tankY = tankY;
		this.tankWidth = tankWidth;
		this.tankHeight = tankHeight;
		this.isEnemy = isEnemy;
	}

	// enable the ability to modify tc
	public Tank(int tankX, int tankY, int tankWidth, int tankLength, TankClient tc, Color tankColor, boolean isEnemy,
			Direction dir) {
		this(tankX, tankY, tankWidth, tankLength, isEnemy);
		this.tc = tc;
		this.tankColor = tankColor;
		this.dir = dir;
	}

	// draw the tank
	public void draw(Graphics g) {
		// if not alive, don't draw the tank
		if (!this.alive)
			return;
		Color c = g.getColor();
		// set my tank's color into red
		g.setColor(tankColor);
		// draw my tank
		// location in the frame: x, y
		// size: w, h
		g.fillOval(tankX, tankY, tankWidth, tankHeight);
		g.setColor(c);

		drawCannon(g);
		move();
		if(!this.isEnemy)
			bb.draw(g);

	}

	// read the key input
	public void keyPressed(KeyEvent e) {
		int key = e.getKeyCode();
		switch (key) {
		case KeyEvent.VK_2:
			if(!this.alive) {
				this.alive = true;
				this.setBloodbar(100);
			}
			break;
		case KeyEvent.VK_LEFT:
			bL = true;
			break;
		case KeyEvent.VK_RIGHT:
			bR = true;
			break;
		case KeyEvent.VK_UP:
			bU = true;
			break;
		case KeyEvent.VK_DOWN:
			bD = true;
			break;

		}
		setDirection();
	}

	// the current direction of the tank
	void setDirection() {
		if (bL && !bU && !bR && !bD)
			dir = Direction.L;
		else if (bL && bU && !bR && !bD)
			dir = Direction.LU;
		else if (!bL && bU && !bR && !bD)
			dir = Direction.U;
		else if (!bL && bU && bR && !bD)
			dir = Direction.RU;
		else if (!bL && !bU && bR && !bD)
			dir = Direction.R;
		else if (!bL && !bU && bR && bD)
			dir = Direction.RD;
		else if (!bL && !bU && !bR && bD)
			dir = Direction.D;
		else if (bL && !bU && !bR && bD)
			dir = Direction.LD;
		else if (!bL && !bU && !bR && !bD)
			dir = Direction.STOP;
	}

	// change the coordination of the tank according to the key input
	void move() {

		// handle crunching into object problem
		// if (this.stop){
		// stop = false;

		// return;
		// }
		oldX = this.tankX;
		oldY = this.tankY;

		switch (dir) {
		case L:
			tankX -= xMove;
			break;
		case LU:
			tankX -= xMove;
			tankY -= yMove;
			break;
		case U:
			tankY -= yMove;
			break;
		case RU:
			tankX += xMove;
			tankY -= yMove;
			break;
		case R:
			tankX += xMove;
			break;
		case RD:
			tankX += xMove;
			tankY += yMove;
			break;
		case D:
			tankY += yMove;
			break;
		case LD:
			tankX -= xMove;
			tankY += yMove;
			break;
		case STOP:
			break;
		}

		if (this.dir != Direction.STOP) {
			this.cannonDir = this.dir;
		}

		// handle the out of border problem
		if (tankX < 0)
			tankX = 0;
		if (tankY < 20)
			tankY = 20;
		if (tankX + tankWidth > TankClient.GAME_WIDTH)
			tankX = TankClient.GAME_WIDTH - tankWidth;
		if (tankY > TankClient.GAME_HEIGHT - tankHeight)
			tankY = TankClient.GAME_HEIGHT - tankHeight;

		// random walker for enemy
		if (isEnemy) {
			// turn the enumeration of the direction into an array
			Direction[] dirs = Direction.values();
			if (step == 0) {
				step = r.nextInt(12) + 3;
				// initiate an random direction
				int rNum = r.nextInt(dirs.length);
				dir = dirs[rNum];

			}
			step--;
			// fire randomly
			if (r.nextInt(40) > 38)
				tc.mList.add(fire());
		}

	}

	public void keyReleased(KeyEvent e) {
		int key = e.getKeyCode();
		switch (key) {
		case KeyEvent.VK_SPACE:
			tc.mList.add(fire());
			break;
		case KeyEvent.VK_X:
			tc.mList.add(specialFire());
			break;

		case KeyEvent.VK_Z:
			tc.mList.addAll(ultimate());
			break;

		case KeyEvent.VK_LEFT:
			bL = false;
			break;
		case KeyEvent.VK_RIGHT:
			bR = false;
			break;
		case KeyEvent.VK_UP:
			bU = false;
			break;
		case KeyEvent.VK_DOWN:
			bD = false;
			break;

		}
		setDirection();
	}

	public Missile fire() {
		if (!alive)
			return null;
		Missile m1 = new Missile(tankX + tankWidth / 2 - Missile.wMissile / 2,
				tankY + tankHeight / 2 - Missile.hMissile / 2, this.isEnemy, cannonDir, tc);
		return m1;
	}

	public ArrayList<Missile> ultimate() {
		if (!alive)
			return null;
		Direction[] dirs = Direction.values();
		ArrayList<Missile> mS = new ArrayList<Missile>();
		for (int i = 0; i < 8; i++) {
			mS.add(new Missile(tankX + tankWidth / 2 - Missile.wMissile / 2,
					tankY + tankHeight / 2 - Missile.hMissile / 2, this.isEnemy, dirs[i], tc));
		}
		return mS;
	}

	public Missile specialFire() {
		if (!alive)
			return null;
		int testX = Missile.wMissile + 20;
		int testY = Missile.hMissile + 20;
		Missile m2 = new Missile(tankX + tankWidth / 2 - Missile.wMissile / 2,
				tankY + tankHeight / 2 - Missile.hMissile / 2, this.isEnemy, cannonDir, tc, testX, testY);
		return m2;
	}

	private void drawCannon(Graphics g) {
		switch (cannonDir) {
		case L:
			g.drawLine(tankX + tankWidth / 2, tankY + tankHeight / 2, tankX, tankY + tankHeight / 2);
			break;
		case LU:
			g.drawLine(tankX + tankWidth / 2, tankY + tankHeight / 2, tankX, tankY);
			break;
		case U:
			g.drawLine(tankX + tankWidth / 2, tankY + tankHeight / 2, tankX + tankWidth / 2, tankY);
			break;
		case RU:
			g.drawLine(tankX + tankWidth / 2, tankY + tankHeight / 2, tankX + tankWidth, tankY);
			break;
		case R:
			g.drawLine(tankX + tankWidth / 2, tankY + tankHeight / 2, tankX + tankWidth, tankY + tankHeight / 2);
			break;
		case RD:
			g.drawLine(tankX + tankWidth / 2, tankY + tankHeight / 2, tankX + tankWidth, tankY + tankHeight);
			break;
		case D:
			g.drawLine(tankX + tankWidth / 2, tankY + tankHeight / 2, tankX + tankWidth / 2, tankY + tankHeight);
			break;
		case LD:
			g.drawLine(tankX + tankWidth / 2, tankY + tankHeight / 2, tankX, tankY + tankHeight);
			break;

		}
	}

	public Rectangle getRect() {
		return new Rectangle(tankX, tankY, tankWidth, tankHeight);
	}

	private void stay() {
		this.tankX = oldX;
		this.tankY = oldY;
	}

	public boolean hitWall(Wall w) {
		if (this.alive && this.getRect().intersects(w.getRect())) {
			stay();
			return true;
		}
		return false;
	}

	public boolean hitTanks(java.util.List<Tank> tanks) {
		for (int i = 0; i < tanks.size(); i++) {
			Tank t = tanks.get(i);
			if (this != t) {
				if (this.alive && t.alive && this.getRect().intersects(t.getRect())) {
					this.stay();
					t.stay();
					return true;
				}
			}
		}

		return false;
	}

	private class Bloodbar  {
		public void draw(Graphics g) {
			Color c = g.getColor();
			g.setColor(Color.RED);
			g.drawRect(tankX, tankY - 10, tankWidth, 10);
			int w = tankWidth * bloodbar / 100;
			g.fillRect(tankX, tankY -10, w, 10);
			g.setColor(c);
		}
	}
	
	
}
