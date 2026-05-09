package com.wastely.views.pages.menro;

import com.wastely.model.Account;
import com.wastely.models.PopupItem;
import com.wastely.service.AccountService;
import com.wastely.utils.UINotificationHelper;
import com.wastely.views.components.ScrollableTable;
import com.wastely.views.components.SearchBar;

import javax.swing.*;
import java.awt.*;
import java.sql.Timestamp;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class UsersPage extends JPanel {

    private final AccountService accountService =
            new AccountService();

    private SearchBar searchBar;
    private ScrollableTable table;

    public UsersPage() {

        setupUI();

        loadTableData(
                "",
                "All Account"
        );
    }

    private void setupUI() {

        setLayout(new BorderLayout());

        setBackground(
                new Color(240, 249, 245)
        );

        JPanel contentPanel =
                new JPanel(
                        new BorderLayout(0, 20)
                );

        contentPanel.setBackground(
                new Color(240, 249, 245)
        );

        contentPanel.setBorder(
                BorderFactory.createEmptyBorder(
                        10,
                        20,
                        30,
                        20
                )
        );

        table = new ScrollableTable(
                "ID",
                "Username",
                "Email",
                "Role",
                "Status",
                "Last Login",
                "Actions"
        ).setTitle("All Accounts");

        searchBar =
                new SearchBar(
                        "Search users by username or status."
                );

        searchBar.setFilters(
                List.of(
                        "All Account",
                        "ACTIVE",
                        "INACTIVE",
                        "DEACTIVATED"
                )
        );

        searchBar.setOnSearchChange(() -> {

            loadTableData(
                    searchBar.getText(),
                    searchBar.getSelectedFilter()
            );
        });

        contentPanel.add(
                searchBar,
                BorderLayout.NORTH
        );

        contentPanel.add(
                table,
                BorderLayout.CENTER
        );

        add(
                contentPanel,
                BorderLayout.CENTER
        );
    }

    private void loadTableData(
            String keyword,
            String statusFilter
    ) {

        try {

            table.clear();

            List<Account> accounts =
                    accountService.getAllAccounts();

            for (Account account : accounts) {

                if (
                        !matchesSearch(
                                account,
                                keyword,
                                statusFilter
                        )
                ) {

                    continue;
                }

                List<PopupItem> actions =
                        new ArrayList<>();

                actions.add(
                        new PopupItem(
                                "Activate",
                                "Enable this account",
                                () -> activateAccount(account)
                        )
                );

                actions.add(
                        new PopupItem(
                                "Deactivate",
                                "Disable this account",
                                () -> deactivateAccount(account)
                        )
                );

                table.addRowWithAction(
                        account.getAccountId(),
                        safe(account.getName()),
                        safe(account.getEmail()),
                        safe(account.getRole()),
                        safe(account.getStatus()),
                        formatTimestamp( account.getLastLogin()),
                        actions
                );
            }

            revalidate();

            repaint();

        } catch (Exception exception) {

            exception.printStackTrace();

            UINotificationHelper.showError(
                    this,
                    "Error loading accounts: "
                            + exception.getMessage()
            );
        }
    }

    private boolean matchesSearch(
            Account account,
            String keyword,
            String statusFilter
    ) {

        String search =
                keyword == null
                        ? ""
                        : keyword.trim().toLowerCase();

        boolean matchesKeyword =
                search.isEmpty()
                        || safe(account.getName())
                        .toLowerCase()
                        .contains(search)
                        || safe(account.getEmail())
                        .toLowerCase()
                        .contains(search)
                        || safe(account.getRole())
                        .toLowerCase()
                        .contains(search);

        boolean matchesStatus =
                statusFilter == null
                        || statusFilter.equalsIgnoreCase(
                                "All Account"
                        )
                        || safe(account.getStatus())
                        .equalsIgnoreCase(statusFilter);

        return matchesKeyword && matchesStatus;
    }

    private void activateAccount(Account account) {

        try {

            boolean success =
                    accountService.activate(
                            account.getAccountId()
                    );

            if (success) {

                UINotificationHelper.showSuccess(
                        this,
                        "Account activated successfully"
                );

                reload();

            } else {

                UINotificationHelper.showError(
                        this,
                        "Failed to activate account"
                );
            }

        } catch (Exception exception) {

            UINotificationHelper.showError(
                    this,
                    "Error: "
                            + exception.getMessage()
            );
        }
    }

    private void deactivateAccount(Account account) {

        int confirm =
                JOptionPane.showConfirmDialog(
                        this,
                        "Deactivate this account?",
                        "Confirm",
                        JOptionPane.YES_NO_OPTION
                );

        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        try {

            boolean success =
                    accountService.deactivate(
                            account.getAccountId()
                    );

            if (success) {

                UINotificationHelper.showSuccess(
                        this,
                        "Account deactivated successfully"
                );

                reload();

            } else {

                UINotificationHelper.showError(
                        this,
                        "Failed to deactivate account"
                );
            }

        } catch (Exception exception) {

            UINotificationHelper.showError(
                    this,
                    "Error: "
                            + exception.getMessage()
            );
        }
    }

    private void reload() {

        loadTableData(
                searchBar.getText(),
                searchBar.getSelectedFilter()
        );
    }

    private String formatTimestamp(
            Timestamp timestamp
    ) {

        if (timestamp == null) {
            return "Never";
        }

        return timestamp.toLocalDateTime()
                .format(
                        DateTimeFormatter.ofPattern(
                                "yyyy-MM-dd HH:mm"
                        )
                );
    }

    private String safe(String value) {

        return value == null
                ? ""
                : value.trim();
    }
}