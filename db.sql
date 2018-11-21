PRAGMA foreign_keys = ON;

BEGIN TRANSACTION;

DROP TABLE IF EXISTS Contacts;
DROP TABLE IF EXISTS Customers;
DROP TABLE IF EXISTS Payments;
DROP TABLE IF EXISTS CarModels;
DROP TABLE IF EXISTS Cars;
DROP TABLE IF EXISTS TariffHistory;
DROP TABLE IF EXISTS Rents;
DROP TABLE IF EXISTS Parks;
DROP TABLE IF EXISTS ParkingHistory;
DROP TABLE IF EXISTS ChargingStations;
DROP TABLE IF EXISTS ChargingHistory;
DROP TABLE IF EXISTS Workshops;
DROP TABLE IF EXISTS Providers;
DROP TABLE IF EXISTS PartTypes;
DROP TABLE IF EXISTS Parts;
DROP TABLE IF EXISTS CurrentPartTypeCost;
DROP TABLE IF EXISTS CurrentPartAmount;
DROP TABLE IF EXISTS Repairs;
DROP TABLE IF EXISTS PartsUsed;

COMMIT;


BEGIN TRANSACTION;

CREATE TABLE Contacts
(
  ContactID INTEGER primary key autoincrement,
  Email     TEXT    not null unique,
  Phone     NUMERIC not null unique,
  Country   TEXT    not null,
  City      TEXT    not null,
  Street    TEXT    not null,
  ZIP_code  TEXT    not null
);

CREATE TABLE Customers
(
  UserID     INTEGER primary key autoincrement,
  Username   TEXT    not null unique,
  Password   TEXT    not null,
  First_name TEXT    not null,
  Last_name  TEXT    not null,
  ContactID  INTEGER not null references Contacts
);

CREATE TABLE Payments
(
  UserID   INTEGER  not null references Customers,
  Sum      REAL     not null,
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
  Reg_number TEXT    not null unique
);

CREATE TABLE TariffHistory
(
  CostKM     REAL not null,
  Time_start TIME not null,
  Time_end   TIME not null,
  Date_out   DATE
);

CREATE TABLE Rents
(
  CarID          INTEGER  not null references Cars,
  UserID         INTEGER  not null references Customers,
  DateTime_start DATETIME not null,
  GPSloc_start   TEXT     not null,
  DateTime_end   DATETIME not null,
  GPSloc_end     TEXT     not null
--   Paid           REAL     not null
);

CREATE TABLE Parks
(
  ParkID  INTEGER primary key autoincrement,
  NPlaces INTEGER not null,
  GPSloc  TEXT    not null
);

CREATE TABLE ParkingHistory
(
  ParkID          INTEGER  not null references Parks,
  CarID           INTEGER  not null references Cars,
  DateTime_arrive DATETIME not null,
  DateTime_leave  DATETIME
);

CREATE TABLE ChargingStations
(
  UID      INTEGER primary key autoincrement,
  NSockets INTEGER not null,
  GPSloc   TEXT    not null,
  CostHour REAL    not null,
  Shape    TEXT    not null
);

CREATE TABLE ChargingHistory
(
  UID            INTEGER  not null references ChargingStations,
  CarID          INTEGER  not null references Cars,
  DateTime_start DATETIME not null,
  DateTime_end   DATETIME,
  Paid           REAL
);

CREATE TABLE Workshops
(
  WID     INTEGER primary key autoincrement,
  NPlaces INTEGER not null,
  GPSloc  TEXT    not null
);

CREATE TABLE Providers
(
  ProviderID INTEGER primary key autoincrement,
  Name       TEXT    not null unique,
  ContactID  INTEGER not null references Contacts
);

CREATE TABLE PartTypes
(
  PartTypeID INTEGER primary key autoincrement,
  Name       TEXT    not null,
  ModelID    INTEGER not null references CarModels
);

CREATE TABLE Parts
(
  PartID     INTEGER primary key autoincrement,
  PartTypeID INTEGER not null references PartTypes,
  WID        INTEGER not null references Workshops,
  ProviderID INTEGER not null references Providers,
  Paid       REAL    not null
);

CREATE TABLE CurrentPartTypeCost
(
  PartTypeID INTEGER not null references PartTypes,
  ProviderID INTEGER not null references Providers,
  Cost       REAL    not null
);

CREATE TABLE CurrentPartAmount
(
  PartTypeID INTEGER not null references PartTypes,
  WID        INTEGER not null references Workshops,
  Amount     INTEGER not null
);

CREATE TABLE Repairs
(
  RepairID       INTEGER  primary key autoincrement,
  WID            INTEGER  not null references Workshops,
  CarID          INTEGER  not null references Cars,
  DateTime_start DATETIME not null,
  DateTime_end   DATETIME
);

CREATE TABLE PartsUsed
(
  RepairID INTEGER not null references Repairs,
  PartID   INTEGER not null references Parts
);

COMMIT;


BEGIN TRANSACTION;
  INSERT INTO CarModels (Manufacturer, Name, Color) VALUES ('Volkswagen', 'Golf', 'Blue');
  INSERT INTO CarModels (Manufacturer, Name, Color) VALUES ('Mercedes', 'Benz', 'White');
  INSERT INTO CarModels (Manufacturer, Name, Color) VALUES ('Ford', 'Galaxy', 'Black');

  INSERT INTO Cars (ModelID, Reg_number) VALUES (1, 'AN102030');
  INSERT INTO Cars (ModelID, Reg_number) VALUES (2, 'AN122131');
  INSERT INTO Cars (ModelID, Reg_number) VALUES (2, 'BN000000');
  INSERT INTO Cars (ModelID, Reg_number) VALUES (3, 'EU201809');
  INSERT INTO Cars (ModelID, Reg_number) VALUES (1, 'RU202020');

  INSERT INTO ChargingStations (NSockets, GPSloc, CostHour, Shape) VALUES (10, '50, 40', 10.5, 'Basic');
  INSERT INTO ChargingStations (NSockets, GPSloc, CostHour, Shape) VALUES (15, '40, 50', 10.5, 'Basic');
  INSERT INTO ChargingStations (NSockets, GPSloc, CostHour, Shape) VALUES (10, '60, 60', 10.5, 'Basic');

  INSERT INTO ChargingHistory (UID, CarID, DateTime_start, DateTime_end, Paid)
    VALUES (1, 1, '2017-12-01 12:00', '2017-12-01 13:00', 100.5);
  INSERT INTO ChargingHistory (UID, CarID, DateTime_start, DateTime_end, Paid)
    VALUES (1, 2, '2017-12-01 20:00', '2017-12-01 21:00', 100.5);
  INSERT INTO ChargingHistory (UID, CarID, DateTime_start, DateTime_end, Paid)
    VALUES (2, 1, '2017-12-01 20:00', '2017-12-01 20:45', 50.0);
  INSERT INTO ChargingHistory (UID, CarID, DateTime_start, DateTime_end, Paid)
    VALUES (2, 2, '2017-12-02 09:00', '2017-12-02 09:45', 50.0);
  INSERT INTO ChargingHistory (UID, CarID, DateTime_start, DateTime_end, Paid)
    VALUES (3, 3, '2018-11-02 09:00', '2018-12-02 09:30', 1000.0);
  INSERT INTO ChargingHistory (UID, CarID, DateTime_start, DateTime_end, Paid)
    VALUES (3, 2, '2018-11-04 09:00', NULL, NULL);
COMMIT;

VACUUM;
