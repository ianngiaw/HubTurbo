package tips;

import prefs.Preferences;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TipsFileHandler {
    private static final String TIPS_FOLDER_PATH = "docs" + File.separator + "tips";
    private static final String ALL_TIPS_FILE_PATH = TIPS_FOLDER_PATH + File.separator + "all-tips.txt";

    private static TipsFileHandler instance;

    private Preferences preferences;
    private List<String> tipNames;
    private int currentIndex = 0;

    private TipsFileHandler (Preferences preferences) {
        this.preferences = preferences;
        File allTipsFile = new File(ALL_TIPS_FILE_PATH);
        tipNames = new ArrayList<>();
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(allTipsFile))) {
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                tipNames.add(line);
            }
            bufferedReader.close();
        } catch (IOException e) {
            // catch exception
        }

        while (currentIndex < tipNames.size() && preferences.isTipViewed(tipNames.get(currentIndex))) {
            currentIndex++;
        }
        if (currentIndex >= tipNames.size()) {
            currentIndex = tipNames.size() - 1;
        }
    }

    public static TipsFileHandler getInstance (Preferences preferences) {
        if (instance == null) {
            instance = new TipsFileHandler(preferences);
        }
        return instance;
    }

    public Optional<String> getNextPath () {
        if (tipNames.size() - 1 > currentIndex) {
            return Optional.of(getPath(++currentIndex));
        }
        return Optional.empty();
    }

    public  Optional<String> getPreviousPath () {
        if (currentIndex >= 1) {
            return Optional.of(getPath(--currentIndex));
        }
        return Optional.empty();
    }

    public String getCurrentPath () {
        return getPath(currentIndex);
    }

    private String getPath (int index) {
        String name = tipNames.get(index);
        if (!preferences.isTipViewed(name)) {
            preferences.addTipViewed(name);
        }
        return generateTipFilePath(name);
    }

    private String generateTipFilePath (String tipFileName) {
        return TIPS_FOLDER_PATH + File.separator + tipFileName;
    }
}
