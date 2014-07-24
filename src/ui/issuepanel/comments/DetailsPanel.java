package ui.issuepanel.comments;

import java.lang.ref.WeakReference;

import handler.IssueDetailsContentHandler;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.beans.value.WeakChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.VBox;
import javafx.util.Callback;
import model.TurboComment;
import model.TurboIssue;
import ui.issuepanel.comments.IssueDetailsDisplay.DisplayType;

public class DetailsPanel extends VBox {
	protected static final int LIST_MAX_HEIGHT = 1000;
	protected static final int COMMENTS_BOX_PREF_HEIGHT = 200;
	public static final int COMMENTS_CELL_HEIGHT = 200;
	public static final int COMMENTS_CELL_WIDTH = 330;
	public static final int COMMENTS_PADDING = 5;
	protected static final int DEFAULT_HEIGHT = 150;
	
	private ListView<TurboComment> listView;
	private IssueDetailsContentHandler handler;
	private TurboIssue issue;
	private DisplayType displayType;
			
	private ObservableList<TurboComment> detailsList;
	private ChangeListener<Boolean> expandedChangeListener;
	
	public DetailsPanel(TurboIssue issue, IssueDetailsContentHandler handler, DisplayType displayType){
		this.issue = issue;
		this.listView = new ListView<TurboComment>();
		this.handler = handler;
		this.displayType = displayType;
		if(displayType == DisplayType.COMMENTS){
			detailsList = handler.getComments();
			
		}else{
			detailsList = handler.getIssueHistory();
		}
		setupLayout();
		loadItems();
	}
	
	private void setupLayout(){
		this.setPadding(new Insets(COMMENTS_PADDING));
		this.setSpacing(COMMENTS_PADDING);
	}
	
	protected void scrollToBottom(){
		if(!listView.getItems().isEmpty()){
			listView.scrollTo(detailsList.size() - 1);
		}
	}
	
	private Callback<ListView<TurboComment>, ListCell<TurboComment>> commentCellFactory(){
		Callback<ListView<TurboComment>, ListCell<TurboComment>> factory = new Callback<ListView<TurboComment>, ListCell<TurboComment>>() {
			@Override
			public ListCell<TurboComment> call(ListView<TurboComment> list) {
				return new DetailsCell(issue, displayType, handler);
			}
		};
		return factory;
	}
	
	private void loadItems() {
		if(displayType == DisplayType.COMMENTS){
			loadNewCommentsBox();
		}
		setListItems();
		getChildren().add(0, listView);
	}

	
	private void loadNewCommentsBox(){
		CommentsEditBox box = new CommentsEditBox(handler);
		box.setPrefHeight(COMMENTS_CELL_HEIGHT);
		box.setPrefWidth(COMMENTS_CELL_WIDTH);
		
		TitledPane commentsContainer = createCommentsContainer(box);
		getChildren().add(commentsContainer);
	}
	
	private TitledPane createCommentsContainer(CommentsEditBox box){
		TitledPane commentsContainer = new TitledPane("Add Comment", box);
		commentsContainer.setExpanded(false);
		commentsContainer.setAnimated(false);
		
		WeakReference<TitledPane> paneRef = new WeakReference<>(commentsContainer);
		expandedChangeListener = new ChangeListener<Boolean>(){
			@Override
			public void changed(ObservableValue<? extends Boolean> arg0,
					Boolean original, Boolean newVal) {
				TitledPane pane = paneRef.get();
				if(pane == null){
					return;
				}
				if(newVal == true){
					pane.setMinHeight(COMMENTS_BOX_PREF_HEIGHT);
					Platform.runLater(() -> box.requestFocus());
				}else{
					pane.setMinHeight(USE_COMPUTED_SIZE);
				}
			}
		};
		
		commentsContainer.expandedProperty().addListener(new WeakChangeListener<Boolean>(expandedChangeListener));
		return commentsContainer;
	}
	
	private void setListItems(){
		listView.setPrefWidth(COMMENTS_CELL_WIDTH);
		listView.setPrefHeight(LIST_MAX_HEIGHT);
		listView.setCellFactory(commentCellFactory());
		listView.setItems(detailsList);
	}
}