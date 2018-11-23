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
  Brand         TEXT    not null check(LENGTH(Brand) > 0),
  Name          TEXT    not null check(LENGTH(Name) > 0),
  SocketShape   TEXT    not null check(LENGTH(SocketShape) > 0),
  unique (Brand, Name)
);

CREATE TABLE Cars
(
  CarID      INTEGER not null primary key autoincrement,
  ModelID    INTEGER not null references CarModels,
  Reg_number TEXT    not null unique check(LENGTH(Reg_number) > 0),
  Color      TEXT    not null check(LENGTH(Color) > 0)
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
  VALUES ('f.braun@gmail.com', '229-11-29', 'Germany', 'Berlin', 'Arkonaplatz', '10115');
INSERT INTO Contacts (Email, Phone, Country, City, Street, ZIP_code)
  VALUES ('s.klein@gmail.com', '318-23-17', 'Germany', 'Berlin', 'Linienstrasse', '10178');
INSERT INTO Contacts (Email, Phone, Country, City, Street, ZIP_code)
  VALUES ('a.schwartz@gmail.com', '229-23-46', 'Germany', 'Berlin', 'Zionskirchstrasse', '10365');
INSERT INTO Contacts (Email, Phone, Country, City, Street, ZIP_code)
  VALUES ('s.walter@gmail.com', '318-22-59', 'Germany', 'Berlin', 'Oranienstrasse', '10407');
INSERT INTO Contacts (Email, Phone, Country, City, Street, ZIP_code)
  VALUES ('p.becker@gmail.com', '228-45-69', 'Germany', 'Berlin', 'Danziger Strasse', '10435');
INSERT INTO Contacts (Email, Phone, Country, City, Street, ZIP_code)
  VALUES ('o.koch@gmail.com', '234-67-19', 'Germany', 'Berlin', 'Oranienstrasse', '10551');
INSERT INTO Contacts (Email, Phone, Country, City, Street, ZIP_code)
  VALUES ('s.ballack@gmail.com', '219-76-89', 'Germany', 'Berlin', 'Chausseestrasse', '10555');
INSERT INTO Contacts (Email, Phone, Country, City, Street, ZIP_code)
  VALUES ('m.nouer@gmail.com', '276-87-99', 'Germany', 'Berlin', 'Karl-Marx-Allee', '10319');
INSERT INTO Contacts (Email, Phone, Country, City, Street, ZIP_code)
  VALUES ('m.roues@gmail.com', '285-90-34', 'Germany', 'Berlin', 'Choriner Strasse', '10435');
INSERT INTO Contacts (Email, Phone, Country, City, Street, ZIP_code)
  VALUES ('t.kross@gmail.com', '234-34-56', 'Germany', 'Berlin', 'Skalitzer Strasse', '10317');
INSERT INTO Contacts (Email, Phone, Country, City, Street, ZIP_code)
  VALUES ('t.muler@gmail.com', '231-35-12', 'Germany', 'Berlin', 'Legiendamm', '10367');
INSERT INTO Contacts (Email, Phone, Country, City, Street, ZIP_code)
  VALUES ('o.khan@gmail.com', '278-16-65', 'Germany', 'Berlin', 'Majakowskiring', '10614');
INSERT INTO Contacts (Email, Phone, Country, City, Street, ZIP_code)
  VALUES ('c.bouteng@gmail.com', '201-04-56', 'Germany', 'Berlin', 'Kaiserdamm', '10717');
INSERT INTO Contacts (Email, Phone, Country, City, Street, ZIP_code)
  VALUES ('l.sane@gmail.com', '233-85-73', 'Germany', 'Berlin', 'Turmstrave', '10415');
INSERT INTO Contacts (Email, Phone, Country, City, Street, ZIP_code)
  VALUES ('a.beckenbauer@gmail.com', '276-10-56', 'Germany', 'Berlin', 'Leuschnerdamm', '10321');

INSERT INTO Customers (Username, Password, First_name, Last_name, ContactID)
  VALUES ('Vlad30', '1234567', 'Braun', 'Fridrih', 1);
INSERT INTO Customers (Username, Password, First_name, Last_name, ContactID)
  VALUES ('CrazyIvan', 'BpbLeaaehs', 'Klein', 'Sem', 2);
INSERT INTO Customers (Username, Password, First_name, Last_name, ContactID)
  VALUES ('EiffelTOP', 'Vtqth<thn', 'Walter', 'Scott', 3);
INSERT INTO Customers (Username, Password, First_name, Last_name, ContactID)
  VALUES ('ForzaJuve', 'CexxbUexxb', 'Becker', 'Paulo', 4);
INSERT INTO Customers (Username, Password, First_name, Last_name, ContactID)
  VALUES ('Bavaria', 'bkGofQBZ5', 'Becker', 'Paula', 5);
INSERT INTO Customers (Username, Password, First_name, Last_name, ContactID)
  VALUES ('KochanO', 'PdgJEcfQs', 'Koch', 'Oliver', 6);
INSERT INTO Customers (Username, Password, First_name, Last_name, ContactID)
  VALUES ('Ballack13', 'W6zcpnFRW', 'Ballack', 'Samuel', 7);
INSERT INTO Customers (Username, Password, First_name, Last_name, ContactID)
    VALUES ('NouerNumberOne', 'TfQVx8XFw', 'Nouer', 'Manuel', 8);
INSERT INTO Customers (Username, Password, First_name, Last_name, ContactID)
  VALUES ('Marko13', 'rpbfO0s4D', 'Roues', 'Marko', 9);
INSERT INTO Customers (Username, Password, First_name, Last_name, ContactID)
  VALUES ('KroosMadrid', 'SuwQesOcT', 'Kross', 'Tony', 10);

INSERT INTO CarModels (Brand, Name, SocketShape) VALUES ('Volkswagen', 'Golf', 'J1772');
INSERT INTO CarModels (Brand, Name, SocketShape) VALUES ('Mercedes Benz', 'Gelandewagen', 'Mennekes');
INSERT INTO CarModels (Brand, Name, SocketShape) VALUES ('Ford', 'Fusion', 'CHAdeMO');
INSERT INTO CarModels (Brand, Name, SocketShape) VALUES ('Mercedes Benz', 'SLA', 'Mennekes');
INSERT INTO CarModels (Brand, Name, SocketShape) VALUES ('KIA', 'Spectre', 'GB/T');
INSERT INTO CarModels (Brand, Name, SocketShape) VALUES ('Honda', 'Pilot', 'CCS Combo');
INSERT INTO CarModels (Brand, Name, SocketShape) VALUES ('Chevrolet', 'Aveo', 'GB/T');
INSERT INTO CarModels (Brand, Name, SocketShape) VALUES ('BMW', 'M5', 'J1772');
INSERT INTO CarModels (Brand, Name, SocketShape) VALUES ('Honda', 'Insight', 'CCS Combo');
INSERT INTO CarModels (Brand, Name, SocketShape) VALUES ('Ford', 'Mustang', 'CHAdeMO');

INSERT INTO Cars (ModelID, Reg_number, Color) VALUES (1, 'AN102030', 'Blue');
INSERT INTO Cars (ModelID, Reg_number, Color) VALUES (2, 'AN122131', 'White');
INSERT INTO Cars (ModelID, Reg_number, Color) VALUES (1, 'BN105020', 'Red');
INSERT INTO Cars (ModelID, Reg_number, Color) VALUES (2, 'EU201809', 'Silver');
INSERT INTO Cars (ModelID, Reg_number, Color) VALUES (3, 'RU202020', 'Grey');
INSERT INTO Cars (ModelID, Reg_number, Color) VALUES (4, 'UA578126', 'Black');
INSERT INTO Cars (ModelID, Reg_number, Color) VALUES (3, 'FR738032', 'Blue');
INSERT INTO Cars (ModelID, Reg_number, Color) VALUES (4, 'LT528791', 'White');
INSERT INTO Cars (ModelID, Reg_number, Color) VALUES (5, 'PL180472', 'Red');
INSERT INTO Cars (ModelID, Reg_number, Color) VALUES (6, 'LT521270', 'Silver');
INSERT INTO Cars (ModelID, Reg_number, Color) VALUES (5, 'UA775685', 'Green');
INSERT INTO Cars (ModelID, Reg_number, Color) VALUES (6, 'KZ251178', 'Yellow');
INSERT INTO Cars (ModelID, Reg_number, Color) VALUES (7, 'KZ470883', 'Pink');
INSERT INTO Cars (ModelID, Reg_number, Color) VALUES (8, 'ES287132', 'White');
INSERT INTO Cars (ModelID, Reg_number, Color) VALUES (7, 'AU697266', 'White');
INSERT INTO Cars (ModelID, Reg_number, Color) VALUES (8, 'SW475863', 'Green');
INSERT INTO Cars (ModelID, Reg_number, Color) VALUES (9, 'UK757311', 'Black');
INSERT INTO Cars (ModelID, Reg_number, Color) VALUES (10, 'AN531472', 'Black');
INSERT INTO Cars (ModelID, Reg_number, Color) VALUES (9, 'RU326657', 'Brown');
INSERT INTO Cars (ModelID, Reg_number, Color) VALUES (10, 'RU663313', 'Beige');

INSERT INTO Rents VALUES (1, 1, '2018-11-23 07:00', '0, 0', '2018-11-23 08:00', '10, 10', 1000, 5);
INSERT INTO Rents VALUES (3, 2, '2018-11-23 07:00', '0, 0', '2018-11-23 08:00', '10, 10', 1000, 5);
INSERT INTO Rents VALUES (1, 3, '2018-11-23 08:00', '10, 10', '2018-11-23 09:00', '0, 0', 1000, 5);
INSERT INTO Rents VALUES (3, 4, '2018-11-23 08:00', '10, 10', '2018-11-23 09:00', '0, 0', 1000, 5);
INSERT INTO Rents VALUES (2, 1, '2018-11-23 12:00', '0, 0', '2018-11-23 13:00', '10, 10', 1000, 5);
INSERT INTO Rents VALUES (1, 2, '2018-11-23 12:00', '0, 0', '2018-11-23 13:00', '10, 10', 1000, 5);
INSERT INTO Rents VALUES (3, 1, '2018-11-23 17:00', '0, 0', '2018-11-23 18:00', '10, 10', 1000, 5);
INSERT INTO Rents VALUES (2, 2, '2018-11-23 17:00', '0, 0', '2018-11-23 18:00', '10, 10', 1000, 5);

INSERT INTO Parks(NPlaces, GPSloc) VALUES (50,  '52.5184755, 13.3865654');
INSERT INTO Parks(NPlaces, GPSloc) VALUES (45,  '52.5174873, 13.3807356');
INSERT INTO Parks(NPlaces, GPSloc) VALUES (30,  '52.5174873, 13.380735');
INSERT INTO Parks(NPlaces, GPSloc) VALUES (20,  '52.5175205, 13.3786468');
INSERT INTO Parks(NPlaces, GPSloc) VALUES (55,  '52.5175205, 13.3786468');
INSERT INTO Parks(NPlaces, GPSloc) VALUES (100, '52.5175205, 13.3786468');
INSERT INTO Parks(NPlaces, GPSloc) VALUES (10,  '52.5176879, 13.3722649');

INSERT INTO ChargingStations (NSockets, GPSloc, CostHour, SocketShape) VALUES (10, '52.5195766, 13.3892441', 7.5, 'J1772');
INSERT INTO ChargingStations (NSockets, GPSloc, CostHour, SocketShape) VALUES (15, '52.5250275, 13.4190273', 5.85, 'Mennekes');
INSERT INTO ChargingStations (NSockets, GPSloc, CostHour, SocketShape) VALUES (10, '52.5302688, 13.4184802', 6.05, 'CCS Combo');
INSERT INTO ChargingStations (NSockets, GPSloc, CostHour, SocketShape) VALUES (15, '52.5195766, 13.3892441', 5.65, 'GB/T');
INSERT INTO ChargingStations (NSockets, GPSloc, CostHour, SocketShape) VALUES (12, '52.5214633, 13.3882999', 8.0, 'CHAdeMO');
INSERT INTO ChargingStations (NSockets, GPSloc, CostHour, SocketShape) VALUES (10, '52.5038338, 13.3803928', 5.55, 'Mennekes');
INSERT INTO ChargingStations (NSockets, GPSloc, CostHour, SocketShape) VALUES (10, '52.51024, 13.3754039', 6.95, 'CSS Combo');

INSERT INTO ChargingHistory VALUES (1, 1, '2017-12-01 12:00:00', '2017-12-01 13:00:00', 100.5);
INSERT INTO ChargingHistory VALUES (1, 2, '2017-12-01 20:00:00', '2017-12-01 21:00:00', 100.5);
INSERT INTO ChargingHistory VALUES (2, 1, '2017-12-01 20:00:00', '2017-12-01 20:45:00', 50.0);
INSERT INTO ChargingHistory VALUES (2, 2, '2017-12-02 09:00:00', '2017-12-02 09:45:00', 50.0);
INSERT INTO ChargingHistory VALUES (3, 3, '2018-11-02 09:00:00', '2018-12-02 09:30:00', 1000.0);
INSERT INTO ChargingHistory (UID, CarID, DateTime_start) VALUES (3, 2, '2018-11-04 09:00:00');

INSERT INTO Workshops(NPlaces, GPSloc) VALUES (10, '52.5151250, 13.5041570');
INSERT INTO Workshops(NPlaces, GPSloc) VALUES (7,  '52.5165847, 13.3141253');
INSERT INTO Workshops(NPlaces, GPSloc) VALUES (5,  '52.5276901, 13.4252264');
INSERT INTO Workshops(NPlaces, GPSloc) VALUES (12, '52.7202866, 13.3362814');
INSERT INTO Workshops(NPlaces, GPSloc) VALUES (5,  '52.6293875, 13.3561214');
INSERT INTO Workshops(NPlaces, GPSloc) VALUES (7,  '52.5684884, 13.3454411');
INSERT INTO Workshops(NPlaces, GPSloc) VALUES (4,  '52.6765893, 13.3732356');

INSERT INTO Providers(Name, ContactID) VALUES ('Car Parts Stock', 11);
INSERT INTO Providers(Name, ContactID) VALUES ('Mini-Center-Berlin', 12);
INSERT INTO Providers(Name, ContactID) VALUES ('Daimler AG', 13);
INSERT INTO Providers(Name, ContactID) VALUES ('British Car Center', 14);
INSERT INTO Providers(Name, ContactID) VALUES ('Biesdorf Auto Center', 15);

INSERT INTO PartTypes (Name, ModelID) VALUES ('Left door', 10);
INSERT INTO PartTypes (Name, ModelID) VALUES ('Left door', 1);
INSERT INTO PartTypes (Name, ModelID) VALUES ('Bumper', 9);
INSERT INTO PartTypes (Name, ModelID) VALUES ('Bumper', 2);
INSERT INTO PartTypes (Name, ModelID) VALUES ('Right Mirror', 8);
INSERT INTO PartTypes (Name, ModelID) VALUES ('Right Mirror', 3);
INSERT INTO PartTypes (Name, ModelID) VALUES ('Grilles', 7);
INSERT INTO PartTypes (Name, ModelID) VALUES ('Grilles', 4);
INSERT INTO PartTypes (Name, ModelID) VALUES ('Left tail light', 6);
INSERT INTO PartTypes (Name, ModelID) VALUES ('Left tail light', 5);
INSERT INTO PartTypes (Name, ModelID) VALUES ('Right fender', 5);
INSERT INTO PartTypes (Name, ModelID) VALUES ('Right fender', 6);
INSERT INTO PartTypes (Name, ModelID) VALUES ('Right door handle', 4);
INSERT INTO PartTypes (Name, ModelID) VALUES ('Right door handle', 7);
INSERT INTO PartTypes (Name, ModelID) VALUES ('Headlights', 3);
INSERT INTO PartTypes (Name, ModelID) VALUES ('Headlights', 8);
INSERT INTO PartTypes (Name, ModelID) VALUES ('Hood', 2);
INSERT INTO PartTypes (Name, ModelID) VALUES ('Hood', 9);
INSERT INTO PartTypes (Name, ModelID) VALUES ('Nose panel', 1);
INSERT INTO PartTypes (Name, ModelID) VALUES ('Nose panel', 10);

INSERT INTO CurrentCostOfPartTypes VALUES (1, 1, 1500);
INSERT INTO CurrentCostOfPartTypes VALUES (1, 3, 1450);
INSERT INTO CurrentCostOfPartTypes VALUES (2, 1, 1200);
INSERT INTO CurrentCostOfPartTypes VALUES (2, 3, 1225);
INSERT INTO CurrentCostOfPartTypes VALUES (3, 2, 600);
INSERT INTO CurrentCostOfPartTypes VALUES (4, 2, 1100);
INSERT INTO CurrentCostOfPartTypes VALUES (5, 5, 105);
INSERT INTO CurrentCostOfPartTypes VALUES (6, 1, 75);
INSERT INTO CurrentCostOfPartTypes VALUES (7, 1, 110);
INSERT INTO CurrentCostOfPartTypes VALUES (7, 3, 105);
INSERT INTO CurrentCostOfPartTypes VALUES (8, 3, 300);
INSERT INTO CurrentCostOfPartTypes VALUES (9, 1, 150);
INSERT INTO CurrentCostOfPartTypes VALUES (10, 1, 135);
INSERT INTO CurrentCostOfPartTypes VALUES (11, 4, 950);
INSERT INTO CurrentCostOfPartTypes VALUES (12, 5, 1050);
INSERT INTO CurrentCostOfPartTypes VALUES (13, 2, 65);
INSERT INTO CurrentCostOfPartTypes VALUES (14, 2, 35);
INSERT INTO CurrentCostOfPartTypes VALUES (15, 2, 210);
INSERT INTO CurrentCostOfPartTypes VALUES (15, 5, 230);
INSERT INTO CurrentCostOfPartTypes VALUES (16, 2, 310);
INSERT INTO CurrentCostOfPartTypes VALUES (17, 3, 875);
INSERT INTO CurrentCostOfPartTypes VALUES (18, 5, 675);
INSERT INTO CurrentCostOfPartTypes VALUES (19, 2, 335);
INSERT INTO CurrentCostOfPartTypes VALUES (20, 2, 535);
