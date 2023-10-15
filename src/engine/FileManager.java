package engine;
import java.awt.Font;
import java.awt.FontFormatException;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.util.*;
import java.util.logging.Logger;
import engine.DrawManager.SpriteType;

/**
 * Manages files used in the application.
 * 
 * @author <a href="mailto:RobertoIA1987@gmail.com">Roberto Izquierdo Amo</a>
 * 
 */
public final class FileManager {

	/** Singleton instance of the class. */
	private static FileManager instance;
	/** Application logger. */
	private static Logger logger;
	/** Max number of high scores. */
	private static final int MAX_SCORES = 7;

	/**
	 * private constructor.
	 */
	private FileManager() {
		logger = Core.getLogger();
	}

	/**
	 * Returns shared instance of FileManager.
	 * 
	 * @return Shared instance of FileManager.
	 */
	protected static FileManager getInstance() {
		if (instance == null)
			instance = new FileManager();
		return instance;
	}

	/**
	 * Loads sprites from disk.
	 * 
	 * @param spriteMap
	 *            Mapping of sprite type and empty boolean matrix that will
	 *            contain the image.
	 * @throws IOException
	 *             In case of loading problems.
	 */
	public void loadSprite(Map<SpriteType, boolean[][]> spriteMap)
			throws IOException {
		InputStream inputStream = null;

		try {
			inputStream = DrawManager.class.getClassLoader()
					.getResourceAsStream("graphics");
			char c;
			// Sprite loading.
			for (Map.Entry<SpriteType, boolean[][]> sprite : spriteMap
					.entrySet()) {
				for (int i = 0; i < sprite.getValue().length; i++)
					for (int j = 0; j < sprite.getValue()[i].length; j++) {
						do
							c = (char) inputStream.read();
						while (c != '0' && c != '1');

						if (c == '1')
							sprite.getValue()[i][j] = true;
						else
							sprite.getValue()[i][j] = false;
					}
				logger.fine("Sprite " + sprite.getKey() + " loaded.");
			}
			if (inputStream != null)
				inputStream.close();
		} finally {
			if (inputStream != null)
				inputStream.close();
		}
	}
	/**
	 * Change sprites from disk.
	 *
	 * @param spriteMap,spriteType,graphicsNum
	 *            Changing boolean matrix that will
	 *            change the image.
	 * 			  graphicsNum is col_num(each graphics)
	 * @throws IOException
	 *             In case of changing problems.
	 */
	public void changeSprite(Map<SpriteType, boolean[][]> spriteMap, SpriteType spriteType, int graphicsNum)
			throws IOException {
		InputStream inputStream = checkSpriteType(spriteType);
		try {
			char c;
			for (Map.Entry<SpriteType, boolean[][]> sprite : spriteMap
					.entrySet()) {
				if(sprite.getKey() == spriteType){
					for(int k=-1; k<graphicsNum;k++){
						for (int i = 0; i < sprite.getValue().length; i++)
							for (int j = 0; j < sprite.getValue()[i].length; j++) {
								do
									c = (char) inputStream.read();
								while (c != '0' && c != '1');

								if (c == '1')
									sprite.getValue()[i][j] = true;
								else
									sprite.getValue()[i][j] = false;
							}
					}
					logger.fine("Sprite " + spriteType + " changed.");
					break;
				}
			}
			if (inputStream != null)
				inputStream.close();
		} finally {
			if (inputStream != null)
				inputStream.close();
		}
	}
	/**
	 * Check Sprite Type.
	 *
	 * @param spriteType
	 *            Point size of the font.
	 * @return inputStream.
	 * @throws IOException
	 *             In case of loading problems.
	 */
	public InputStream checkSpriteType(SpriteType spriteType){
		InputStream inputStream = null;
		if(spriteType == SpriteType.Bullet){
			inputStream = DrawManager.class.getClassLoader()
					.getResourceAsStream("bulletGraphics");
		}
		else if(spriteType == SpriteType.Ship){
			inputStream = DrawManager.class.getClassLoader()
					.getResourceAsStream("shipGraphics");
		}
		else if(spriteType == SpriteType.EnemyBullet){
			inputStream = DrawManager.class.getClassLoader()
					.getResourceAsStream("bulletGraphics");
		}
		else if(spriteType == SpriteType.EnemyShipA1){
			inputStream = DrawManager.class.getClassLoader()
					.getResourceAsStream("enemyshipGraphics");
		}
		else if(spriteType == SpriteType.EnemyShipA2){
			inputStream = DrawManager.class.getClassLoader()
					.getResourceAsStream("enemyshipGraphics");
		}
		else if(spriteType == SpriteType.EnemyShipB1){
			inputStream = DrawManager.class.getClassLoader()
					.getResourceAsStream("enemyshipGraphics");
		}
		else if(spriteType == SpriteType.EnemyShipB2){
			inputStream = DrawManager.class.getClassLoader()
					.getResourceAsStream("enemyshipGraphics");
		}
		else if(spriteType == SpriteType.EnemyShipC1){
			inputStream = DrawManager.class.getClassLoader()
					.getResourceAsStream("enemyshipGraphics");
		}
		else if(spriteType == SpriteType.EnemyShipC2){
			inputStream = DrawManager.class.getClassLoader()
					.getResourceAsStream("enemyshipGraphics");
		}
		else if(spriteType == SpriteType.EnemyShipSpecial){
			inputStream = DrawManager.class.getClassLoader()
					.getResourceAsStream("specialenemyGraphics");
		}
		return inputStream;
	}


	/**
	 * Loads a font of a given size.
	 * 
	 * @param size
	 *            Point size of the font.
	 * @return New font.
	 * @throws IOException
	 *             In case of loading problems.
	 * @throws FontFormatException
	 *             In case of incorrect font format.
	 */
	public Font loadFont(final float size) throws IOException,
			FontFormatException {
		InputStream inputStream = null;
		Font font;

		try {
			// Font loading.
			inputStream = FileManager.class.getClassLoader()
					.getResourceAsStream("font.ttf");
			font = Font.createFont(Font.TRUETYPE_FONT, inputStream).deriveFont(
					size);
		} finally {
			if (inputStream != null)
				inputStream.close();
		}

		return font;
	}

	/**
	 * Returns the application default scores if there is no user high scores
	 * file.
	 * 
	 * @return Default high scores.
	 * @throws IOException
	 *             In case of loading problems.
	 */
	private List<Score> loadDefaultHighScores() throws IOException {
		List<Score> highScores = new ArrayList<Score>();
		InputStream inputStream = null;
		BufferedReader reader = null;

		try {
			inputStream = FileManager.class.getClassLoader()
					.getResourceAsStream("scores");
			reader = new BufferedReader(new InputStreamReader(inputStream));

			Score highScore = null;
			String name = reader.readLine();
			String score = reader.readLine();

			while ((name != null) && (score != null)) {
				highScore = new Score(name, Integer.parseInt(score));
				highScores.add(highScore);
				name = reader.readLine();
				score = reader.readLine();
			}
		} finally {
			if (inputStream != null)
				inputStream.close();
		}

		return highScores;
	}

	/**
	 * Loads high scores from file, and returns a sorted list of pairs score -
	 * value.
	 * @param gameMode
	 *             The game mode.
	 * @return Sorted list of scores - players.
	 * @throws IOException
	 *             In case of loading problems.
	 */
	public List<Score> loadHighScores(final int gameMode) throws IOException {

		List<Score> highScores = new ArrayList<Score>();
		InputStream inputStream = null;
		BufferedReader bufferedReader = null;

		try {
			String jarPath = FileManager.class.getProtectionDomain()
					.getCodeSource().getLocation().getPath();
			jarPath = URLDecoder.decode(jarPath, "UTF-8");

			String scoresPath = new File(jarPath).getParent();
			scoresPath += File.separator;
			if (gameMode == 1)
				scoresPath += "scores_1p";
			else
				scoresPath += "scores_2p";

			File scoresFile = new File(scoresPath);
			inputStream = new FileInputStream(scoresFile);
			bufferedReader = new BufferedReader(new InputStreamReader(
					inputStream, Charset.forName("UTF-8")));

			logger.info("Loading user high scores " + "from 'scores_" + gameMode +"p'");

			Score highScore = null;
			String name = bufferedReader.readLine();
			String score = bufferedReader.readLine();

			while ((name != null) && (score != null)) {
				highScore = new Score(name, Integer.parseInt(score));
				highScores.add(highScore);
				name = bufferedReader.readLine();
				score = bufferedReader.readLine();
			}

		} catch (FileNotFoundException e) {
			// loads default if there's no user scores.
			logger.info("Loading default high scores.");
			highScores = loadDefaultHighScores();
		} finally {
			if (bufferedReader != null)
				bufferedReader.close();
		}

		Collections.sort(highScores);
		return highScores;
	}
	
	

	/**
	 * Saves user high scores to disk.
	 * 
	 * @param highScores
	 *            High scores to save.
	 * @throws IOException
	 *             In case of loading problems.
	 */
	public void saveHighScores(final List<Score> highScores, final int gameMode)
			throws IOException {
		OutputStream outputStream = null;
		BufferedWriter bufferedWriter = null;

		try {
			String jarPath = FileManager.class.getProtectionDomain()
					.getCodeSource().getLocation().getPath();
			jarPath = URLDecoder.decode(jarPath, "UTF-8");

			String scoresPath = new File(jarPath).getParent();
			scoresPath += File.separator;
			if (gameMode == 1)
				scoresPath += "scores_1p";
			else
				scoresPath += "scores_2p";

			File scoresFile = new File(scoresPath);

			if (!scoresFile.exists())
				scoresFile.createNewFile();

			outputStream = new FileOutputStream(scoresFile);
			bufferedWriter = new BufferedWriter(new OutputStreamWriter(
					outputStream, Charset.forName("UTF-8")));

			logger.info("Saving user high scores.");

			// Saves 7 or less scores.
			int savedCount = 0;
			for (Score score : highScores) {
				if (savedCount >= MAX_SCORES)
					break;
				bufferedWriter.write(score.getName());
				bufferedWriter.newLine();
				bufferedWriter.write(Integer.toString(score.getScore()));
				bufferedWriter.newLine();
				savedCount++;
			}

		} finally {
			if (bufferedWriter != null)
				bufferedWriter.close();
		}
	}
	public List<Settings> loadSettings() throws IOException {

		List<Settings> settings = new ArrayList<Settings>();
		InputStream inputStream = null;
		BufferedReader bufferedReader = null;

		try {
			String jarPath = FileManager.class.getProtectionDomain()
					.getCodeSource().getLocation().getPath();
			jarPath = URLDecoder.decode(jarPath, "UTF-8");

			String settingPath = new File(jarPath).getParent();
			settingPath += File.separator;
			settingPath += "settings";

			File scoresFile = new File(settingPath);
			inputStream = new FileInputStream(scoresFile);
			bufferedReader = new BufferedReader(new InputStreamReader(
					inputStream, Charset.forName("UTF-8")));

			logger.info("Loading settings.");

			Settings settings1 = null;
			String name = bufferedReader.readLine();
			String value = bufferedReader.readLine();
			settings1 = new Settings(name, Integer.parseInt(value));
			settings.add(settings1);

			name = bufferedReader.readLine();
			value = bufferedReader.readLine();
			settings1 = new Settings(name, Integer.parseInt(value));
			settings.add(settings1);

			name = bufferedReader.readLine();
			value = bufferedReader.readLine();
			while ((name != null) && (value != null)) {
				settings1 = new Settings(name, Integer.parseInt(value,16));
				settings.add(settings1);
				name = bufferedReader.readLine();
				value = bufferedReader.readLine();
			}

		} catch (FileNotFoundException e) {
			// loads default if there's no settings.
			logger.info("Loading default Settings.");
			settings = loaddefaultSettings();
		} finally {
			if (bufferedReader != null)
				bufferedReader.close();
		}
		return settings;
	}
	public List<Settings> loaddefaultSettings() throws IOException {
		List<Settings> Setting = new ArrayList<Settings>();
		InputStream inputStream = null;
		BufferedReader reader = null;

		try {
			inputStream = FileManager.class.getClassLoader()
					.getResourceAsStream("settings");
			reader = new BufferedReader(new InputStreamReader(inputStream));

			Settings Setting1 = null;
			String name = reader.readLine();
			String value = reader.readLine();
			Setting1 = new Settings(name, Integer.parseInt(value));
			Setting.add(Setting1);

			name = reader.readLine();
			value = reader.readLine();
			Setting1 = new Settings(name, Integer.parseInt(value));
			Setting.add(Setting1);

			name = reader.readLine();
			value = reader.readLine();
			while ((name != null) && (value != null)) {
				Setting1 = new Settings(name, Integer.parseInt(value.substring(2),16));
				Setting.add(Setting1);
				name = reader.readLine();
				value = reader.readLine();
			}

			logger.info("Successfully load");
		} finally {
			if (inputStream != null)
				inputStream.close();
		}

		return Setting;
	}


	public static void saveSettings(final List<Settings> setting)
			throws IOException {
		OutputStream outputStream = null;
		BufferedWriter bufferedWriter = null;

		try {
			String jarPath = FileManager.class.getProtectionDomain()
					.getCodeSource().getLocation().getPath();
			jarPath = URLDecoder.decode(jarPath, "UTF-8");

			String settingPath = new File(jarPath).getParent() + File.separator + "settings";
			File settingFlie = new File(settingPath);

			if (!settingFlie.exists())
				settingFlie.createNewFile();

			outputStream = new FileOutputStream(settingFlie);
			bufferedWriter = new BufferedWriter(new OutputStreamWriter(
					outputStream, Charset.forName("UTF-8")));

			logger.info("Saving user settings.");
			bufferedWriter.write(setting.get(0).getName());
			bufferedWriter.newLine();
			bufferedWriter.write(Integer.toString(setting.get(0).getValue()));
			bufferedWriter.newLine();
			bufferedWriter.write(setting.get(1).getName());
			bufferedWriter.newLine();
			bufferedWriter.write(Integer.toString(setting.get(1).getValue()));
			bufferedWriter.newLine();
			// Saves settings.
			for (int i =2; i<18; i++) {
				bufferedWriter.write(setting.get(i).getName());
				bufferedWriter.newLine();
				bufferedWriter.write(Integer.toHexString(setting.get(i).getValue()));
				bufferedWriter.newLine();
			}

		} finally {
			if (bufferedWriter != null)
				bufferedWriter.close();
		}
	}


		
}




