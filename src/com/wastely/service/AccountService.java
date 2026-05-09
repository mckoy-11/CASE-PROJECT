package com.wastely.service;

import com.wastely.dao.AccountDao;
import com.wastely.database.SQLConnection;
import com.wastely.model.Account;

import java.sql.Connection;
import java.util.Collections;
import java.util.List;

public class AccountService {

    public List<Account> getAllAccounts() {

        try {

            Connection connection =
                    SQLConnection.getConnection();

            AccountDao dao =
                    new AccountDao(connection);

            return dao.findAll();

        } catch (Exception exception) {

            exception.printStackTrace();

            return Collections.emptyList();
        }
    }

    public boolean activate(int accountId) {

        return updateStatus(
                accountId,
                "ACTIVE"
        );
    }

    public boolean deactivate(int accountId) {

        return updateStatus(
                accountId,
                "DEACTIVATED"
        );
    }

    public boolean updateStatus(
            int accountId,
            String status
    ) {

        try {

            Connection connection =
                SQLConnection.getConnection();

            AccountDao dao =
                    new AccountDao(connection);

            return dao.updateStatus(
                    accountId,
                    status
            );

        } catch (Exception exception) {

            exception.printStackTrace();

            return false;
        }
    }
}