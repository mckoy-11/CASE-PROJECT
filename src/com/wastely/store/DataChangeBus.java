package com.wastely.store;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import javax.swing.SwingUtilities;

/**
 * Event bus for keeping Swing panels in sync after data changes.
 * All event listeners are executed on the Event Dispatch Thread (EDT) for thread safety.
 * 
 * Listeners are guaranteed to run on the EDT and can safely update Swing components.
 * Use {@link #subscribe(String, Runnable)} to listen for data changes on a specific topic,
 * and {@link #publish(String...)} to notify all listeners of changes.
 */
public final class DataChangeBus {

    private static final Map<String, CopyOnWriteArrayList<Runnable>> LISTENERS =
            new ConcurrentHashMap<>();

    // Prevent instantiation
    private DataChangeBus() {
    }

    /**
     * Subscribe to data changes on a specific topic.
     * The provided listener will be called on the EDT whenever the topic is published.
     *
     * @param topic The topic to subscribe to (e.g., DataTopics.SCHEDULES)
     * @param listener The runnable to execute when the topic is published
     * @return A Subscription object that can be used to unsubscribe
     */
    public static Subscription subscribe(String topic, Runnable listener) {
        if (topic == null || listener == null) {
            return () -> {}; // Return a no-op subscription
        }
        LISTENERS.computeIfAbsent(topic, key -> new CopyOnWriteArrayList<>()).add(listener);
        return () -> unsubscribe(topic, listener);
    }

    /**
     * Publish data changes to all listeners on one or more topics.
     * All listeners are executed on the EDT to ensure thread safety.
     * Failed listeners do not block other listeners.
     *
     * @param topics The topics to publish (e.g., DataTopics.SCHEDULES, DataTopics.DASHBOARD)
     */
    public static void publish(String... topics) {
        if (topics == null || topics.length == 0) {
            return;
        }

        // Dispatch to EDT if not already on EDT
        if (SwingUtilities.isEventDispatchThread()) {
            publishOnEDT(topics);
        } else {
            SwingUtilities.invokeLater(() -> publishOnEDT(topics));
        }
    }

    /**
     * Internal method to publish events on the EDT.
     * This method assumes it's already being called on the EDT.
     */
    private static void publishOnEDT(String... topics) {
        for (String topic : topics) {
            List<Runnable> topicListeners = LISTENERS.get(topic);
            if (topicListeners == null || topicListeners.isEmpty()) {
                continue;
            }

            // Call all listeners for this topic
            for (Runnable listener : topicListeners) {
                try {
                    listener.run();
                } catch (RuntimeException e) {
                    // One failed listener should not block others
                    System.err.println("Error in DataChangeBus listener for topic '" + topic + "': " + e.getMessage());
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Unsubscribe from a specific topic.
     * Called automatically by the Subscription returned from {@link #subscribe(String, Runnable)}.
     *
     * @param topic The topic to unsubscribe from
     * @param listener The listener to remove
     */
    private static void unsubscribe(String topic, Runnable listener) {
        List<Runnable> topicListeners = LISTENERS.get(topic);
        if (topicListeners == null) {
            return;
        }

        topicListeners.remove(listener);
        if (topicListeners.isEmpty()) {
            LISTENERS.remove(topic);
        }
    }

    /**
     * Subscription interface returned by {@link #subscribe(String, Runnable)}.
     * Call {@link #unsubscribe()} to stop listening to data changes.
     */
    public interface Subscription {
        /**
         * Unsubscribe from the topic.
         */
        void unsubscribe();
    }
}
