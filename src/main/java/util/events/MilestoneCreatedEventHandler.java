package util.events;

import com.google.common.eventbus.Subscribe;

@FunctionalInterface
public interface MilestoneCreatedEventHandler extends EventHandler {
    @Subscribe
    void handle(MilestoneCreatedEvent e);
}
