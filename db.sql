PRAGMA foreign_keys = ON;

DROP TABLE IF EXISTS Contacts;
DROP TABLE IF EXISTS Customers;
DROP TABLE IF EXISTS Payments;
DROP TABLE IF EXISTS CarModels;
DROP TABLE IF EXISTS Cars;
DROP TABLE IF EXISTS AvailableCars;
DROP TABLE IF EXISTS Rents;
DROP TABLE IF EXISTS Parks;
DROP TABLE IF EXISTS ParkingHistory;
DROP TABLE IF EXISTS ChargingStations;
DROP TABLE IF EXISTS ChargingHistory;
DROP TABLE IF EXISTS Workshops;
DROP TABLE IF EXISTS Providers;
DROP TABLE IF EXISTS PartTypes;
DROP TABLE IF EXISTS Parts;
DROP TABLE IF EXISTS CurrentAmountOfParts;
DROP TABLE IF EXISTS CurrentCostOfPartTypes;
DROP TABLE IF EXISTS Repairs;
DROP TABLE IF EXISTS PartsUsed;

VACUUM;

CREATE TABLE Contacts
(
  ContactID INTEGER not null primary key autoincrement,
  Email     TEXT    not null unique check(Email LIKE '_%@_%._%'),
  Phone     NUMERIC not null unique check(LENGTH(Phone) > 0),
  Country   TEXT    not null check(LENGTH(Country) > 0),
  City      TEXT    not null check(LENGTH(City) > 0),
  Street    TEXT    not null check(LENGTH(Street) > 0),
  ZIP_code  NUMERIC not null check(LENGTH(ZIP_code) > 0)
);

CREATE TABLE Customers
(
  UserID     INTEGER not null primary key autoincrement,
  Username   TEXT    not null unique check(LENGTH(Username) >= 4),
  Password   TEXT    not null check(LENGTH(Password) >= 6),
  First_name TEXT    not null check(LENGTH(First_name) > 0),
  Last_name  TEXT    not null check(LENGTH(Last_name) > 0),
  ContactID  INTEGER not null unique references Contacts
);

CREATE TABLE Payments
(
  UserID   INTEGER  not null references Customers,
  Paid      REAL     not null check(Paid > 0.0),
  DateTime DATETIME not null check(DateTime LIKE '____-__-__ __:__:__.%')
);

CREATE TABLE CarModels
(
  ModelID       INTEGER not null primary key autoincrement,
  VIN           TEXT    not null unique check(LENGTH(VIN) = 17),
  Brand         TEXT    not null check(LENGTH(Brand) > 0),
  Name          TEXT    not null check(LENGTH(Name) > 0),
  Color         TEXT    not null check(LENGTH(Color) > 0),
  SocketShape   TEXT    not null check(LENGTH(SocketShape) > 0)
);

CREATE TABLE Cars
(
  CarID      INTEGER not null primary key autoincrement,
  ModelID    INTEGER not null references CarModels,
  Reg_number TEXT    not null unique check(LENGTH(Reg_number) > 0)
);

CREATE TABLE AvailableCars
(
  CarID  INTEGER not null unique references Cars,
  Charge REAL    not null check(Charge >= 0.0 AND Charge <= 100.0),
  Health TEXT    not null check(LENGTH(Health) > 0),
  GPSloc TEXT    not null check(GPSloc LIKE '_%, _%')
);

CREATE TABLE Rents
(
  CarID          INTEGER  not null references Cars,
  UserID         INTEGER  not null references Customers,
  DateTime_start DATETIME not null check(DateTime_start LIKE '____-__-__ __:__'),
  GPSloc_start   TEXT     not null check(GPSloc_start LIKE '_%, _%'),
  DateTime_end   DATETIME not null check(DateTime_end LIKE '____-__-__ __:__'),
  GPSloc_end     TEXT     not null check(GPSloc_end LIKE '_%, _%'),
  Cost           REAL     not null check(Cost >= 0.0),
  DistanceKM     REAL     not null check(DistanceKM > 0.0)
);

CREATE TABLE Parks
(
  ParkID  INTEGER not null primary key autoincrement,
  NPlaces INTEGER not null check(NPlaces > 0),
  GPSloc  TEXT    not null check(GPSloc LIKE '_%, _%')
);

CREATE TABLE ParkingHistory
(
  ParkID          INTEGER  not null references Parks,
  CarID           INTEGER  not null references Cars,
  DateTime_arrive DATETIME not null check(DateTime_arrive LIKE '____-__-__ __:__'),
  DateTime_leave  DATETIME not null default 'now' check(DateTime_leave = 'now' OR DateTime_leave LIKE '____-__-__ __:__')
);

CREATE TABLE ChargingStations
(
  UID            INTEGER not null primary key autoincrement,
  NSockets       INTEGER not null check(NSockets > 0),
  GPSloc         TEXT    not null check(GPSloc LIKE '_%, _%'),
  CostHour       REAL    not null check(CostHour > 0.0),
  SocketShape    TEXT    not null check(LENGTH(SocketShape) > 0)
);

CREATE TABLE ChargingHistory
(
  UID            INTEGER  not null references ChargingStations,
  CarID          INTEGER  not null references Cars,
  DateTime_start DATETIME not null check(DateTime_start LIKE '____-__-__ __:__:__'),
  DateTime_end   DATETIME not null default 'now' check(DateTime_end = 'now' OR DateTime_end LIKE '____-__-__ __:__:__'),
  Cost           REAL     not null default 'now' check(Cost = 'now' OR Cost > 0.0)
);

CREATE TABLE Workshops
(
  WID     INTEGER not null primary key autoincrement,
  NPlaces INTEGER not null check(NPlaces > 0),
  GPSloc  TEXT    not null check(GPSloc LIKE '_%, _%')
);

CREATE TABLE Providers
(
  ProviderID INTEGER not null primary key autoincrement,
  Name       TEXT    not null unique check(LENGTH(Name) > 0),
  ContactID  INTEGER not null unique references Contacts
);

CREATE TABLE PartTypes
(
  PartTypeID INTEGER not null primary key autoincrement,
  Name       TEXT    not null check(LENGTH(Name) > 0),
  ModelID    INTEGER not null references CarModels,
  unique (Name, ModelID)
);

CREATE TABLE Parts
(
  PartID      INTEGER not null primary key autoincrement,
  PartTypeID  INTEGER not null references PartTypes,
  WID         INTEGER not null references Workshops,
  ProviderID  INTEGER not null references Providers,
  Paid        REAL    not null check(Paid > 0.0),
  Date_bought DATE    not null check(Date_bought LIKE '____-__-__')
);

CREATE TABLE CurrentCostOfPartTypes
(
  PartTypeID INTEGER not null references PartTypes,
  ProviderID INTEGER not null references Providers,
  Cost       REAL    not null check(Cost > 0.0),
  unique (PartTypeID, ProviderID)
);

CREATE TABLE CurrentAmountOfParts
(
  PartTypeID INTEGER not null references PartTypes,
  WID        INTEGER not null references Workshops,
  Amount     INTEGER not null check(Amount >= 0),
  unique (PartTypeID, WID)
);

CREATE TABLE Repairs
(
  RepairID   INTEGER  not null primary key autoincrement,
  WID        INTEGER  not null references Workshops,
  CarID      INTEGER  not null references Cars,
  Date_start DATE     not null check(Date_start LIKE '____-__-__'),
  Date_end   DATE     not null default 'now' check(Date_end = 'now' OR Date_end LIKE '____-__-__')
);

CREATE TABLE PartsUsed
(
  RepairID INTEGER not null references Repairs,
  PartID   INTEGER not null references Parts
);


INSERT INTO Contacts (Email, Phone, Country, City, Street, ZIP_code)
  VALUES ('some1@gmail.com', '44-44-44', 'Germany', 'Berlin', 'Main', '000000');
INSERT INTO Contacts (Email, Phone, Country, City, Street, ZIP_code)
  VALUES ('some2@gmail.com', '44-44-45', 'Germany', 'Berlin', 'Main', '000000');
INSERT INTO Contacts (Email, Phone, Country, City, Street, ZIP_code)
  VALUES ('some3@gmail.com', '44-44-46', 'Germany', 'Berlin', 'Main', '000000');
INSERT INTO Contacts (Email, Phone, Country, City, Street, ZIP_code)
  VALUES ('some4@gmail.com', '44-44-47', 'Germany', 'Berlin', 'Main', '000000');

INSERT INTO Customers (Username, Password, First_name, Last_name, ContactID)
  VALUES ('Vladic30', '1234567', 'Vlad', 'Ivanov', 1);
INSERT INTO Customers (Username, Password, First_name, Last_name, ContactID)
  VALUES ('CrazyIvan', 'BpbLeaaehs', 'Ivan', 'Konyukhov', 2);
INSERT INTO Customers (Username, Password, First_name, Last_name, ContactID)
  VALUES ('EiffelTOP', 'Vtqth<thn', 'Bertran', 'Meyer', 3);
INSERT INTO Customers (Username, Password, First_name, Last_name, ContactID)
  VALUES ('DROPPER', 'CexxbUexxb', 'Giancarlo', 'Succi', 4);



INSERT INTO CarModels (VIN, Brand, Name, Color) VALUES ('11111111111111111', 'Volkswagen', 'Golf', 'Blue');
INSERT INTO CarModels (VIN, Brand, Name, Color) VALUES ('11111111111111112', 'Mercedes', 'Benz', 'White');
INSERT INTO CarModels (VIN, Brand, Name, Color) VALUES ('11111111111111113', 'Ford', 'Galaxy', 'Black');

INSERT INTO Cars (ModelID, Reg_number) VALUES (1, 'AN102030');
INSERT INTO Cars (ModelID, Reg_number) VALUES (2, 'AN122131');
INSERT INTO Cars (ModelID, Reg_number) VALUES (2, 'BN000000');
INSERT INTO Cars (ModelID, Reg_number) VALUES (3, 'EU201809');
INSERT INTO Cars (ModelID, Reg_number) VALUES (1, 'RU202020');



INSERT INTO Rents VALUES (1, 1, '2017-01-01 07:00', '0, 0', '2017-01-01 08:00', '10, 10', 1000, 5);
INSERT INTO Rents VALUES (3, 2, '2017-01-01 07:00', '0, 0', '2017-01-01 08:00', '10, 10', 1000, 5);
INSERT INTO Rents VALUES (1, 3, '2017-01-01 08:00', '10, 10', '2017-01-01 09:00', '0, 0', 1000, 5);
INSERT INTO Rents VALUES (3, 4, '2017-01-01 08:00', '10, 10', '2017-01-01 09:00', '0, 0', 1000, 5);
INSERT INTO Rents VALUES (2, 1, '2017-01-01 12:00', '0, 0', '2017-01-01 13:00', '10, 10', 1000, 5);
INSERT INTO Rents VALUES (1, 2, '2017-01-01 12:00', '0, 0', '2017-01-01 13:00', '10, 10', 1000, 5);
INSERT INTO Rents VALUES (3, 1, '2017-01-01 17:00', '0, 0', '2017-01-01 18:00', '10, 10', 1000, 5);
INSERT INTO Rents VALUES (2, 2, '2017-01-01 17:00', '0, 0', '2017-01-01 18:00', '10, 10', 1000, 5);


INSERT INTO ChargingStations (NSockets, GPSloc, CostHour, Shape) VALUES (10, '50, 40', 10.5, 'Basic');
INSERT INTO ChargingStations (NSockets, GPSloc, CostHour, Shape) VALUES (15, '40, 50', 10.5, 'Basic');
INSERT INTO ChargingStations (NSockets, GPSloc, CostHour, Shape) VALUES (10, '60, 60', 10.5, 'Basic');

INSERT INTO ChargingHistory VALUES (1, 1, '2017-12-01 12:00:00', '2017-12-01 13:00:00', 100.5);
INSERT INTO ChargingHistory VALUES (1, 2, '2017-12-01 20:00:00', '2017-12-01 21:00:00', 100.5);
INSERT INTO ChargingHistory VALUES (2, 1, '2017-12-01 20:00:00', '2017-12-01 20:45:00', 50.0);
INSERT INTO ChargingHistory VALUES (2, 2, '2017-12-02 09:00:00', '2017-12-02 09:45:00', 50.0);
INSERT INTO ChargingHistory VALUES (3, 3, '2018-11-02 09:00:00', '2018-12-02 09:30:00', 1000.0);
INSERT INTO ChargingHistory (UID, CarID, DateTime_start) VALUES (3, 2, '2018-11-04 09:00:00');

VACUUM;
