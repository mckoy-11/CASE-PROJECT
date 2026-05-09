package com.wastely.service;

import java.util.Collections;
import java.util.List;
import com.wastely.dao.TeamDao;
import com.wastely.model.Team;
import com.wastely.store.DataChangeBus;
import com.wastely.store.DataTopics;

public class TeamService {

    private final TeamDao teamDao;

    public TeamService() {
        this.teamDao = new TeamDao();
    }

    public List<Team> getAllTeams() {
        List<Team> teams = teamDao.getAllTeams();
        return teams == null ? Collections.<Team>emptyList() : teams;
    }

    public Team getTeamById(int id) {
        return teamDao.getTeamById(id);
    }

    public boolean addTeam(Team team) {
        boolean success = teamDao.addTeam(team);
        publish(success);
        return success;
    }

    public boolean updateTeam(Team team) {
        boolean success = teamDao.updateTeam(team);
        publish(success);
        return success;
    }

    public boolean deleteTeam(int id) {
        boolean success = teamDao.deleteTeam(id);
        publish(success);
        return success;
    }

    public int getTotalTeamCount() {
        return teamDao.getTotalTeamCount();
    }

    public int getActiveTeamCount() {
        return teamDao.getActiveTeamCount();
    }

    private void publish(boolean success) {
        if (success) {
            DataChangeBus.publish(DataTopics.TEAMS, DataTopics.PERSONNEL, DataTopics.TRUCKS, DataTopics.SCHEDULES, DataTopics.DASHBOARD);
        }
    }
}
