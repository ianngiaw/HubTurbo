package guitests;

import javafx.scene.input.KeyCode;
import org.junit.Test;
import ui.UI;
import ui.listpanel.ListPanel;
import util.PlatformEx;
import util.events.testevents.UILogicRefreshEvent;
import util.events.testevents.UpdateDummyRepoEvent;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

import static org.junit.Assert.assertEquals;

public class UpdateIssuesTest extends UITest {

    private static final int EVENT_DELAY = 500;

    @Test
    @SuppressWarnings("unchecked")
    public void updateIssues() throws InterruptedException, ExecutionException {
        resetRepo();
        updateIssue(5, "Issue 5.1");
        // After updating, issue with ID 5 should have title Issue 5.1

        // Updated view should only contain Issue 5.1
        click("#dummy/dummy_col0_filterTextField");
        type("updated");
        press(KeyCode.SHIFT).press(KeyCode.SEMICOLON).release(KeyCode.SEMICOLON).release(KeyCode.SHIFT);
        type("24");
        push(KeyCode.ENTER);
        FutureTask countIssues = new FutureTask(((ListPanel) find("#dummy/dummy_col0"))::getIssueCount);
        PlatformEx.runAndWait(countIssues);
        assertEquals(2, countIssues.get());
    }

    public void resetRepo() {
        UI.events.triggerEvent(new UpdateDummyRepoEvent(UpdateDummyRepoEvent.UpdateType.RESET_REPO, "dummy/dummy"));
        sleep(EVENT_DELAY);
    }

    public void updateIssue(int issueId, String newIssueTitle) {
        UI.events.triggerEvent(new UpdateDummyRepoEvent(
                UpdateDummyRepoEvent.UpdateType.UPDATE_ISSUE,
                "dummy/dummy",
                issueId,
                newIssueTitle));
        UI.events.triggerEvent(new UILogicRefreshEvent());
        sleep(EVENT_DELAY);
    }
}
