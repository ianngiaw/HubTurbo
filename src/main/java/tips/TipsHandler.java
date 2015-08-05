package tips;

import prefs.Preferences;
import util.GitHubURL;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TipsHandler {
    public static final String LINE_SPLITTER = "###";
    private static TipsHandler instance;

    private Preferences preferences;
    private List<String> tipLines;
    private int currentIndex = 0;

    private TipsHandler(Preferences preferences) {
        this.preferences = preferences;
        URL allTipsUrl = null;
        try {
            allTipsUrl = new URL(GitHubURL.ALL_TIPS_FILE);
        } catch (MalformedURLException e) {
            // Error
        }
        tipLines = new ArrayList<>();
        List<String> tempTipLines = new ArrayList<>();
        if (allTipsUrl != null) {
            try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(allTipsUrl.openStream()))) {
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    tempTipLines.add(line);
                }
                bufferedReader.close();
            } catch (IOException e) {
                // catch exception
            }
        }
        int readIndex = 0;
        for (String tipLine : tempTipLines) {
            if (preferences.isTipConfirmedRead(getTipFile(tipLine))) {
                tipLines.add(readIndex++, tipLine);
            } else {
                tipLines.add(tipLine);
            }
        }

        while (currentIndex < tipLines.size() && preferences.isTipConfirmedRead(getTipFile(getTipLine(currentIndex)))) {
            currentIndex++;
        }
        if (currentIndex >= tipLines.size()) {
            currentIndex = tipLines.size() - 1;
        }
    }

    public static TipsHandler getInstance (Preferences preferences) {
        if (instance == null) {
            instance = new TipsHandler(preferences);
        }
        return instance;
    }

    public boolean hasUnreadTips () {
        for (String tipLine : tipLines) {
            if (!preferences.isTipConfirmedRead(getTipFile(tipLine))){
                return true;
            }
        }
        return false;
    }

    public void goToNextTip() {
        if (tipLines.size() - 1 > currentIndex) {
            currentIndex++;
        }
    }

    public void goToPreviousTip() {
        if (currentIndex >= 1) {
            currentIndex--;
        }
    }

    public Optional<String> getNextTipName() {
        if (tipLines.size() - 1 > currentIndex) {
            return Optional.of(getTipName(getTipLine(currentIndex + 1)));
        }
        return Optional.empty();
    }

    public  Optional<String> getPreviousTipName() {
        if (currentIndex >= 1) {
            return Optional.of(getTipName(getTipLine(currentIndex - 1)));
        }
        return Optional.empty();
    }

    public String getCurrentTipName() {
        return getTipName(getTipLine(currentIndex));
    }

    public String getCurrentTipFile() {
        return getTipFile(getTipLine(currentIndex));
    }

    private String getTipLine(int index) {
        return tipLines.get(index);
    }

    private String getTipName(String tipLine) {
        return tipLine.split(LINE_SPLITTER)[0].trim();
    }

    private String getTipFile(String tipLine) {
        return tipLine.split(LINE_SPLITTER)[1].trim();
    }
}
