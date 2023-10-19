package screen;

import java.awt.event.KeyEvent;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

import engine.AchievementManager;
import engine.Cooldown;
import engine.Core;
import engine.GameState;
import engine.Score;

/**
 * Implements the score screen.
 * 
 * @author <a href="mailto:RobertoIA1987@gmail.com">Roberto Izquierdo Amo</a>
 * 
 */
public class ScoreScreen extends Screen {

	/** Milliseconds between changes in user selection. */
	private static final int SELECTION_TIME = 200;
	/** Maximum number of high scores. */
	private static final int MAX_HIGH_SCORE_NUM = 7;
	/** Code of first mayus character. */
	private static final int FIRST_CHAR = 65;
	/** Code of last mayus character. */
	private static final int LAST_CHAR = 90;

	/** Current gameMode. */
	private int gameMode;
	/** Current score. */
	private int score;
	/** 1p's lives left. */
	private int livesRemaining1;
	/** 2p's lives left. */
	private int livesRemaining2;
	/** Total bullets shot by the players. */
	private int bulletsShot;
	/** Total ships destroyed by the player. */
	private int shipsDestroyed;
	/** List of past high scores. */
	private List<Score> highScores;
	/** Checks if current score is a new high score. */
	private boolean isNewRecord;
	/** Player name for record input. */
	private char[] name;
	/** Character of players name selected for change. */
	private int nameCharSelected;
	/** Time between changes in user selection. */
	private Cooldown selectionCooldown;

	/**
	 * Constructor, establishes the properties of the screen.
	 * 
	 * @param width
	 *            Screen width.
	 * @param height
	 *            Screen height.
	 * @param fps
	 *            Frames per second, frame rate at which the game is run.
	 * @param gameState
	 *            Current game state.
	 */
	public ScoreScreen(final int width, final int height, final int fps,
			final GameState gameState) {
		super(width, height, fps);

		this.gameMode = gameState.getMode();
		this.score = gameState.getScore();
		this.livesRemaining1 = gameState.getLivesRemaining1p();

		if (gameState.getMode() == 2) {
			this.livesRemaining2 = gameState.getLivesRemaining2p();
		}

		this.bulletsShot = gameState.getBulletsShot1() + gameState.getBulletsShot2();
		this.shipsDestroyed = gameState.getShipsDestroyed();
		this.isNewRecord = false;
		this.name = "AAA".toCharArray();
		this.nameCharSelected = 0;
		this.selectionCooldown = Core.getCooldown(SELECTION_TIME);
		this.selectionCooldown.reset();

		AchievementManager.getInstance().checkScore(this.score);

		try {
			this.highScores = Core.getFileManager().loadHighScores(this.gameMode);
			if (highScores.size() < MAX_HIGH_SCORE_NUM
					|| highScores.get(highScores.size() - 1).getScore()
					< this.score)
				this.isNewRecord = true;

		} catch (IOException e) {
			logger.warning("Couldn't load high scores!");
		}
	}

	/**
	 * Starts the action.
	 * 
	 * @return Next screen code.
	 */
	public final int run() {
		super.run();

		return this.returnCode;
	}

	/**
	 * Updates the elements on screen and checks for events.
	 */
	protected final void update() {
		super.update();

		if (gameMode == 1) {
			draw();
		} else {
			draw2();
		}

		if (this.inputDelay.checkFinished()) {
			if (inputManager.isKeyDown(KeyEvent.VK_ESCAPE)) {
				// Return to main menu.
				this.returnCode = 1;
				this.isRunning = false;
				saveScore(gameMode);
			} else if (inputManager.isKeyDown(KeyEvent.VK_SPACE)) {
				// Play again.
				this.returnCode = 2;
				this.isRunning = false;
				saveScore(gameMode);
			}

			if (this.isNewRecord && this.selectionCooldown.checkFinished()) {
				if (inputManager.isKeyDown(KeyEvent.VK_RIGHT)) {
					this.nameCharSelected = this.nameCharSelected == 2 ? 0
							: this.nameCharSelected + 1;
					this.selectionCooldown.reset();
				}
				if (inputManager.isKeyDown(KeyEvent.VK_LEFT)) {
					this.nameCharSelected = this.nameCharSelected == 0 ? 2
							: this.nameCharSelected - 1;
					this.selectionCooldown.reset();
				}
				if (inputManager.isKeyDown(KeyEvent.VK_UP)) {
					this.name[this.nameCharSelected] =
							(char) (this.name[this.nameCharSelected]
									== LAST_CHAR ? FIRST_CHAR
							: this.name[this.nameCharSelected] + 1);
					this.selectionCooldown.reset();
				}
				if (inputManager.isKeyDown(KeyEvent.VK_DOWN)) {
					this.name[this.nameCharSelected] =
							(char) (this.name[this.nameCharSelected]
									== FIRST_CHAR ? LAST_CHAR
							: this.name[this.nameCharSelected] - 1);
					this.selectionCooldown.reset();
				}
			}
		}

	}

	/**
	 * Saves the score as a high score.
	 *
	 * @param gameMode
	 *            Current game mode.
	 */
	private void saveScore(final int gameMode) {
		highScores.add(new Score(new String(this.name), score));
		Collections.sort(highScores);
		if (highScores.size() > MAX_HIGH_SCORE_NUM)
			highScores.remove(highScores.size() - 1);

		try {
			Core.getFileManager().saveHighScores(highScores, gameMode);
		} catch (IOException e) {
			logger.warning("Couldn't load high scores!");
		}
	}

	/**
	 * Draws the elements associated with the screen.
	 */
	private void draw() {
		drawManager.initDrawing(this);

		drawManager.drawGameOver(this, this.inputDelay.checkFinished(),
				this.isNewRecord);
		drawManager.drawResults(this, this.score, this.livesRemaining1,
				this.shipsDestroyed, (float) this.shipsDestroyed
						/ this.bulletsShot, this.isNewRecord);

		if (this.isNewRecord)
			drawManager.drawNameInput(this, this.name, this.nameCharSelected);

		drawManager.completeDrawing(this);
	}

	/**
	 * Draws the elements associated with the screen for 2P mode.
	 */
	private void draw2() {
		drawManager.initDrawing(this);

		drawManager.drawGameOver(this, this.inputDelay.checkFinished(),
				this.isNewRecord);
		drawManager.drawResults(this, this.score, this.livesRemaining1, this.livesRemaining2,
				this.shipsDestroyed, (float) this.shipsDestroyed
						/ this.bulletsShot, this.isNewRecord);

		if (this.isNewRecord)
			drawManager.drawNameInput(this, this.name, this.nameCharSelected);

		drawManager.completeDrawing(this);
	}
}