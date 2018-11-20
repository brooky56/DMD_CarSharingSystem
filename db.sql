PRAGMA foreign_keys = ON;
BEGIN TRANSACTION;

CREATE TABLE Customers
(
  UserID     INTEGER primary key autoincrement,
  Username   TEXT    not null unique,
  Password   TEXT    not null,
  First_name TEXT    not null,
  Last_name  TEXT    not null,
  Email      TEXT    not null unique,
  Country    TEXT    not null,
  City       TEXT    not null,
  ZIP_code   TEXT    not null
);

CREATE TABLE Payments
(
  UserID   INTEGER  not null references Customers,
  Amount   REAL     not null,
  DateTime DATETIME not null
);

CREATE TABLE CarModels
(
  ModelID      INTEGER primary key autoincrement,
  Manufacturer TEXT    not null,
  Name         TEXT    not null unique,
  Color        TEXT    not null
);

CREATE TABLE Cars
(
  CarID      INTEGER primary key autoincrement,
  ModelID    INTEGER not null references CarModels,
  Reg_number TEXT    not null
);

CREATE TABLE Rents
(
  CarID          INTEGER  not null references Cars,
  UserID         INTEGER  not null references Customers,
  DateTime_start DATETIME not null,
  GPSloc_start   TEXT     not null,
  DateTime_end   DATETIME not null,
  GPSloc_end     TEXT     not null,
  Cost           REAL     not null
);
COMMIT;
VACUUM;

