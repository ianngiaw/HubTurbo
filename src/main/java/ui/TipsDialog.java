package ui;

import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.markdown4j.Markdown4jProcessor;
import prefs.Preferences;
import tips.TipsFileHandler;
import ui.components.Dialog;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

public class TipsDialog extends Dialog<Boolean> {

    private static final String[] AFFECTED_TAGS = {"a", "div", "p", "header", "footer", "h1", "h2", "h3", "h4", "h5", "h6", "span", "ul", "li"};
    private Stage parentStage;
    private Preferences prefs;
    private Markdown4jProcessor markdownProcessor;
    private TipsFileHandler tipsFileHandler;

    private CheckBox showAtStartupCheckBox;
    private WebEngine engine;

    public TipsDialog (Stage parentStage, Preferences prefs) {
        super(parentStage);
        this.parentStage = parentStage;
        this.prefs = prefs;
        this.tipsFileHandler = TipsFileHandler.getInstance(prefs);
        this.markdownProcessor = new Markdown4jProcessor()
                .addHtmlAttribute("style", "font-family:sans-serif", AFFECTED_TAGS);
        setValues();
    }

    private void setValues() {
        this.showAtStartupCheckBox.setSelected(prefs.isOpenTipsAtStartup());

        setTipContent(tipsFileHandler.getCurrentPath());
    }

    private void setTipContent (String path) {
        String html;
        try {
            html = markdownProcessor.process(new File(path));
        } catch (IOException e) {
            html = "Error loading tip";
        }
        engine.loadContent(html);
    }

    @Override
    protected Parent content() {
        setTitle("Tip of the day");
        setSize(585.0, 380.0);
        setStageStyle(StageStyle.UTILITY);

        BorderPane borderPane = new BorderPane();
        borderPane.setPadding(new Insets(10.0));
        WebView browser = new WebView();
        engine = browser.getEngine();

        Label topLabel = new Label("Did you know...");

        VBox bottomContainer = new VBox();
        showAtStartupCheckBox = new CheckBox("Show at startup");
        HBox buttonContainer = new HBox();
        Button previousTipButton = new Button("Previous tip");
        Button nextTipButton = new Button("Next tip");
        Button closeButton = new Button("Close");
        buttonContainer.getChildren().addAll(previousTipButton, nextTipButton, closeButton);
        bottomContainer.getChildren().addAll(showAtStartupCheckBox, buttonContainer);

        closeButton.setOnMouseClicked(event -> {
            this.close();
            this.parentStage.requestFocus();
        });
        nextTipButton.setOnMouseClicked(event -> {
            Optional<String> path = tipsFileHandler.getNextPath();
            if (path.isPresent()) {
                setTipContent(path.get());
            }
        });
        previousTipButton.setOnMouseClicked(event -> {
            Optional<String> path = tipsFileHandler.getPreviousPath();
            if (path.isPresent()) {
                setTipContent(path.get());
            }
        });

        borderPane.setTop(topLabel);
        borderPane.setCenter(browser);
        borderPane.setBottom(bottomContainer);

        return borderPane;
    }

    @Override
    public void close() {
        prefs.setOpenTipsAtStartup(showAtStartupCheckBox.isSelected());
        super.close();
    }
}
