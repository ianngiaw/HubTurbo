package util.events;

import com.google.common.eventbus.Subscribe;

@FunctionalInterface
public interface ShowLabelPickerEventHandler extends EventHandler {
    @Subscribe
    void handle(ShowLabelPickerEvent e);
}
