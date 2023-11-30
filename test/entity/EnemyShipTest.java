package entity;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import engine.DrawManager.SpriteType;

import java.awt.*;


class EnemyShipTest {

    @Test
    @DisplayName("Boss Test")
    void isBoss() {
        // Arrange
        EnemyShip normalShip = new EnemyShip(0, 0, SpriteType.EnemyShipA1);
        EnemyShip bossShip1 = new EnemyShip(0, 0, 1, 1, SpriteType.BossA, Color.RED);

        // Assert
        assertFalse(normalShip.isBoss());
        assertTrue(bossShip1.isBoss());
    }

    @Test
    void isHpValue() {
        // Arrange
        EnemyShip bossShip2 = new EnemyShip(0, 0, 1, 1, SpriteType.BossA, Color.RED);
        int expectedHp = 10;

        // Act
        int actualHp = bossShip2.isHpValue();

        // Assert
        assertEquals(expectedHp, actualHp);
    }

    @Test
    void getDamage() {
        // Arrange
        EnemyShip bossShip2 = new EnemyShip(0, 0, 1, 1, SpriteType.BossA, Color.RED);
        int initialHp = bossShip2.isHpValue();
        int damage = 3;

        // Act
        bossShip2.getDamage(damage);

        // Assert
        assertEquals(initialHp - damage, bossShip2.isHpValue());
    }
}