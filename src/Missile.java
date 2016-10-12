import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.util.List;


public class Missile {
	public static final int xMove = 10;
	public static final int yMove = 10;
	int xMissile, yMissile;
	public static int wMissile = 10;
	public static int hMissile = 10;
	private int w;
	private int h;
	Tank.Direction dir;

	private boolean alive = true;
	TankClient tc;
	
	private boolean isEnemy;

	public boolean isAlive() {
		return alive;
	}

	public void setAlive(boolean alive) {
		this.alive = alive;
	}


	public Missile(int xMissile, int yMissile, boolean isEnemy, Tank.Direction dir, TankClient tc) {
		this.xMissile = xMissile;
		this.yMissile = yMissile;
		this.isEnemy = isEnemy;
		this.dir = dir;
		this.tc = tc;
		w = wMissile;
		h = hMissile;
	}
	public Missile(int xMissile, int yMissile, boolean isEnemy, Tank.Direction dir, TankClient tc, int w, int h) {
		this.xMissile = xMissile;
		this.yMissile = yMissile;
		this.isEnemy = isEnemy;
		this.dir = dir;
		this.tc = tc;
		w = wMissile;
		h = hMissile;
		this.isEnemy = isEnemy;
		this.w = w;
		this.h= h;
	}




	// draw the missile
	public void draw(Graphics g) {
		if (!alive) {
			tc.mList.remove(this);
			return;
		}
		Color c = g.getColor();
		g.setColor(Color.BLACK);
		g.fillOval(xMissile, yMissile, w, h);
		g.setColor(c);
		move();
	}

	// movement of the missile
	private void move() {
		switch (dir) {
		case L:
			xMissile -= xMove;
			break;
		case LU:
			xMissile -= xMove;
			yMissile -= yMove;
			break;
		case U:
			yMissile -= yMove;
			break;
		case RU:
			xMissile += xMove;
			yMissile -= yMove;
			break;
		case R:
			xMissile += xMove;
			break;
		case RD:
			xMissile += xMove;
			yMissile += yMove;
			break;
		case D:
			yMissile += yMove;
			break;
		case LD:
			xMissile -= xMove;
			yMissile += yMove;
			break;
		}

		if (xMissile < 0 || yMissile < 0 || xMissile > TankClient.GAME_WIDTH || yMissile > TankClient.GAME_HEIGHT) {
			alive = false;
		}

	}

	public Rectangle getRect() {
		return new Rectangle(xMissile, yMissile, w, h);
	}

	// hit tank
	public boolean hit(Tank t) {
		if (this.alive && this.getRect().intersects(t.getRect()) && t.isAlive() && this.isEnemy != t.isEnemy) {
			if(!t.isEnemy) {
				t.setBloodbar(t.getBloodbar() - 20);
				if(t.getBloodbar() <=0) 
					t.setAlive(false);
			}
			else 
				t.setAlive(false);
			this.setAlive(false);
			Explode e = new Explode(t.tankX, t.tankY, tc);
			tc.eList.add(e);
			return true;
		}
		else
			return false;
	}
	
	public boolean hitTanks (List<Tank> tanks) {
		for (int i = 0; i < tanks.size(); i++) {
			if (hit(tanks.get(i))) {
				tanks.remove(i);
				return true;
			}
		}
		return false;
	}
	
	public boolean hitWall(Wall w) {
		if (this.alive && this.getRect().intersects(w.getRect()) ) {
			this.setAlive(false);
			return true;
		}
		return false;


	}
	

}
