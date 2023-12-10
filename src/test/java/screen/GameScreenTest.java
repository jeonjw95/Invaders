package screen;

import static org.junit.jupiter.api.Assertions.*;

import engine.DrawManager.SpriteType;
import engine.GameSettings;
import engine.GameState;
import entity.Bullet;
import entity.EnemyShip;
import entity.EnemyShipFormation;
import entity.Ship;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/** 테스트 클래스: GameScreenTest */
class GameScreenTest {

  /** 게임 화면의 너비 */
  private static final int WIDTH = 448;
  /** 게임 화면의 높이 */
  private static final int HEIGHT = 520;
  /** 초당 프레임 수 */
  private static final int FPS = 60;
  /** 최대 목숨 수 */
  private static final int MAX_LIVES = 3;
  /** 테스트 설정 : 실제 설정과 같은 설정 */
  private static final GameSettings SETTINGS_BASE_LEVEL =
      new GameSettings(5, 4, 60, 2000);
  /** 테스트 설정 : Ship이 빠르게 죽는 상황을 볼 수 있도록 설정 */
  private static final GameSettings SETTINGS_MANY_ENEMYSHIPS =
      new GameSettings(10, 10, 40, 1);
  /** 테스트 설정 : EnemyShip들이 빠르게 전멸하는 상황을 볼 수 있도록 설정 */
  private static final GameSettings SETTINGS_ONE_ENEMYSHIP =
      new GameSettings(1, 1, 20, 10000);
  /** 게임 상태 */
  private static GameState gameState;
  /** 게임 설정 리스트 */
  private static List<GameSettings> gameSettings;
  /** 테스트할 게임 화면 */
  private static GameScreen gameScreen;
  /** 현재 게임 설정 */
  private static GameSettings currentGameSettings;
  /** 현재 가상 스크린에 날아가고 있는 총알들*/
  private static Set<Bullet> bullets;
  /** 현재 가상 스크린에 생성된 EnemyShipFormation*/
  private static EnemyShipFormation enemyShipFormation;
  /** 현재 가상 스크린에 생성된 Ship*/
  private static Ship ship;
  /** 테스트 레벨*/
  private static int testLevel;
  /** 보너스 목숨 여부*/
  private static boolean bonusLife;
  private static boolean test0;
  private static boolean test1;
  private static boolean test2;
  private static boolean test3;
  private static boolean test4;
  private static boolean test5;



  /** 테스트 실행 전 설정 초기화*/
  @BeforeAll
  static void setUp() {
    gameSettings = new ArrayList<GameSettings>();
    gameSettings.add(SETTINGS_BASE_LEVEL);
    gameSettings.add(SETTINGS_BASE_LEVEL);
    gameSettings.add(SETTINGS_BASE_LEVEL);
    gameSettings.add(SETTINGS_BASE_LEVEL);
    gameSettings.add(SETTINGS_BASE_LEVEL);
    gameSettings.add(SETTINGS_BASE_LEVEL);
    gameSettings.add(SETTINGS_MANY_ENEMYSHIPS);
    gameSettings.add(SETTINGS_ONE_ENEMYSHIP);
    testLevel = 1;
    test0 = false;
    test1 = false;
    test2 = false;
    test3 = false;
    test4 = false;
    test5 = false;
  }

  /** 각 테스트 메소드 실행 전 GameScreen 초기화*/
  @BeforeEach
  void initialize() {
    bonusLife = false;
    gameState = new GameState(testLevel, 0, MAX_LIVES, 0, 0);
    currentGameSettings = gameSettings.get(gameState.getLevel() - 1);
    gameScreen = new GameScreen(gameState, currentGameSettings, bonusLife, WIDTH, HEIGHT, FPS);
    gameScreen.initialize();
    enemyShipFormation = gameScreen.getEnemyShipFormation();
    enemyShipFormation.update();
    ship = gameScreen.getShip();
    bullets = gameScreen.getBullets();
    gameScreen.setTesting(true);
    enemyShipFormation.setTesting(true);
    testLevel++;
  }

  @Test
  @DisplayName("initialize 메소드 테스트")
  /** GameScreen이 객체들을 잘 생성하는지 확인 */
  void testGameScreenInitialization() {
    /** gameScreen initialize*/
    assertNotNull(gameScreen); // gameScreen이 null이면 안됨.
    assertNotNull(ship); // Ship 생성여부확인
    assertNotNull(enemyShipFormation); // EnemyShipFormation 생성여부확인
    assertNotNull(bullets); // Bullets 생성여부확인
    assertEquals(0, bullets.size()); // 빈 리스트로 선언되었는지 확인
    assertEquals(WIDTH / 2, gameScreen.getShip().getPositionX()); // Ship이 지정된 위치에 생성되었는지 확인
    assertEquals(HEIGHT - 30, gameScreen.getShip().getPositionY()); // Ship이 지정된 위치에 생성되었는지 확인
    assertFalse(gameScreen.getEnemyShipFormation().isEmpty()); // EnemyShipFormation이 비어있으면 안됨

    /** bonusLife == false인 상황*/
    assertEquals(MAX_LIVES, gameScreen.getLives()); // lives == MAX_LIVES

    /** bonusLife == true인 상황*/
    bonusLife = true;
    gameScreen = new GameScreen(gameState, currentGameSettings, bonusLife, WIDTH, HEIGHT, FPS);
    gameScreen.initialize();
    assertEquals(MAX_LIVES + 1, gameScreen.getLives()); // // lives == MAX_LIVES+1
    test0 = true;
  }

  @Nested
  @DisplayName("update 메소드 테스트")
  /**  update 메소드가 처리해야만 하는 상황들에 대한 테스트 */
  class TestUpdate {

    @Test
    @DisplayName("manageCollisions 테스트1")
    /** Ship이 총알을 맞는 경우 */
    void testUpdateManageCollisions1() {
      /** gameScreen initialize*/
      assertEquals(MAX_LIVES, gameScreen.getLives()); // lives == MAX_LIVES

      /** Ship과 동일한 위치에 EnemyBullet 생성*/
      Bullet bullet = new Bullet(ship.getPositionX(), ship.getPositionY(), 1);
      bullets.add(bullet);
      gameScreen.update(); // 총에 맞았는지 확인

      /** 총 맞은 후 */
      assertEquals(MAX_LIVES - 1, gameScreen.getLives()); // lives == MAX_LIVES-1
      test1 = true;
    }


    @Test
    @DisplayName("manageCollisions 테스트2")
    /** EnemyShip이 총알을 맞는 경우 */
    void testUpdateManageCollisions2() {
      /** gameScreen initialize*/
      EnemyShip enemyShip = enemyShipFormation.iterator().next();
      assertFalse(enemyShip.isDestroyed()); // enemyShip의 isDestroyed 값이 False

      /** EnemyShip과 동일한 위치에 Bullet 생성*/
      Bullet bullet = new Bullet(enemyShip.getPositionX(), enemyShip.getPositionY(), -1);
      bullets.add(bullet);
      gameScreen.setTesting(false);
      gameScreen.update(); // 총에 맞았는지 확인

      /** 총 맞은 후 */
      assertTrue(enemyShip.isDestroyed()); // enemyShip의 isDestroyed 값이 True
      test2 = true;
    }

    @Test
    @DisplayName("Ship shoot 테스트")
    /** Ship이 총을 잘 쏘는지 쏜 총알의 위치가 잘 바뀌는지 확인 */
    void testUpdateShipShoot() {
      // Ship이 총을 잘 쏘는지 확인
      assertEquals(0, bullets.size());
      ship.shoot(bullets);
      assertEquals(1, bullets.size());

      // 쏜 총알의 위치가 잘 update되는지 확인
      Bullet bullet = bullets.iterator().next();
      assertEquals(bullet.getPositionY(), ship.getPositionY());
      gameScreen.update();
      assertEquals(bullet.getPositionY(), ship.getPositionY() + bullet.getSpeedY());
      test3 = true;
    }

    @Test
    @DisplayName("EnemyShip shoot 테스트")
    /** EnemyShip이 총을 잘 쏘는지 쏜 총알의 위치가 잘 바뀌는지 확인 */
    void testUpdateEnemyShipShoot() {
      // EnemyShip이 총을 잘 쏘는지 확인
      assertEquals(0, bullets.size());
      enemyShipFormation.shoot(bullets);
      assertEquals(1, bullets.size());

      Bullet bullet = bullets.iterator().next();
      int shooterPositionY = enemyShipFormation.getShooters().get(0).getPositionY();
      assertEquals(bullet.getPositionY(), shooterPositionY);

      // 쏜 총알의 위치가 잘 update되는지 확인
      gameScreen.update();
      assertEquals(bullet.getPositionY(), shooterPositionY + bullet.getSpeedY());
      assertEquals(2, bullets.size());
      test4 = true;
    }

    @Test
    @DisplayName("EnemyShipFormation update 테스트")
    /** enemyShipFormation의 위치가 변하는지 확인 */
    void testUpdateEnemyShipFormationLocation() {
      // 원래 위치 저장
      int positionX = enemyShipFormation.getPositionX();
      int positionY = enemyShipFormation.getPositionY();
      // 위치 update
      gameScreen.update();
      // 위치가 바뀌지 않으면 fail 바뀌었으면 success
      assertNotEquals(positionX + positionY,
          enemyShipFormation.getPositionX() + enemyShipFormation.getPositionY());
      test5 = true;
    }

    @Nested
    @DisplayName("가상게임 진행")
    /** 가상게임을 진행하여 게임이 잘 끝나는지 확인 - 위에 있는 테스트들을 통과하는지에 따라 테스트 할지 말지 정해짐 */
    class TestSimulation {

      @Test
      @DisplayName("가상게임 테스트1")
      /** Ship이 체력을 다 잃어서 게임이 끝나는 경우 */
      void testSimulation1() {
        /** gameScreen initialize*/
        assertFalse(gameScreen.isLevelFinished()); // 생성된 직후에는 LevelFinished 값이 False.
        assertEquals(SpriteType.Ship, ship.getSpriteType()); // 생성된 직후에는 Ship의 SpriteType은 Ship

        /** 가상 게임 실행 (ship이 체력을 다 잃을 때까지 반복)*/
        while (gameScreen.getLives() != 0 && test1 && test4 && test5) {
          int bulletsNum = bullets.size();
          // Enemyship이 shoot을 했는데 총알이 쏜 것으로 처리되지 않았다면 실패로 처리
          if (enemyShipFormation.shoot(bullets) && bulletsNum == bullets.size())
            fail();
          gameScreen.update();
        }
        ship.update();

        /** 가상 게임 종료*/
        assertTrue(gameScreen.isLevelFinished()); // 가상 게임 종료 후에는 LevelFinished 값이 True.
        assertEquals(SpriteType.ShipDestroyed,
            ship.getSpriteType()); // 가상 게임 종료 후에는 Ship의 SpriteType은 ShipDestroyed

      }

      @Test
      @DisplayName("가상게임 테스트2")
      /** EnemyShip이 다 죽어서 게임이 끝나는 경우 */
      void testSimulation2() {
        /** gameScreen initialize*/
        EnemyShip enemyShip = enemyShipFormation.iterator().next();
        assertFalse(gameScreen.isLevelFinished()); // 생성된 직후에는 LevelFinished 값이 False.
        assertNotEquals(SpriteType.Explosion,
            enemyShip.getSpriteType()); // 생성된 직후에는 enemyShip의 SpriteType은 Explosion이 아니어야 함.

        /** 가상 게임 실행 (ship이 EnemyShip을 다 파괴할 때까지 반복)*/
        while (!enemyShipFormation.isEmpty() && test2 && test3 && test5) {
          int bulletsNum = bullets.size();
          // ship이 shoot을 했는데 총알이 쏜 것으로 처리되지 않았다면 실패로 처리
          if (ship.shoot(bullets) && bulletsNum == bullets.size())
            fail();
          gameScreen.update();
        }

        /** 가상 게임 종료*/
        assertTrue(gameScreen.isLevelFinished()); // 가상 게임 종료 후에는 LevelFinished 값이 True.
        assertEquals(SpriteType.Explosion,
            enemyShip.getSpriteType()); // 가상 게임 종료 후에는 enemyShip의 SpriteType은 Explosion

      }
    }
  }



}
