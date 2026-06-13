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
		SELECT COUNT(*) AS cnt
		FROM Ratings
		WHERE Date >= DATEADD(DAY, -30, GETDATE()) 
		GROUP BY MovieId
	)
	SELECT @maxRatings = MAX(cnt) FROM RatingsCount30;

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
		SELECT @avgRating = AVG(Score) FROM LatestRatings;
		
		SELECT @avg = AVG(Score) FROM Ratings
		WHERE MovieId = @movieId;

		UPDATE m
		SET Status = 
		CASE
			WHEN 
			(SELECT COUNT(*) FROM Ratings WHERE MovieId = @movieId) >= 3
			AND 
			@avg >= 8
			THEN 'Classic'

			WHEN @avgRating <= @avg + 1
			THEN 'Falling'

			WHEN @avgRating >= @avg + 1
			THEN 'Rising'

			WHEN @ratingsCount30 = @maxRatings
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
	DECLARE @userId INT, @score INT, @movieId INT
	
	SELECT @userId = UserId, @movieId = MovieId, @score = Score FROM inserted

	IF (@score IN (1, 10) 
		AND 
		(SELECT COUNT(*) FROM Ratings WHERE Score IN (1, 10) AND UserId = @userId AND MovieId IN 
			(SELECT DISTINCT MovieId FROM MovieGenres WHERE GenreId IN 
				(SELECT DISTINCT GenreId FROM MovieGenres WHERE MovieId = @movieId))) > 4
		AND
		(SELECT COUNT(*) FROM Ratings WHERE Score IN (6, 7, 8) AND UserId = @userId AND MovieId IN 
			(SELECT DISTINCT MovieId FROM MovieGenres WHERE GenreId IN 
				(SELECT DISTINCT GenreId FROM MovieGenres WHERE MovieId = @movieId))) < 3
		)
		THROW 50001, 'You can not add another extreme score', 1;
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
		AVG(Score) as AverageScore
	FROM Ratings r
	JOIN MovieGenres mg ON mg.MovieId = r.MovieId
	WHERE GenreId IN (
		-- favorite genre
		SELECT GenreId FROM MovieGenres mg1
		JOIN Ratings r1 ON mg1.MovieId = r1.MovieId
		WHERE UserId = @UserId
		GROUP BY GenreId
		HAVING AVG(Score) >= 8
	)
	AND r.MovieId NOT IN (
		SELECT MovieId FROM Ratings WHERE UserId = @UserId
		UNION
		SELECT MovieId FROM WatchLists WHERE UserId = @UserId
	)
	GROUP BY mg.MovieId
	HAVING (COUNT(*) >= 4 AND AVG(Score) >= 7.5) OR (COUNT(*) < 4 AND AVG(Score) >= 9)
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
    IF ((SELECT COUNT(*) FROM Ratings WHERE UserId = @UserId) < 10) RETURN 'nedefinisan'

	IF ((SELECT COUNT(DISTINCT TagId) FROM Ratings r 
		JOIN MovieTags mt ON r.MovieId = mt.MovieId
		WHERE UserId = @UserId) >= 10) RETURN 'radoznao'

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
				HAVING AVG(Score) >= 8
			)
		)	
		AND 
		COALESCE((SELECT AVG(Score) FROM Ratings WHERE UserId <> @UserId AND MovieId = @MovieId), 0) < 6
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