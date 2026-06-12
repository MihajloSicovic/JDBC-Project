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