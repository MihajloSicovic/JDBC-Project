CREATE DATABASE MovieRatings;
GO

USE MovieRatings;
GO

CREATE TABLE Users
(
    IdU INT IDENTITY(1,1) PRIMARY KEY,
    Username NVARCHAR(100) NOT NULL UNIQUE,
    Rewards INT NOT NULL DEFAULT 0
);
GO

CREATE TABLE Genres
(
    IdG INT IDENTITY(1,1) PRIMARY KEY,
    Name NVARCHAR(100) NOT NULL UNIQUE
);
GO

CREATE TABLE Tags
(
    IdT INT IDENTITY(1,1) PRIMARY KEY,
    Name NVARCHAR(100) NOT NULL UNIQUE
);
GO

CREATE TABLE Movies
(
    IdM INT IDENTITY(1,1) PRIMARY KEY,
    Title NVARCHAR(100) NOT NULL,
    Director NVARCHAR(100) NOT NULL,

    Status NVARCHAR(10)
        CONSTRAINT CK_Movie_TrendStatus
        CHECK (Status IN ('Trending', 'Rising', 'Falling', 'Classic'))
);
GO

CREATE TABLE MovieGenres
(
    MovieId INT NOT NULL,
    GenreId INT NOT NULL,

    CONSTRAINT PK_MovieGenre
        PRIMARY KEY (MovieId, GenreId),

    CONSTRAINT FK_MovieGenre_Movie
        FOREIGN KEY (MovieId)
        REFERENCES Movies(IdM)
        ON UPDATE CASCADE
        ON DELETE CASCADE,

    CONSTRAINT FK_MovieGenre_Genre
        FOREIGN KEY (GenreId)
        REFERENCES Genres(IdG)
        ON UPDATE CASCADE
        ON DELETE NO ACTION
);
GO

CREATE TABLE MovieTags
(
    MovieId INT NOT NULL,
    TagId INT NOT NULL,

    CONSTRAINT PK_MovieTag
        PRIMARY KEY (MovieId, TagId),

    CONSTRAINT FK_MovieTag_Movie
        FOREIGN KEY (MovieId)
        REFERENCES Movies(IdM)
        ON UPDATE CASCADE
        ON DELETE CASCADE,

    CONSTRAINT FK_MovieTag_Tag
        FOREIGN KEY (TagId)
        REFERENCES Tags(IdT)
        ON UPDATE CASCADE
        ON DELETE NO ACTION
);
GO

CREATE TABLE WatchLists
(
    UserId INT NOT NULL,
    MovieId INT NOT NULL,

    CONSTRAINT PK_WatchList
        PRIMARY KEY (UserId, MovieId),

    CONSTRAINT FK_WatchList_User
        FOREIGN KEY (UserId)
        REFERENCES Users(IdU)
        ON UPDATE CASCADE
        ON DELETE CASCADE,

    CONSTRAINT FK_WatchList_Movie
        FOREIGN KEY (MovieId)
        REFERENCES Movies(IdM)
        ON UPDATE CASCADE
        ON DELETE NO ACTION
);
GO

CREATE TABLE Ratings
(
    IdR INT IDENTITY(1,1) PRIMARY KEY,

    UserId INT NOT NULL,
    MovieId INT NOT NULL,

    Score INT NOT NULL,

    Date DATETIME NOT NULL DEFAULT GETDATE(),

    CONSTRAINT CK_Rating_Value
        CHECK (Score BETWEEN 1 AND 10),

    CONSTRAINT UQ_Rating_User_Movie
        UNIQUE (UserId, MovieId),

    CONSTRAINT FK_Rating_User
        FOREIGN KEY (UserId)
        REFERENCES Users(IdU)
        ON UPDATE CASCADE
        ON DELETE NO ACTION,

    CONSTRAINT FK_Rating_Movie
        FOREIGN KEY (MovieId)
        REFERENCES Movies(IdM)
        ON UPDATE CASCADE
        ON DELETE NO ACTION
);
GO

CREATE OR ALTER TRIGGER TR_UPDATE_MOVIE_TREND
ON Ratings
AFTER INSERT, UPDATE, DELETE
AS
BEGIN
	DECLARE @movieId INT, @ratingsCount30 INT, @maxRatings INT
	DECLARE @avgRating DECIMAL(10, 3), @avg DECIMAL(10, 3)

	DECLARE myCursor CURSOR FOR
	SELECT DISTINCT movieId FROM inserted
	UNION
	SELECT DISTINCT movieId FROM deleted

	OPEN myCursor

	FETCH NEXT FROM myCursor
	INTO @movieId;

	WITH RatingsCount30 AS (
		SELECT TOP 10 PERCENT COUNT(*) AS cnt
		FROM Ratings
		WHERE Date >= DATEADD(DAY, -30, GETDATE())
		GROUP BY MovieId
		ORDER BY COUNT(*) DESC
	)
	SELECT @maxRatings = MIN(cnt) FROM RatingsCount30;

	WHILE @@FETCH_STATUS = 0
	BEGIN
		SELECT @ratingsCount30 = COUNT(*)
		FROM Ratings
		WHERE Date >= DATEADD(DAY,-30,GETDATE()) 
		AND MovieId = @movieId;

		WITH LatestRatings AS (
			SELECT TOP 5 Score 
			FROM Ratings 
			WHERE MovieId = @movieId
			ORDER BY Date DESC
		)
		SELECT @avgRating = AVG(Score * 1.0) FROM LatestRatings;
		
		SELECT @avg = AVG(Score * 1.0) FROM Ratings
		WHERE MovieId = @movieId;

		UPDATE m
		SET Status = 
		CASE

			WHEN @avgRating >= @avg + 1
			THEN 'Rising'

			WHEN @avgRating <= @avg - 1
			THEN 'Falling'

			WHEN 
			(SELECT COUNT(*) FROM Ratings WHERE MovieId = @movieId) >= 3
			AND 
			@avg >= 8
			THEN 'Classic'

			WHEN @ratingsCount30 >= @maxRatings
			THEN 'Trending'

			ELSE NULL

		END
		FROM Movies m
		WHERE IdM = @movieId

		FETCH NEXT FROM myCursor
		INTO @movieId
	END

	CLOSE myCursor
	DEALLOCATE myCursor
END;
GO

CREATE OR ALTER TRIGGER TR_BLOCK_EXTREME
ON Ratings
AFTER INSERT, UPDATE
AS
BEGIN
	IF EXISTS (
        SELECT 1
        FROM inserted i
        WHERE i.Score IN (1, 10)
          AND (
                SELECT COUNT(*)
                FROM Ratings r
                WHERE r.Score IN (1, 10)
                  AND r.UserId = i.UserId
                  AND r.MovieId IN (
                        SELECT DISTINCT mg2.MovieId
                        FROM MovieGenres mg2
                        WHERE mg2.GenreId IN (
                              SELECT DISTINCT mg1.GenreId
                              FROM MovieGenres mg1
                              WHERE mg1.MovieId = i.MovieId
                        )
                  )
              ) > 4
          AND (
                SELECT COUNT(*)
                FROM Ratings r2
                WHERE r2.Score IN (6, 7, 8)
                  AND r2.UserId = i.UserId
                  AND r2.MovieId IN (
                        SELECT DISTINCT mg4.MovieId
                        FROM MovieGenres mg4
                        WHERE mg4.GenreId IN (
                              SELECT DISTINCT mg3.GenreId
                              FROM MovieGenres mg3
                              WHERE mg3.MovieId = i.MovieId
                        )
                  )
              ) < 3
    )
    BEGIN
        ROLLBACK TRANSACTION;
        THROW 50001, 'You can not add another extreme score', 1;
    END
END;
GO

CREATE OR ALTER FUNCTION FN_MovieRecommendations
(
    @UserId INT
)
RETURNS TABLE
AS
RETURN
(
    SELECT mg.MovieId as MovieId, 
		AVG(Score * 1.0) as AverageScore
	FROM Ratings r
	JOIN MovieGenres mg ON mg.MovieId = r.MovieId
	WHERE GenreId IN (
		-- favorite genre
		SELECT GenreId FROM MovieGenres mg1
		JOIN Ratings r1 ON mg1.MovieId = r1.MovieId
		WHERE UserId = @UserId
		GROUP BY GenreId
		HAVING AVG(Score * 1.0) >= 8
	)
	AND r.MovieId NOT IN (
		SELECT MovieId FROM Ratings WHERE UserId = @UserId
		UNION
		SELECT MovieId FROM WatchLists WHERE UserId = @UserId
	)
	GROUP BY mg.MovieId
	HAVING (COUNT(*) >= 4 AND AVG(Score * 1.0) >= 7.5) OR (COUNT(*) < 4 AND AVG(Score * 1.0) >= 9)
);
GO

CREATE OR ALTER FUNCTION FN_UserSpecializations
(
    @UserId INT
)
RETURNS TABLE
AS
RETURN
(
    SELECT t.Name FROM Ratings r
	JOIN MovieTags mt ON r.MovieId = mt.MovieId
	JOIN Tags t ON t.IdT = mt.TagId
	WHERE UserId = @UserId AND Score >= 8
	GROUP BY t.Name
	HAVING COUNT(*) >= 2
);
GO

CREATE OR ALTER FUNCTION FN_UserDescription
(
    @UserId INT
)
RETURNS NVARCHAR(20)
AS
BEGIN
    IF ((SELECT COUNT(*) FROM Ratings WHERE UserId = @UserId) < 10) RETURN 'undefined'

	IF ((SELECT COUNT(DISTINCT TagId) FROM Ratings r 
		JOIN MovieTags mt ON r.MovieId = mt.MovieId
		WHERE UserId = @UserId) >= 10) RETURN 'curious'

    RETURN 'focused';
END;
GO

CREATE OR ALTER PROCEDURE SP_REWARD_USER
(
    @UserId INT,
    @MovieId INT
)
AS
BEGIN
    IF ((SELECT COUNT(*) FROM Ratings WHERE UserId = @UserId) < 10) RETURN

	IF (EXISTS
		(
			SELECT * FROM MovieGenres
			WHERE MovieId = @MovieId AND GenreId IN (
				-- favorite genre
				SELECT GenreId FROM MovieGenres mg1
				JOIN Ratings r1 ON mg1.MovieId = r1.MovieId
				WHERE UserId = @UserId
				GROUP BY GenreId
				HAVING AVG(Score * 1.0) >= 8
			)
		)	
		AND 
		COALESCE((SELECT AVG(Score * 1.0) FROM Ratings WHERE UserId <> @UserId AND MovieId = @MovieId), 0) < 6
	)
	UPDATE Users SET Rewards += 1 WHERE IdU = @UserId
END;
GO

CREATE OR ALTER TRIGGER TR_REWARD_USER
ON Ratings
AFTER INSERT, UPDATE
AS
BEGIN
	DECLARE @movieId INT, @userId INT

	DECLARE myCursor CURSOR FOR
	SELECT MovieId, UserId FROM inserted

	OPEN myCursor

	FETCH NEXT FROM myCursor
	INTO @movieId, @userId

	WHILE @@FETCH_STATUS = 0
	BEGIN
		EXEC SP_REWARD_USER @userId, @movieId

		FETCH NEXT FROM myCursor
		INTO @movieId, @userId
	END

	CLOSE myCursor
	DEALLOCATE myCursor
END;
GO
GO