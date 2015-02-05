package ui;

import java.util.List;
import java.util.function.Consumer;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import storage.DataManager;

public class RepositorySelector extends HBox {
	private final ComboBox<String> comboBox = new ComboBox<>();
	private final Label label = new Label();
	private Consumer<String> onValueChangeCallback = null;

	public RepositorySelector() {
		setupLayout();
		setupComboBox();
		getChildren().addAll(comboBox);
		getChildren().addAll(label);
	}

	private void setupLayout() {
		setSpacing(5);
		setPadding(new Insets(5));
		setAlignment(Pos.BASELINE_LEFT);
		comboBox.setPrefWidth(250);
	}

	private void setupComboBox() {
		comboBox.setFocusTraversable(false);
		comboBox.setEditable(true);
		loadComboBoxContents();
		comboBox.valueProperty().addListener((observable, old, newVal) -> {
			if (onValueChangeCallback != null) {
				onValueChangeCallback.accept(newVal);
			}
		});
	}
	
	public void setLabelText(String text) {
		label.setText(text);
	}

	public void setValue(String val) {
		comboBox.setValue(val);
	}

	public void setOnValueChange(Consumer<String> callback) {
		onValueChangeCallback = callback;
	}

	private void loadComboBoxContents() {
		List<String> items = DataManager.getInstance().getLastViewedRepositories();
		comboBox.getItems().addAll(items);
	}

	public void refreshComboBoxContents() {
		comboBox.getItems().clear();
		loadComboBoxContents();
	}
}
