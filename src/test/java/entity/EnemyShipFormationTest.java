package entity;

import static org.junit.jupiter.api.Assertions.*;

import engine.GameSettings;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.HashSet;
import java.util.Set;

/**
 * 테스트 클래스: BossAttackMechanismTest
 */
class BossAttackMechanismTest {

  /**
   * 무한대 상수.
   */
  private static final int INFINITE = 99999;
  /**
   * 기본 레벨 설정.
   */
  private static final GameSettings SETTINGS_BASE_LEVEL =
      new GameSettings(5, 4, 60, 2000);

  /**
   * 테스트할 EnemyShipFormation 및 Bullet 객체.
   */
  private static EnemyShipFormation enemyShipFormation;
  /**
   * 임의 생성 ship 객체.
   */
  private static Ship ship;
  /**
   * 임의 생성 Bullet Set.
   */
  private static Set<Bullet> bullets;

  /**
   * 각 테스트 메소드 실행 전 초기화.
   */
  @BeforeEach
  void initialize() {
    enemyShipFormation = new EnemyShipFormation(SETTINGS_BASE_LEVEL);
    enemyShipFormation.update();
    ship = new Ship(0, 0);
    bullets = new HashSet<>();
    enemyShipFormation.setTesting(true);
  }

  /**
   * BossAttackMechanism1 테스트
   */
  @Test
  void testBossAttackMechanism1() {
    // 메커니즘1 실행
    enemyShipFormation.bossAttackMechanism1(bullets);

    // 발사된 총알이 7개인지 확인
    assertEquals(7, bullets.size());
  }

  /**
   * BossAttackMechanism2 테스트
   */
  @Test
  void testBossAttackMechanism2() {
    // 메커니즘2 실행
    enemyShipFormation.bossAttackMechanism2(bullets);

    // 레이저 인터벌이 무한대인지 확인
    assertEquals(enemyShipFormation.getLazerInterval(), INFINITE);

    // 테스팅 비활성화 후 메커니즘2 실행
    enemyShipFormation.setTesting(false);
    enemyShipFormation.bossAttackMechanism2(bullets);

    // 레이저 인터벌이 1인지 확인
    assertEquals(enemyShipFormation.getLazerInterval(), 1);
  }

  /**
   * BossAttackMechanism3 테스트
   */
  @Test
  void testBossAttackMechanism3() {
    // 메커니즘3 실행
    enemyShipFormation.bossAttackMechanism3(bullets, ship);

    // 터닝 총알 인터벌이 무한대인지 확인
    assertEquals(enemyShipFormation.getTurningBulletsInterval(), INFINITE);

    // 테스팅 비활성화 후 메커니즘3 실행
    enemyShipFormation.setTesting(false);
    enemyShipFormation.bossAttackMechanism3(bullets, ship);

    // 터닝 총알 인터벌이 100인지 확인
    assertEquals(enemyShipFormation.getTurningBulletsInterval(), 100);
  }

  /**
   * BossAttackMechanism4 테스트
   */
  @Test
  void testBossAttackMechanism4() {
    // 메커니즘4 실행
    enemyShipFormation.bossAttackMechanism4(bullets, ship);

    // 터닝 총알 인터벌이 무한대인지 확인
    assertEquals(enemyShipFormation.getTurningBulletsInterval(), INFINITE);

    // 테스팅 비활성화 후 메커니즘4 실행
    enemyShipFormation.setTesting(false);
    enemyShipFormation.bossAttackMechanism4(bullets, ship);

    // 터닝 총알 인터벌이 300인지 확인
    assertEquals(enemyShipFormation.getTurningBulletsInterval(), 300);
  }
}

