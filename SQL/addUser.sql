DECLARE @UserID uniqueidentifier;

INSERT INTO [User] (name, userName, Password)
VALUES (?, ?, ?)

SET @UserID = (SELECT ID FROM [User] WHERE userName = ?)

INSERT INTO UserRole (UserID, RoleID)
VALUES (@UserID,(SELECT Id FROM Role WHERE RoleName LIKE 'EventCoordinator'));

SELECT * FROM [User] LEFT JOIN UserRole ON [User].ID = UserRole.UserID LEFT JOIN Role ON UserRole.RoleID = Role.ID;
SELECT * FROM UserRole LEFT JOIN [User] ON UserRole.UserID = [User].ID LEFT JOIN Role ON UserRole.RoleID = Role.ID;