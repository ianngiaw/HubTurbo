package ui;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Popup;
import javafx.stage.Window;
import prefs.Preferences;
import tips.TipsUrlHandler;

import java.util.Optional;

public class TipsPopup extends Popup {

    private static final Font TOP_LABEL_FONT = Font.font(null, FontWeight.BOLD, 20);
    private static final Insets BOTTOM_PADDING = new Insets(0, 0, 5.0, 0);
    private static final String TOP_LABEL_TEXT = "Tip of the Day";
    private static final String CHECK_BOX_TEXT = "Show at startup";
    private static final String PREVIOUS_TIP_TEXT = "Previous tip";
    private static final String NEXT_TIP_TEXT = "Next tip";
    private static final String CLOSE_TEXT = "Close";
    private static final Insets WINDOW_PADDING = new Insets(10.0);
    private static final Insets CHECK_BOX_PADDING = new Insets(5.0, 0, 5.0, 0);
    private static final Insets NEXT_TIP_MARGIN = new Insets(0, 10.0, 0, 10.0);
    private static final Color UNSELECTED_LABEL_COLOR = Color.web("#888888");
    private static final Font SELECTED_LABEL_FONT = Font.font(null, FontWeight.BOLD, 15);
    private static final String CURRENT_TIP_POINTER_TEXT = ">";
    private static final Background POPUP_BACKGROUND = new Background(new BackgroundFill(Color.web("#EEEEEE"), new CornerRadii(5.0), Insets.EMPTY));

    private UI parentUI;
    private Preferences prefs;
    private TipsUrlHandler tipsUrlHandler;

    private BorderPane container;
    private CheckBox showAtStartupCheckBox;
    private Label previousTipLabel, currentTipLabel, nextTipLabel;

    public TipsPopup(UI ui, Preferences prefs) {
        this.parentUI = ui;
        this.prefs = prefs;
        this.tipsUrlHandler = TipsUrlHandler.getInstance(prefs);
        this.setOnHidden(event -> onHide());
        setContent();
        setValues();
    }

    private void setValues() {
        this.showAtStartupCheckBox.setSelected(prefs.isOpenTipsAtStartup());
    }

    private void setContent() {
        container = new BorderPane();
        container.setPadding(WINDOW_PADDING);
        container.setBackground(POPUP_BACKGROUND);
        setPopupDimensions();

        Label topLabel = new Label(TOP_LABEL_TEXT);
        topLabel.setPadding(BOTTOM_PADDING);
        topLabel.setFont(TOP_LABEL_FONT);

        VBox tipSpinner = new VBox();
        previousTipLabel = new Label();
        HBox currentTipHBox = new HBox();
        Label currentTipPointer = new Label(CURRENT_TIP_POINTER_TEXT);
        currentTipPointer.setFont(SELECTED_LABEL_FONT);
        currentTipLabel = new Label();
        nextTipLabel = new Label();
        previousTipLabel.setTextFill(UNSELECTED_LABEL_COLOR);
        currentTipLabel.setFont(SELECTED_LABEL_FONT);
        nextTipLabel.setTextFill(UNSELECTED_LABEL_COLOR);
        currentTipHBox.getChildren().addAll(currentTipPointer, currentTipLabel);
        tipSpinner.getChildren().addAll(previousTipLabel, currentTipHBox, nextTipLabel);
        tipSpinner.setPadding(BOTTOM_PADDING);

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
            this.hide();
        });
        nextTipButton.setOnMouseClicked(event -> {
            goToNextTip();
        });
        nextTipLabel.setOnMouseClicked(event -> {
            goToNextTip();
        });
        previousTipButton.setOnMouseClicked(event -> {
            goToPreviousTip();
        });
        previousTipLabel.setOnMouseClicked(event -> {
            goToPreviousTip();
        });

        container.setTop(topLabel);
        container.setCenter(tipSpinner);
        container.setBottom(bottomContainer);

        DropShadow dropShadow = new DropShadow();
        dropShadow.setRadius(5.0);
        dropShadow.setOffsetX(3.0);
        dropShadow.setOffsetY(3.0);
        dropShadow.setColor(Color.GRAY);
        container.setEffect(dropShadow);

        this.getContent().add(container);
    }

    public void showTip(String tip) {
        parentUI.getBrowserComponent().showTip(tip);
        if (!prefs.isTipViewed(tip)) {
            prefs.addTipViewed(tip);
        }
    }

    private void goToNextTip() {
        tipsUrlHandler.goToNextTip();
        setLabelValues();
        showTip(tipsUrlHandler.getCurrentTipFile());
    }

    private void goToPreviousTip() {
        tipsUrlHandler.goToPreviousTip();
        setLabelValues();
        showTip(tipsUrlHandler.getCurrentTipFile());
    }

    public void onHide() {
        prefs.setOpenTipsAtStartup(showAtStartupCheckBox.isSelected());
    }

    private void setPopupDimensions () {
        container.setMaxWidth(350.0);
        container.setPrefWidth(350.0);
        container.setMaxHeight(200.0);
    }

    private void setLabelValues () {
        Optional<String> previous = tipsUrlHandler.getPreviousTipName(), next = tipsUrlHandler.getNextTipName();
        String current = tipsUrlHandler.getCurrentTipName();
        if (previous.isPresent()) {
            previousTipLabel.setText(previous.get());
        } else {
            previousTipLabel.setText("");
        }

        currentTipLabel.setText(current);

        if (next.isPresent()) {
            nextTipLabel.setText(next.get());
        } else {
            nextTipLabel.setText("");
        }
    }

    private void centerInWindow (Window owner) {
        double anchorX = owner.getX() + owner.getWidth() / 2 - container.getMaxWidth() / 2;
        double anchorY = owner.getY() + owner.getHeight() / 2 - container.getMaxHeight() / 2;
        this.setAnchorX(anchorX);
        this.setAnchorY(anchorY);
    }

    @Override
    public void show(Window owner) {
        centerInWindow(owner);
        owner.heightProperty().addListener((observable, oldValue, newValue) -> {
            centerInWindow(owner);});
        owner.widthProperty().addListener((observable, oldValue, newValue) -> {centerInWindow(owner);});
        owner.xProperty().addListener((observable, oldValue, newValue) -> {
            centerInWindow(owner);
        });
        owner.yProperty().addListener((observable, oldValue, newValue) -> {
            centerInWindow(owner);
        });

        this.setLabelValues();
        showTip(tipsUrlHandler.getCurrentTipFile());
        super.show(owner);
    }
}
