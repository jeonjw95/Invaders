package entity;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;
import java.time.LocalDateTime;


import lombok.Getter;
import lombok.Setter;
import screen.Screen;
import engine.Cooldown;
import engine.Core;
import engine.DrawManager;
import engine.DrawManager.SpriteType;
import engine.GameSettings;

/**
 * Groups enemy ships into a formation that moves together.
 * 
 * @author <a href="mailto:RobertoIA1987@gmail.com">Roberto Izquierdo Amo</a>
 * 
 */
public class EnemyShipFormation implements Iterable<EnemyShip> {

	/** Initial position in the x-axis. */
	private static final int INIT_POS_X = 20;
	/** Initial position in the y-axis. */
	private static final int INIT_POS_Y = 100;
	/** Distance between ships. */
	private static final int SEPARATION_DISTANCE = 40;
	/** Proportion of C-type ships. */
	private static final double PROPORTION_C = 0.2;
	/** Proportion of B-type ships. */
	private static final double PROPORTION_B = 0.4;
	/** Lateral speed of the formation. */
	private static final int X_SPEED = 8;
	/** Downwards speed of the formation. */
	private static final int Y_SPEED = 4;
	/** Speed of the bullets shot by the members. */
	private static final int BULLET_SPEED = 4;
	/** Proportion of differences between shooting times. */
	private static final double SHOOTING_VARIANCE = .2;
	/** Margin on the sides of the screen. */
	private static final int SIDE_MARGIN = 20;
	/** Margin on the bottom of the screen. */
	private static final int BOTTOM_MARGIN = 80;
	/** Distance to go down each pass. */
	private static final int DESCENT_DISTANCE = 20;
	/** Minimum speed allowed. */
	private static final int MINIMUM_SPEED = 10;
	/** Enemyship 기본 공격 주기 */
	private static final int ENEMYSHIP_SHOOTING_INTERVAL = 2000;
	/** Boss 기본 공격 주기 */
	private static final int BOSS_SHOOTING_INTERVAL = 1000;
	/** Boss Lazer 주기 */
	private static final int BOSS_SHOOTING_LAZER_INTERVAL = 10000;
	/** BOSS_SHOOTING_LAZER_INTERVAL와 곱한 만큼 Lazer 발사  */
	private static final double BOSS_SHOOTING_LAZER_RATE = 0.3;
	/** Boss TURNING BULLET 주기 */
	private static final int BOSS_SHOOTING_TURNING_BULLET_INTERVAL = 2000;
	/** BOSS_SHOOTING_TURNING BULLET_INTERVAL와 곱한 만큼 Skill 발사  */
	private static final double BOSS_SHOOTING_TURNING_BULLET_RATE = 0.8;
	/** 무한대 상수 (공격 주기를 무한대로 만들어 공격을 못하는 상태로 만듦)*/
	private static final int INFINITE = 99999;
	/** DrawManager instance. */
	private DrawManager drawManager;
	/** Application logger. */
	private Logger logger;
	/** Screen to draw ships on. */
	private Screen screen;

	/** List of enemy ships forming the formation. */
	private List<List<EnemyShip>> enemyShips;
	/** Minimum time between shots. */
	private @Setter Cooldown shootingCooldown;
	private Cooldown bossShootingCooldown;

	private Cooldown shootingLazerCooldown;

	private Cooldown lazerCooldown;

	private Cooldown shootingTurningBulletsCooldown;

	private Cooldown turningBulletsCooldown;

	private Cooldown finalBossShootingCooldown;
	/** Number of ships in the formation - horizontally. */
	private int nShipsWide;
	/** Number of ships in the formation - vertically. */
	private int nShipsHigh;
	/** Time between shots. */
	private int shootingInterval;
	/** Variance in the time between shots. */
	private int shootingVariance;
	private @Getter int lazerInterval;
	private @Getter int turningBulletsInterval;
	/** Initial ship speed. */
	private int baseSpeed;
	/** Speed of the ships. */
	private int movementSpeed;
	/** Current direction the formation is moving on. */
	private Direction currentDirection;
	/** Direction the formation was moving previously. */
	private Direction previousDirection;
	/** Interval between movements, in frames. */
	private int movementInterval;
	/** Total width of the formation. */
	private int width;
	/** Total height of the formation. */
	private int height;
	/** Position in the x-axis of the upper left corner of the formation. */
	private @Getter int positionX;
	/** Position in the y-axis of the upper left corner of the formation. */
	private @Getter int positionY;
	/** Width of one ship. */
	private int shipWidth;
	/** Height of one ship. */
	private int shipHeight;
	/** List of ships that are able to shoot. */
	private @Getter List<EnemyShip> shooters;
	/** Number of not destroyed ships. */
	private int shipCount;
	private int bossStage;
	private boolean isShootingIntervalChanged;
	private boolean isLazerOn;
	private boolean isTurningBulletsOn;
	private int randomLazerLocation;
	private int randomTurningBulletLocation;

	private @Setter boolean isTesting;

	/** Directions the formation can move. */
	private enum Direction {
		/** Movement to the right side of the screen. */
		RIGHT,
		/** Movement to the left side of the screen. */
		LEFT,
		/** Movement to the bottom of the screen. */
		DOWN
	};

	/**
	 * Constructor, sets the initial conditions.
	 * 
	 * @param gameSettings
	 *            Current game settings.
	 */
	public EnemyShipFormation(final GameSettings gameSettings) {
		this.drawManager = Core.getDrawManager();
		this.logger = Core.getLogger();
		this.enemyShips = new ArrayList<List<EnemyShip>>();
		this.currentDirection = Direction.RIGHT;
		this.movementInterval = 0;
		this.nShipsWide = gameSettings.getFormationWidth();
		this.nShipsHigh = gameSettings.getFormationHeight();
		this.shootingInterval = gameSettings.getShootingFrecuency();
		this.shootingVariance = (int) (gameSettings.getShootingFrecuency()
				* SHOOTING_VARIANCE);
		this.lazerInterval = 1;
		this.turningBulletsInterval = 100;
		this.baseSpeed = gameSettings.getBaseSpeed();
		this.movementSpeed = this.baseSpeed;
		this.bossStage = gameSettings.getBossStage();
		this.positionX = INIT_POS_X;
		this.positionY = INIT_POS_Y;
		this.shooters = new ArrayList<EnemyShip>();
		this.isTesting = false;
		SpriteType spriteType;
		this.isShootingIntervalChanged = true;
		this.isLazerOn = true;
		this.isTurningBulletsOn = true;
		this.randomLazerLocation = (int) (Math.random() * 900);
		this.randomTurningBulletLocation = (int) (Math.random() * 900);

		this.logger.info("Initializing " + nShipsWide + "x" + nShipsHigh
				+ " ship formation in (" + positionX + "," + positionY + ")");

		// Each sub-list is a column on the formation.
		for (int i = 0; i < this.nShipsWide; i++)
			this.enemyShips.add(new ArrayList<EnemyShip>());

		if (bossStage == 0) {
			for (List<EnemyShip> column : this.enemyShips) {
				for (int i = 0; i < this.nShipsHigh; i++) {
					if (i / (float) this.nShipsHigh < PROPORTION_C)
						spriteType = SpriteType.EnemyShipC1;
					else if (i / (float) this.nShipsHigh < PROPORTION_B
							+ PROPORTION_C)
						spriteType = SpriteType.EnemyShipB1;
					else
						spriteType = SpriteType.EnemyShipA1;

					column.add(new EnemyShip((SEPARATION_DISTANCE
							* this.enemyShips.indexOf(column))
							+ positionX, (SEPARATION_DISTANCE * i)
							+ positionY, spriteType));
					this.shipCount++;
				}
			}
		}
		else {
			for (List<EnemyShip> column : this.enemyShips) {
				for (int i = 0; i < this.nShipsHigh; i++) {
					switch (this.bossStage)
					{
						case 1:
							spriteType = SpriteType.BossA;
							column.add(new EnemyShip((SEPARATION_DISTANCE
									* this.enemyShips.indexOf(column))
									+ positionX, (SEPARATION_DISTANCE * i)
									+ positionY, 500, 209, spriteType, Color.WHITE));
							this.shipCount++;
							break;
						case 2:
							spriteType = SpriteType.BossA;
							column.add(new EnemyShip((SEPARATION_DISTANCE
									* this.enemyShips.indexOf(column))
									+ positionX, (SEPARATION_DISTANCE * i)
									+ positionY, 500, 209, spriteType, Color.GRAY));
							this.shipCount++;
							break;
						case 3:
							spriteType = SpriteType.BossB;
							column.add(new EnemyShip((SEPARATION_DISTANCE
									* this.enemyShips.indexOf(column))
									+ positionX, (SEPARATION_DISTANCE * i)
									+ positionY, 500, 142, spriteType, Color.WHITE));
							this.shipCount++;
							break;
						case 4:
							spriteType = SpriteType.BossB;
							column.add(new EnemyShip((SEPARATION_DISTANCE
									* this.enemyShips.indexOf(column))
									+ positionX, (SEPARATION_DISTANCE * i)
									+ positionY, 500, 142, spriteType, Color.GRAY));
							this.shipCount++;
							break;
						case 5:
							spriteType = SpriteType.BossC;
							column.add(new EnemyShip((SEPARATION_DISTANCE
									* this.enemyShips.indexOf(column))
									+ positionX, (SEPARATION_DISTANCE * i)
									+ positionY, 500, 188, spriteType, Color.WHITE));
							this.shipCount++;
							break;
						case 6:
							spriteType = SpriteType.BossC;
							column.add(new EnemyShip((SEPARATION_DISTANCE
									* this.enemyShips.indexOf(column))
									+ positionX, (SEPARATION_DISTANCE * i)
									+ positionY, 500, 188, spriteType, Color.GRAY));
							this.shipCount++;
							break;
						default:
							spriteType = SpriteType.BossD;
							column.add(new EnemyShip((SEPARATION_DISTANCE
									* this.enemyShips.indexOf(column))
									+ positionX, (SEPARATION_DISTANCE * i)
									+ positionY, 400, 219, spriteType, Color.GRAY));
							this.shipCount++;
							break;
					}
				}
			}
		}

		this.shipWidth = this.enemyShips.get(0).get(0).getWidth();
		this.shipHeight = this.enemyShips.get(0).get(0).getHeight();

		this.width = (this.nShipsWide - 1) * SEPARATION_DISTANCE
				+ this.shipWidth;
		this.height = (this.nShipsHigh - 1) * SEPARATION_DISTANCE
				+ this.shipHeight;

		for (List<EnemyShip> column : this.enemyShips)
			this.shooters.add(column.get(column.size() - 1));
	}

	/**
	 * Associates the formation to a given screen.
	 * 
	 * @param newScreen
	 *            Screen to attach.
	 */
	public final void attach(final Screen newScreen) {
		screen = newScreen;
	}

	/**
	 * Draws every individual component of the formation.
	 */
	public final void draw() {
		for (List<EnemyShip> column : this.enemyShips)
			for (EnemyShip enemyShip : column)
				drawManager.drawEntity(enemyShip, enemyShip.getPositionX(),
						enemyShip.getPositionY());
	}

	/**
	 * Updates the position of the ships.
	 */
	public final void update() {
		if (this.shootingCooldown == null) {
			this.shootingCooldown = Core.getVariableCooldown(shootingInterval,
					shootingVariance);
			this.shootingCooldown.reset();
		}
		if (this.isShootingIntervalChanged) {
			this.bossShootingCooldown = Core.getVariableCooldown(shootingInterval,
					shootingVariance);
			this.bossShootingCooldown.reset();
			this.finalBossShootingCooldown = Core.getVariableCooldown(shootingInterval,
					shootingVariance);
			this.finalBossShootingCooldown.reset();
		}
		if(this.isLazerOn) {
			this.lazerCooldown = Core.getCooldown(lazerInterval);
			this.lazerCooldown.reset();
		}
		if(this.shootingLazerCooldown == null) {
			this.shootingLazerCooldown = Core.getCooldown(BOSS_SHOOTING_LAZER_INTERVAL);
			this.shootingLazerCooldown.reset();
		}
		if(this.isTurningBulletsOn) {
			this.turningBulletsCooldown = Core.getCooldown(turningBulletsInterval);
			this.turningBulletsCooldown.reset();
		}
		if(this.shootingTurningBulletsCooldown == null) {
			this.shootingTurningBulletsCooldown = Core.getCooldown(BOSS_SHOOTING_TURNING_BULLET_INTERVAL);
			this.shootingTurningBulletsCooldown.reset();
		}
		
		cleanUp();

		int movementX = 0;
		int movementY = 0;
		double remainingProportion = (double) this.shipCount
				/ (this.nShipsHigh * this.nShipsWide);
		this.movementSpeed = (int) (Math.pow(remainingProportion, 2)
				* this.baseSpeed);
		this.movementSpeed += MINIMUM_SPEED;
		
		movementInterval++;
		if (movementInterval >= this.movementSpeed || isTesting) {
			movementInterval = 0;

			boolean isAtBottom = positionY
					+ this.height > screen.getHeight() - BOTTOM_MARGIN;
			boolean isAtRightSide = positionX
					+ this.width >= screen.getWidth() - SIDE_MARGIN;
			boolean isAtLeftSide = positionX <= SIDE_MARGIN;
			boolean isAtHorizontalAltitude = positionY % DESCENT_DISTANCE == 0;

			if (currentDirection == Direction.DOWN) {
				if (isAtHorizontalAltitude)
					if (previousDirection == Direction.RIGHT) {
						currentDirection = Direction.LEFT;
						this.logger.info("Formation now moving left 1");
					} else {
						currentDirection = Direction.RIGHT;
						this.logger.info("Formation now moving right 2");
					}
			} else if (currentDirection == Direction.LEFT) {
				if (isAtLeftSide)
					if (!isAtBottom) {
						previousDirection = currentDirection;
						currentDirection = Direction.DOWN;
						this.logger.info("Formation now moving down 3");
					} else {
						currentDirection = Direction.RIGHT;
						this.logger.info("Formation now moving right 4");
					}
			} else {
				if (isAtRightSide)
					if (!isAtBottom) {
						previousDirection = currentDirection;
						currentDirection = Direction.DOWN;
						this.logger.info("Formation now moving down 5");
					} else {
						currentDirection = Direction.LEFT;
						this.logger.info("Formation now moving left 6");
					}
			}

			if (currentDirection == Direction.RIGHT)
				movementX = X_SPEED;
			else if (currentDirection == Direction.LEFT)
				movementX = -X_SPEED;
			else
				movementY = Y_SPEED;

			positionX += movementX;
			positionY += movementY;

			// Cleans explosions.
			List<EnemyShip> destroyed;
			for (List<EnemyShip> column : this.enemyShips) {
				destroyed = new ArrayList<EnemyShip>();
				for (EnemyShip ship : column) {
					if (ship != null && ship.isDestroyed()) {
						destroyed.add(ship);
						this.logger.info("Removed enemy "
								+ column.indexOf(ship) + " from column "
								+ this.enemyShips.indexOf(column));
					}
				}
				column.removeAll(destroyed);
			}

			for (List<EnemyShip> column : this.enemyShips)
				for (EnemyShip enemyShip : column) {
					enemyShip.move(movementX, movementY);
					enemyShip.update();
				}
		}
	}

	/**
	 * Cleans empty columns, adjusts the width and height of the formation.
	 */
	private void cleanUp() {
		Set<Integer> emptyColumns = new HashSet<Integer>();
		int maxColumn = 0;
		int minPositionY = Integer.MAX_VALUE;
		for (List<EnemyShip> column : this.enemyShips) {
			if (!column.isEmpty()) {
				// Height of this column
				int columnSize = column.get(column.size() - 1).positionY
						- this.positionY + this.shipHeight;
				maxColumn = Math.max(maxColumn, columnSize);
				minPositionY = Math.min(minPositionY, column.get(0)
						.getPositionY());
			} else {
				// Empty column, we remove it.
				emptyColumns.add(this.enemyShips.indexOf(column));
			}
		}
		for (int index : emptyColumns) {
			this.enemyShips.remove(index);
			logger.info("Removed column " + index);
		}

		int leftMostPoint = 0;
		int rightMostPoint = 0;
		
		for (List<EnemyShip> column : this.enemyShips) {
			if (!column.isEmpty()) {
				if (leftMostPoint == 0)
					leftMostPoint = column.get(0).getPositionX();
				rightMostPoint = column.get(0).getPositionX();
			}
		}

		this.width = rightMostPoint - leftMostPoint + this.shipWidth;
		this.height = maxColumn;

		this.positionX = leftMostPoint;
		this.positionY = minPositionY;
	}

	/**
	 * Shoots a bullet downwards.
	 * 
	 * @param bullets
	 *            Bullets set to add the bullet being shot.
	 */
	public final boolean shoot(final Set<Bullet> bullets) {
		// For now, only ships in the bottom row are able to shoot.
		setShootingInterval(ENEMYSHIP_SHOOTING_INTERVAL);
		int index = (int) (Math.random() * this.shooters.size());
		EnemyShip shooter = this.shooters.get(index);
		if (this.shootingCooldown.checkFinished() || isTesting) {
			this.shootingCooldown.reset();
			bullets.add(BulletPool.getBullet(shooter.getPositionX()
					+ shooter.width / 2, shooter.getPositionY(), BULLET_SPEED));
			return true;
		}
		return false;
	}

	public final void bossAttackMechanism1(final Set<Bullet> bullets) {
		// For now, only ships in the bottom row are able to shoot.
		EnemyShip shooter = this.shooters.get(0);
		int[] bulletLocation = new int[7];
		for (int i = 0; i < 7; i++) {
			bulletLocation[i] = (int) (Math.random() * shooter.width);
		}
		setShootingInterval(BOSS_SHOOTING_INTERVAL - (this.bossStage * 50));
		if (this.bossShootingCooldown.checkFinished() || isTesting) {
			this.bossShootingCooldown.reset();
			bullets.add(BulletPool.getBullet(shooter.getPositionX()
					+ bulletLocation[0], shooter.getPositionY() + shooter.height, BULLET_SPEED));
			bullets.add(BulletPool.getBullet(shooter.getPositionX()
					+ bulletLocation[1], shooter.getPositionY() + shooter.height, BULLET_SPEED));
			bullets.add(BulletPool.getBullet(shooter.getPositionX()
					+ bulletLocation[2], shooter.getPositionY() + shooter.height, BULLET_SPEED));
			bullets.add(BulletPool.getBullet(shooter.getPositionX()
					+ bulletLocation[3], shooter.getPositionY() + shooter.height, BULLET_SPEED));
			bullets.add(BulletPool.getBullet(shooter.getPositionX()
					+ bulletLocation[4], shooter.getPositionY() + shooter.height, BULLET_SPEED));
			bullets.add(BulletPool.getBullet(shooter.getPositionX()
					+ bulletLocation[5], shooter.getPositionY() + shooter.height, BULLET_SPEED));
			bullets.add(BulletPool.getBullet(shooter.getPositionX()
					+ bulletLocation[6], shooter.getPositionY() + shooter.height, BULLET_SPEED));
		}
	}

	public final void bossAttackMechanism2(final Set<Bullet> bullets) {
		// For now, only ships in the bottom row are able to shoot.
		EnemyShip shooter = this.shooters.get(0);
		if(this.shootingLazerCooldown.checkMoreThan((int)(BOSS_SHOOTING_LAZER_INTERVAL * BOSS_SHOOTING_LAZER_RATE)) || isTesting) {
			if(this.shootingLazerCooldown.checkFinished()) {
				randomLazerLocation = (int) (Math.random() * shooter.width);
				this.shootingLazerCooldown.reset();
			}
			setLazerInterval(INFINITE);
		}
		else {
			setLazerInterval(1);
			if(this.lazerCooldown.checkFinished()) {
				this.lazerCooldown.reset();
				bullets.add(BulletPool.getBullet(shooter.getPositionX()
								+ randomLazerLocation, shooter.getPositionY() + shooter.height,
						BULLET_SPEED * 2));
			}
		}
	}

	public final void bossAttackMechanism3(final Set<Bullet> bullets, final Ship ship) {
		// For now, only ships in the bottom row are able to shoot.
		EnemyShip shooter = this.shooters.get(0);
		if(this.shootingTurningBulletsCooldown.checkMoreThan((int)(BOSS_SHOOTING_TURNING_BULLET_INTERVAL * BOSS_SHOOTING_TURNING_BULLET_RATE)) || isTesting) {
			if(this.shootingTurningBulletsCooldown.checkFinished()) {
				randomTurningBulletLocation = (int) (Math.random() * shooter.width);
				this.shootingTurningBulletsCooldown.reset();
			}
			setTurningBulletsInterval(INFINITE);
		}
		else {
			setTurningBulletsInterval(100);
			if(this.turningBulletsCooldown.checkFinished()) {
				this.turningBulletsCooldown.reset();
				Bullet bullet = BulletPool.getBullet(shooter.getPositionX()
								+ randomTurningBulletLocation, shooter.getPositionY() + shooter.height,
						BULLET_SPEED);
				if(bullet.getPositionX() > ship.getPositionX()) bullet.setIsTurningRight();
				else bullet.setIsTurningLeft();
				bullets.add(bullet);
			}
		}
	}

	public final void bossAttackMechanism4(final Set<Bullet> bullets, final Ship ship) {
		// For now, only ships in the bottom row are able to shoot.
		EnemyShip shooter = this.shooters.get(0);
		if(this.shootingTurningBulletsCooldown.checkMoreThan((int)(BOSS_SHOOTING_TURNING_BULLET_INTERVAL * BOSS_SHOOTING_TURNING_BULLET_RATE)) || isTesting) {
			if(this.shootingTurningBulletsCooldown.checkFinished()) {
				randomTurningBulletLocation = (int) (Math.random() * shooter.width);
				this.shootingTurningBulletsCooldown.reset();
			}
			setTurningBulletsInterval(INFINITE);
		}
		else {
			setTurningBulletsInterval(300);
			if(this.turningBulletsCooldown.checkFinished()) {
				this.turningBulletsCooldown.reset();
					bullets.add(BulletPool.getBullet(shooter.getPositionX()
							+ shooter.width, shooter.getPositionY() + shooter.height/4, BULLET_SPEED));
					bullets.add(BulletPool.getBullet(shooter.getPositionX()
							+ shooter.width, shooter.getPositionY() + shooter.height*3/4, BULLET_SPEED));
					bullets.add(BulletPool.getBullet(shooter.getPositionX()
							, shooter.getPositionY() + shooter.height/4, BULLET_SPEED));
					bullets.add(BulletPool.getBullet(shooter.getPositionX()
							, shooter.getPositionY() + shooter.height*3/4, BULLET_SPEED));
					for(Bullet bullet : bullets) {
						if (bullet.getPositionX() > ship.getPositionX())
							bullet.setIsTurningRight();
						else
							bullet.setIsTurningLeft();
					}
			}
		}
		}

	/**
	 * Destroys a ship.
	 * 
	 * @param destroyedShip
	 *            Ship to be destroyed.
	 */
	public final void destroy(final EnemyShip destroyedShip) {
		for (List<EnemyShip> column : this.enemyShips)
			for (int i = 0; i < column.size(); i++)
				if (column.get(i).equals(destroyedShip)) {
					column.get(i).destroy();
					this.logger.info("Destroyed ship in ("
							+ this.enemyShips.indexOf(column) + "," + i + ")");
				}

		// Updates the list of ships that can shoot the player.
		if (this.shooters.contains(destroyedShip)) {
			int destroyedShipIndex = this.shooters.indexOf(destroyedShip);
			int destroyedShipColumnIndex = -1;

			for (List<EnemyShip> column : this.enemyShips)
				if (column.contains(destroyedShip)) {
					destroyedShipColumnIndex = this.enemyShips.indexOf(column);
					break;
				}

			EnemyShip nextShooter = getNextShooter(this.enemyShips
					.get(destroyedShipColumnIndex));

			if (nextShooter != null)
				this.shooters.set(destroyedShipIndex, nextShooter);
			else {
				this.shooters.remove(destroyedShipIndex);
				this.logger.info("Shooters list reduced to "
						+ this.shooters.size() + " members.");
			}
		}

		this.shipCount--;
	}

	/**
	 * Gets the ship on a given column that will be in charge of shooting.
	 * 
	 * @param column
	 *            Column to search.
	 * @return New shooter ship.
	 */
	public final EnemyShip getNextShooter(final List<EnemyShip> column) {
		Iterator<EnemyShip> iterator = column.iterator();
		EnemyShip nextShooter = null;
		while (iterator.hasNext()) {
			EnemyShip checkShip = iterator.next();
			if (checkShip != null && !checkShip.isDestroyed())
				nextShooter = checkShip;
		}

		return nextShooter;
	}

	/**
	 * Returns an iterator over the ships in the formation.
	 * 
	 * @return Iterator over the enemy ships.
	 */
	@Override
	public final Iterator<EnemyShip> iterator() {
		Set<EnemyShip> enemyShipsList = new HashSet<EnemyShip>();

		for (List<EnemyShip> column : this.enemyShips)
			for (EnemyShip enemyShip : column)
				enemyShipsList.add(enemyShip);

		return enemyShipsList.iterator();
	}

	/**
	 * Checks if there are any ships remaining.
	 * 
	 * @return True when all ships have been destroyed.
	 */
	public final boolean isEmpty() {
		return this.shipCount <= 0;
	}

	public void setShootingInterval(int shootingInterval) {
		if (this.shootingInterval != shootingInterval) {
			this.shootingInterval = shootingInterval;
			this.isShootingIntervalChanged = true;
		} else
			this.isShootingIntervalChanged = false;
	}

	public void setLazerInterval(int lazerInterval) {
		if(this.lazerInterval != lazerInterval) {
			this.lazerInterval = lazerInterval;
			this.isLazerOn = true;
		}
		else this.isLazerOn = false;
	}

	public void setTurningBulletsInterval(int turningBulletsInterval) {
		if(this.turningBulletsInterval != turningBulletsInterval) {
			this.turningBulletsInterval = turningBulletsInterval;
			this.isTurningBulletsOn = true;
		}
		else this.isTurningBulletsOn = false;
	}
}
