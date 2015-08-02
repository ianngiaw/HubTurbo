package tips;

import prefs.Preferences;
import util.GitHubURL;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TipsUrlHandler {
    private static TipsUrlHandler instance;

    private Preferences preferences;
    private List<String> tipNames;
    private int currentIndex = 0;

    private TipsUrlHandler(Preferences preferences) {
        this.preferences = preferences;
        URL allTipsUrl = null;
        try {
            allTipsUrl = new URL(GitHubURL.ALL_TIPS_FILE);
        } catch (MalformedURLException e) {
            // Error
        }
        tipNames = new ArrayList<>();
        if (allTipsUrl != null) {
            try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(allTipsUrl.openStream()))) {
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    tipNames.add(line);
                }
                bufferedReader.close();
            } catch (IOException e) {
                // catch exception
            }
        }

        while (currentIndex < tipNames.size() && preferences.isTipViewed(tipNames.get(currentIndex))) {
            currentIndex++;
        }
        if (currentIndex >= tipNames.size()) {
            currentIndex = tipNames.size() - 1;
        }
    }

    public static TipsUrlHandler getInstance (Preferences preferences) {
        if (instance == null) {
            instance = new TipsUrlHandler(preferences);
        }
        return instance;
    }

    public void goToNextTip() {
        if (tipNames.size() - 1 > currentIndex) {
            currentIndex++;
        }
    }

    public void goToPreviousTip() {
        if (currentIndex >= 1) {
            currentIndex--;
        }
    }

    public Optional<String> getNextTip() {
        if (tipNames.size() - 1 > currentIndex) {
            return Optional.of(getTip(currentIndex + 1));
        }
        return Optional.empty();
    }

    public  Optional<String> getPreviousTip() {
        if (currentIndex >= 1) {
            return Optional.of(getTip(currentIndex - 1));
        }
        return Optional.empty();
    }

    public String getCurrentTip() {
        return getTip(currentIndex);
    }

    private String getTip(int index) {
        return tipNames.get(index);
    }
}
