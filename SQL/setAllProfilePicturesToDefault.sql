DECLARE @ProfilePicture VARBINARY(MAX) = (SELECT ProfilePicture FROM [User] WHERE ID = '241F5521-8381-425A-8E6F-955AAC752DB9');

UPDATE [User] SET profilePicture = @ProfilePicture;
