package engine;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class SoundManagerTest {

  private static final String TEST_SOUND_FILE_PATH = "res/WelcomeToTheHell.wav";
  private static final String TEST_CLIP_NAME = "testClip";


  @Test
  void testPlaySound() {
    // 페이드 없이 사운드를 재생하는 경우를 테스트
    assertDoesNotThrow(
        () -> SoundManager.playSound(TEST_SOUND_FILE_PATH, TEST_CLIP_NAME, false, false, 1.0f));
  }

  @Test
  public void testPlaySoundWithFadeIn() {
    // 페이드 인을 사용하여 사운드를 재생하는 경우를 테스트
    assertDoesNotThrow(
        () -> SoundManager.playSound(TEST_SOUND_FILE_PATH, TEST_CLIP_NAME, false, 1.0f));
  }

  @Test
  void testStopSound() {
    // 페이드 없이 사운드를 정지하는 경우를 테스트
    SoundManager.playSound(TEST_SOUND_FILE_PATH, TEST_CLIP_NAME, false, false, 1.0f); // 사운드를 먼저 재생
    assertDoesNotThrow(() -> SoundManager.stopSound(TEST_CLIP_NAME));
  }

  @Test
  public void testStopSoundWithoutFade() {
    // 페이드 없이 사운드를 정지하는 경우를 테스트
    SoundManager.playSound(TEST_SOUND_FILE_PATH, TEST_CLIP_NAME, false, false, 1.0f); // 사운드를 먼저 재생
    assertDoesNotThrow(() -> SoundManager.stopSound(TEST_CLIP_NAME));
  }

  @Test
  public void testStopSoundWithFadeOut() {
    // 페이드 아웃을 사용하여 사운드를 정지하는 경우를 테스트
    SoundManager.playSound(TEST_SOUND_FILE_PATH, TEST_CLIP_NAME, false, 1.0f); // 사운드를 먼저 재생
    assertDoesNotThrow(() -> SoundManager.stopSound(TEST_CLIP_NAME, 1.0f));
  }
}