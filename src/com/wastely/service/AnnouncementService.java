package com.wastely.service;

import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import com.wastely.dao.AnnouncementDao;
import com.wastely.model.Announcement;

public class AnnouncementService {

    private final AnnouncementDao dao = new AnnouncementDao();

    public Announcement getActiveAnnouncement() {
        try {
            return dao.findActive();
        } catch (SQLException e) {
            return null;
        }
    }

    public List<Announcement> getArchivedAnnouncements() {
        try {
            return dao.findArchived();
        } catch (SQLException e) {
            return Collections.emptyList();
        }
    }

    public boolean publishAnnouncement(String title, String message) {
        Announcement announcement = new Announcement();
        announcement.setTitle(title);
        announcement.setMessage(message);
        try {
            boolean success = dao.saveAndActivate(announcement);
            if (success) {
                // Data change published (placeholder for event system)
            }
            return success;
        } catch (SQLException e) {
            return false;
        }
    }

    public boolean dismissAnnouncement(int announcementId) {
        try {
            boolean success = dao.dismiss(announcementId);
            if (success) {
                // Data change published (placeholder for event system)
            }
            return success;
        } catch (SQLException e) {
            return false;
        }
    }

    public boolean addAnnouncement(Announcement announcement) {
        try {
            boolean success = dao.addAnnouncement(announcement);
            if (success) {
                // Data change published (placeholder for event system)
            }
            return success;
        } catch (SQLException e) {
            return false;
        }
    }

    public boolean updateAnnouncement(Announcement announcement) {
        try {
            boolean success = dao.updateAnnouncement(announcement);
            if (success) {
                // Data change published (placeholder for event system)
            }
            return success;
        } catch (SQLException e) {
            return false;
        }
    }
}
