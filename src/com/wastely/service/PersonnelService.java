package com.wastely.service;

import java.util.Collections;
import java.util.List;

import com.wastely.dao.PersonnelDao;
import com.wastely.model.Personnel;
import com.wastely.store.DataChangeBus;
import com.wastely.store.DataTopics;

public class PersonnelService {

    private final PersonnelDao personnelDao;

    public PersonnelService() {
        this.personnelDao = new PersonnelDao();
    }

    // ========================= READ =========================

    public List<Personnel> getAllPersonnel() {
        return safeList(personnelDao.getAllPersonnel());
    }

    public List<Personnel> getAllUnassignedPersonnel() {
        return safeList(personnelDao.getAllUnassignedPersonnel());
    }

    public Personnel getPersonnelById(int id) {
        return personnelDao.getPersonnelById(id);
    }

    public List<Personnel> getPersonnelByRole(String roleKey) {
        return safeList(personnelDao.getPersonnelByRole(roleKey));
    }

    // ========================= WRITE =========================

    public boolean addPersonnel(Personnel personnel) {
        boolean success = personnelDao.addPersonnel(personnel);
        publish(success);
        return success;
    }

    public boolean updatePersonnel(Personnel personnel) {
        boolean success = personnelDao.updatePersonnel(personnel);
        publish(success);
        return success;
    }

    /**
     * FIXED: Role transition handler (used by TeamFormDialog)
     */
    public boolean updatePersonnelRole(int personnelId, String roleKey) {
        if (roleKey == null || roleKey.trim().isEmpty()) {
            return false;
        }

        boolean success = personnelDao.updatePersonnelRole(personnelId, roleKey.trim());
        publish(success);
        return success;
    }

    public boolean deletePersonnel(int id) {
        boolean success = personnelDao.deletePersonnel(id);
        publish(success);
        return success;
    }

    public boolean updateStatus(int id, String status) {
        boolean success = personnelDao.updateStatus(id, status);
        publish(success);
        return success;
    }

    // ========================= COUNT =========================

    public int getTotalPersonnelCount() {
        return personnelDao.getTotalPersonnelCount();
    }

    public int getActivePersonnelCount() {
        return personnelDao.getActivePersonnelCount();
    }

    public int getUnassignedPersonnelCount() {
        return personnelDao.getUnassignedPersonnelCount();
    }

    // ========================= HELPERS =========================

    private List<Personnel> safeList(List<Personnel> data) {
        return data == null ? Collections.emptyList() : data;
    }

    private void publish(boolean success) {
        if (!success) return;

        DataChangeBus.publish(
                DataTopics.PERSONNEL,
                DataTopics.TEAMS,
                DataTopics.DASHBOARD
        );
    }
}