package com.wastely.views.base;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;
import com.wastely.store.DataChangeBus;

/**
 * Base class for all UI panels with automatic data change subscription management.
 * Handles subscribing to data changes and safely unsubscribing when the panel is destroyed.
 * Subclasses should override {@link #onDataChanged()} to implement refresh logic.
 */
public abstract class BaseUIPanel extends JPanel {

    /**
     * List of active subscriptions for cleanup on panel disposal.
     */
    private final List<DataChangeBus.Subscription> subscriptions = new ArrayList<>();

    /**
     * Initialize the panel and set up data change listeners.
     * Called by subclasses to subscribe to relevant data topics.
     */
    protected BaseUIPanel() {
        super();
        // Subclasses will call subscribeToDataChanges() in their constructors
    }

    /**
     * Subscribe to a single data change topic.
     * The subscription is automatically tracked for cleanup.
     *
     * @param topic The data topic to subscribe to (e.g., DataTopics.SCHEDULES)
     * @return true if subscription was successful
     */
    protected boolean subscribe(String topic) {
        if (topic == null) {
            return false;
        }
        try {
            DataChangeBus.Subscription subscription = DataChangeBus.subscribe(
                    topic,
                    this::onDataChanged
            );
            subscriptions.add(subscription);
            return true;
        } catch (Exception e) {
            System.err.println("Failed to subscribe to topic: " + topic);
            return false;
        }
    }

    /**
     * Subscribe to multiple data change topics.
     * All subscriptions are automatically tracked for cleanup.
     *
     * @param topics The data topics to subscribe to
     */
    protected void subscribe(String... topics) {
        if (topics == null) {
            return;
        }
        for (String topic : topics) {
            subscribe(topic);
        }
    }

    /**
     * Called when any of the subscribed data topics change.
     * Override this method in subclasses to implement refresh logic.
     * This method is automatically called on the EDT.
     */
    protected abstract void onDataChanged();

    /**
     * Unsubscribe from all data change topics.
     * This is automatically called when the panel is removed or destroyed.
     * You can also call this manually if needed.
     */
    public void unsubscribeAll() {
        for (DataChangeBus.Subscription subscription : subscriptions) {
            try {
                subscription.unsubscribe();
            } catch (Exception e) {
                System.err.println("Error unsubscribing from data changes: " + e.getMessage());
            }
        }
        subscriptions.clear();
    }

    /**
     * Override addNotify to ensure subscriptions are set up when panel is added.
     */
    @Override
    public void addNotify() {
        super.addNotify();
    }

    /**
     * Override removeNotify to clean up subscriptions when panel is removed.
     */
    @Override
    public void removeNotify() {
        unsubscribeAll();
        super.removeNotify();
    }
}
