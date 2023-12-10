package entity;

import java.awt.Color;

import engine.DrawManager.SpriteType;

/**
 * Implements a bullet that moves vertically up or down.
 *
 * @author <a href="mailto:RobertoIA1987@gmail.com">Roberto Izquierdo Amo</a>
 *
 */
public class Bullet extends Entity {

	/**
	 * Speed of the bullet, positive or negative depending on direction -
	 * positive is down.
	 */
	private int speedX;
	private int speedY;
	private int isTurning;

	private boolean firstTouchX;
	private boolean firstTouchY;

	/**
	 * Constructor, establishes the bullet's properties.
	 *
	 * @param positionX
	 *            Initial position of the bullet in the X axis.
	 * @param positionY
	 *            Initial position of the bullet in the Y axis.
	 * @param speed
	 *            Speed of the bullet, positive or negative depending on
	 *            direction - positive is down.
	 */
	public Bullet(final int positionX, final int positionY, final int speed) {
		super(positionX, positionY, 3 * 2, 5 * 2, Color.WHITE);

		this.speedX = speed + 2;
		this.speedY = speed;
		this.isTurning = 0;
		this.firstTouchX = false;
		this.firstTouchY = false;
		setSprite();
	}

	/**
	 * Sets correct sprite for the bullet, based on speed.
	 */
	public final void setSprite() {
		if (speedY < 0)
			this.spriteType = SpriteType.Bullet;
		else
			this.spriteType = SpriteType.EnemyBullet;
	}

	/**
	 * Updates the bullet's position.
	 */
	public final void update() {
		this.positionY += this.speedY;
	}

	public final void turnLeft() {
		this.positionX += this.speedX;
		this.positionY += this.speedY;
		System.out.println("Left <X : " + this.speedX + ", Y: " + this.speedY + ">");
		if(this.speedX > -6 && !this.firstTouchX) this.speedX -= 1;
		else if(this.speedX == -6 && !this.firstTouchX) this.firstTouchX = true;
		else this.speedX += 1;
		if(this.speedY > -6 && !this.firstTouchY) this.speedY -= 1;
		else if(this.speedY == -6 && !this.firstTouchY) this.firstTouchY = true;
		else this.speedY += 1;
	}

	public final void turnRight() {
		this.positionX -= this.speedX;
		this.positionY += this.speedY;
		System.out.println("Right <X : " + this.speedX + ", Y: " + this.speedY + ">");
		if (this.speedX > -6 && !this.firstTouchX)
			this.speedX -= 1;
		else if (this.speedX == -6 && !this.firstTouchX)
			this.firstTouchX = true;
		else
			this.speedX += 1;
		if (this.speedY > -6 && !this.firstTouchY)
			this.speedY -= 1;
		else if (this.speedY == -6 && !this.firstTouchY)
			this.firstTouchY = true;
		else
			this.speedY += 1;
	}

	/**
	 * Setter of the speed of the bullet.
	 *
	 * @param speed
	 *            New speed of the bullet.
	 */
	public final void setSpeed ( final int speed){
		this.speedX = speed + 2;
		this.speedY = speed;
	}

	/**
	 * Getter for the speed of the bullet.
	 *
	 * @return Speed of the bullet.
	 */
	public final int getSpeedY () {
		return this.speedY;
	}

	/**
	 * Getter for the speed of the bullet.
	 *
	 * @return Speed of the bullet.
	 */
	public final void setFirstTouchX () {
		this.firstTouchX = false;
	}
	public final void setFirstTouchY () {
		this.firstTouchY = false;
	}
	public final void setIsTurningLeft () {
		this.isTurning = 1;
	}
	public final void setIsTurningRight () {
		this.isTurning = 2;
	}
	public final int getIsTurning () {
		return this.isTurning;
	}
}
