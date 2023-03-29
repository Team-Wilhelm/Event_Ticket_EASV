SELECT [User].Id, [User].Name, [User].Username, [User].Password FROM [User]
    JOIN UserRole ON [User].ID = UserRole.UserID,
        (SELECT Id FROM Role WHERE RoleName LIKE 'EventCoordinator') Role
                WHERE [User].deleted=0 AND UserRole.RoleID = Role.ID;