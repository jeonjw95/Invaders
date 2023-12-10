package screen;

import engine.Cooldown;
import engine.Core;
import entity.Ship;

import java.awt.event.KeyEvent;

import static screen.GameScreen.*;

public class BuffScreen extends Screen {

  /**
   * Milliseconds between changes in user selection.
   */
  private static final int SELECTION_TIME = 200;

  /**
   * Time between changes in user selection.
   */
  private final Cooldown selectionCooldown;
  private final GameScreen gameScreen;
  private int itemCode;
  private static int hpSelected = 0;
  private static int shipSpeedSelected = 0;
  private static int bulletSpeedSelected = 0;


  /**
   * Constructor, establishes the properties of the screen.
   *
   * @param width  Screen width.
   * @param height Screen height.
   * @param fps    Frames per second, frame rate at which the game is run.
   */
  public BuffScreen(int width, int height, int fps, GameScreen gameScreen) {
    super(width, height, fps);
    this.itemCode = 2;
    this.selectionCooldown = Core.getCooldown(SELECTION_TIME);
    this.selectionCooldown.reset();
    this.gameScreen = gameScreen;
  }


  protected final void update() {
    super.update();
    draw();
    if (this.selectionCooldown.checkFinished()
        && this.inputDelay.checkFinished()) {
      if (inputManager.isKeyDown(KeyEvent.VK_UP)
          || inputManager.isKeyDown(KeyEvent.VK_W)) {
        previousMenuItem();
        this.selectionCooldown.reset();
      }
      if (inputManager.isKeyDown(KeyEvent.VK_DOWN)
          || inputManager.isKeyDown(KeyEvent.VK_S)) {
        nextMenuItem();
        this.selectionCooldown.reset();
      }
      if (inputManager.isKeyDown(KeyEvent.VK_SPACE)) {
        if (hpSelected >= 5 || shipSpeedSelected >= 5 || bulletSpeedSelected >= 5) {
          logger.info("This item has already been selected 5 times.");
        }
        if (itemCode == 2 && hpSelected < 5) {
          gameScreen.setHpSelected(true);
          hpSelected++;
          logger.info("HP buff selected.");
        }
        if (itemCode == 3 && shipSpeedSelected < 5) {
          gameScreen.setShipSpeedSelected(true);
          shipSpeedSelected++;
          logger.info("Ship speed buff selected.");
        }
        if (itemCode == 4 && bulletSpeedSelected < 5) {
          gameScreen.setBulletSpeedSelected(true);
          bulletSpeedSelected++;
          logger.info("Bullet speed buff selected.");
        }
        this.isRunning = false;
      }
    }
  }

  /**
   * Shifts the focus to the next menu item.
   */
  private void nextMenuItem() {
      if (this.itemCode == 3) {
          this.itemCode = 4;
      } else if (this.itemCode == 4) {
          this.itemCode = 2;
      } else {
          this.itemCode++;
      }
  }

  /**
   * Shifts the focus to the previous menu item.
   */
  private void previousMenuItem() {
      if (this.itemCode == 4) {
          this.itemCode = 3;
      } else if (this.itemCode == 2) {
          this.itemCode = 4;
      } else {
          this.itemCode--;
      }
  }

  private void draw() {
    drawManager.initDrawing(this);

    drawManager.drawBuff(this, this.itemCode, hpSelected, shipSpeedSelected, bulletSpeedSelected);

    drawManager.completeDrawing(this);
  }

}
