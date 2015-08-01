package ui;

import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
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
    public static final Font TOP_LABEL_FONT = new Font(20.0);
    public static final Insets TOP_LABEL_PADDING = new Insets(0, 0, 5.0, 0);
    public static final String TOP_LABEL_TEXT = "Did you know...";
    public static final String CHECK_BOX_TEXT = "Show at startup";
    public static final String PREVIOUS_TIP_TEXT = "Previous tip";
    public static final String NEXT_TIP_TEXT = "Next tip";
    public static final String CLOSE_TEXT = "Close";
    public static final String TITLE_TEXT = "Tip of the day";
    public static final String ERROR_LOADING_TIP_TEXT = "Error loading tip";
    public static final Insets WINDOW_PADDING = new Insets(10.0);
    public static final Insets CHECK_BOX_PADDING = new Insets(5.0, 0, 5.0, 0);
    public static final Insets NEXT_TIP_MARGIN = new Insets(0, 5.0, 0, 5.0);

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
            html = ERROR_LOADING_TIP_TEXT;
        }
        engine.loadContent(html);
    }

    @Override
    protected Parent content() {
        setTitle(TITLE_TEXT);
        setSize(585.0, 380.0);
        setStageStyle(StageStyle.UTILITY);

        BorderPane borderPane = new BorderPane();
        borderPane.setPadding(WINDOW_PADDING);
        WebView browser = new WebView();
        engine = browser.getEngine();

        Label topLabel = new Label(TOP_LABEL_TEXT);
        topLabel.setPadding(TOP_LABEL_PADDING);
        topLabel.setFont(TOP_LABEL_FONT);

        showAtStartupCheckBox = new CheckBox(CHECK_BOX_TEXT);
        showAtStartupCheckBox.setPadding(CHECK_BOX_PADDING);

        HBox bottomContainer = new HBox();
        Pane rightAlignPane = new Pane();
        Button previousTipButton = new Button(PREVIOUS_TIP_TEXT);
        Button nextTipButton = new Button(NEXT_TIP_TEXT);
        Button closeButton = new Button(CLOSE_TEXT);
        bottomContainer.getChildren().addAll(showAtStartupCheckBox, rightAlignPane,
                previousTipButton, nextTipButton, closeButton);
        bottomContainer.setHgrow(rightAlignPane, Priority.ALWAYS);
        bottomContainer.setMargin(nextTipButton, NEXT_TIP_MARGIN);

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
