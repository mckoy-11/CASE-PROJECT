/**
 * CRUD REFRESH SYSTEM IMPLEMENTATION GUIDE
 * 
 * This guide explains how to implement automatic data refresh on all pages and dialogs
 * using the DataChangeBus event-driven architecture.
 */

// ============================================================
// FOR UI PAGES / PANELS
// ============================================================

/**
 * STEP 1: Add imports to your page class
 */
import com.wastely.store.DataChangeBus;
import com.wastely.store.DataTopics;
import com.wastely.utils.UINotificationHelper;
import com.wastely.utils.UIHelper;

/**
 * STEP 2: Add subscription fields
 */
private DataChangeBus.Subscription entitySubscription;
private DataChangeBus.Subscription relatedDataSubscription;  // if needed

/**
 * STEP 3: In constructor, call subscribeToDataChanges()
 */
public SomeEntityPage() {
    this.service = new SomeEntityService();
    setupUI();
    loadTableData("", "All");
    subscribeToDataChanges();  // Add this line
}

/**
 * STEP 4: Add subscription method
 */
private void subscribeToDataChanges() {
    // Subscribe to your entity's topic
    entitySubscription = DataChangeBus.subscribe(DataTopics.ENTITY_NAME, () -> {
        SwingUtilities.invokeLater(() -> {
            if (isVisible()) {
                // Reload table with current search/filter values
                loadTableData(searchBar.getText(), searchBar.getSelectedFilter());
            }
        });
    });

    // Optionally subscribe to related topics
    // relatedDataSubscription = DataChangeBus.subscribe(DataTopics.RELATED_ENTITY, ...);
}

/**
 * STEP 5: Add unsubscribe on panel removal
 */
@Override
public void removeNotify() {
    if (entitySubscription != null) {
        entitySubscription.unsubscribe();
    }
    if (relatedDataSubscription != null) {
        relatedDataSubscription.unsubscribe();
    }
    super.removeNotify();
}

/**
 * STEP 6: Remove manual reloads from dialog callbacks
 * 
 * BEFORE:
 * private void addEntity() {
 *     dialog.setVisible(true);
 *     loadTableData(...);  // REMOVE THIS - DataChangeBus will refresh
 * }
 * 
 * AFTER:
 * private void addEntity() {
 *     dialog.setVisible(true);
 *     // Dialog will publish event when saved
 * }
 */

/**
 * STEP 7: Add error handling with try-catch
 */
private void deleteEntity(int id) {
    if (!UINotificationHelper.showDeleteConfirmation(this, "entity")) {
        return;
    }
    try {
        boolean success = service.deleteEntity(id);
        if (success) {
            UINotificationHelper.showSuccess(this, "Entity deleted successfully");
            // DataChangeBus event will trigger refresh automatically
        } else {
            UINotificationHelper.showError(this, "Failed to delete entity");
        }
    } catch (Exception e) {
        UINotificationHelper.showError(this, "Error: " + e.getMessage());
    }
}

// ============================================================
// FOR FORM DIALOGS
// ============================================================

/**
 * STEP 1: Add imports
 */
import com.wastely.utils.UINotificationHelper;

/**
 * STEP 2: Update save method to show feedback and publish events
 */
private void saveEntity() {
    try {
        String validation = validateForm();
        if (validation != null) {
            UINotificationHelper.showError(this, "Validation Error", validation);
            return;
        }

        // Build entity from form
        Entity entity = new Entity();
        // ... populate fields ...

        // Save it
        boolean success = false;
        if (isEditMode) {
            success = service.updateEntity(entity);
        } else {
            success = service.createEntity(entity);
        }

        // Show feedback
        if (success) {
            String msg = isEditMode ? "Entity updated successfully" : "Entity created successfully";
            UINotificationHelper.showSuccess(this, msg);
            
            // Close dialog after user clicks OK
            SwingUtilities.invokeLater(this::dispose);
            
            // Service will publish DataChangeBus event automatically
        } else {
            UINotificationHelper.showError(this, "Failed to save entity");
        }
    } catch (Exception e) {
        UINotificationHelper.showError(this, "Error: " + e.getMessage());
    }
}

// ============================================================
// IMPORTANT NOTES
// ============================================================

/**
 * 1. DataChangeBus events are published in Services on successful CRUD
 *    - Look at ScheduleService.publish() method for reference
 *    - All Services should follow this pattern
 * 
 * 2. EDT Safety:
 *    - DataChangeBus now dispatches all listeners to EDT automatically
 *    - Safe to call from any thread
 * 
 * 3. Subscription Cleanup:
 *    - Always unsubscribe in removeNotify()
 *    - Prevents memory leaks
 * 
 * 4. Form Dialogs:
 *    - Show success/error using UINotificationHelper
 *    - Close dialog AFTER showing message
 *    - Don't manually reload parent table - DataChangeBus will do it
 * 
 * 5. No more manual reloads:
 *    - Remove loadTableData() calls after dialog.setVisible()
 *    - Remove all manual refresh code
 *    - Everything is event-driven now
 */
