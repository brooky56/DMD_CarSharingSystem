DROP TABLE IF EXISTS PartsUsed;
DROP TABLE IF EXISTS Repairs;
DROP TABLE IF EXISTS CurrentCostOfPartTypes;
DROP TABLE IF EXISTS Parts;
DROP TABLE IF EXISTS PartTypes;
DROP TABLE IF EXISTS Providers;
DROP TABLE IF EXISTS Workshops;
DROP TABLE IF EXISTS ChargingHistory;
DROP TABLE IF EXISTS ChargingStations;
DROP TABLE IF EXISTS ParkingHistory;
DROP TABLE IF EXISTS Parks;
DROP TABLE IF EXISTS Rents;
DROP TABLE IF EXISTS AvailableCars;
DROP TABLE IF EXISTS Cars;
DROP TABLE IF EXISTS CarModels;
DROP TABLE IF EXISTS Payments;
DROP TABLE IF EXISTS Customers;
DROP TABLE IF EXISTS Contacts;

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
  UserID         INTEGER  not null references Customers,
  CarID          INTEGER  not null references Cars,
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
  PartID   INTEGER not null unique references Parts
);

PRAGMA foreign_keys = ON;



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

INSERT INTO Payments (UserID, Paid, DateTime) VALUES (1, 12.5,  '2018-11-20 08:20:01.0');
INSERT INTO Payments (UserID, Paid, DateTime) VALUES (4, 25.1,   '2018-11-20 08:21:00.0');
INSERT INTO Payments (UserID, Paid, DateTime) VALUES (2, 10.5,  '2018-11-21 11:10:02.0');
INSERT INTO Payments (UserID, Paid, DateTime) VALUES (5, 15.52, '2018-11-21 14:15:45.0');
INSERT INTO Payments (UserID, Paid, DateTime) VALUES (6, 7.75,  '2018-11-21 17:12:22.0');
INSERT INTO Payments (UserID, Paid, DateTime) VALUES (3, 7.5,   '2018-11-22 19:00:03.0');
INSERT INTO Payments (UserID, Paid, DateTime) VALUES (1, 4.53,  '2018-11-22 08:41:09.0');
INSERT INTO Payments (UserID, Paid, DateTime) VALUES (7, 10.05,  '2018-11-22 10:00:00.0');
INSERT INTO Payments (UserID, Paid, DateTime) VALUES (8, 4.65,  '2018-11-22 09:31:25.0');
INSERT INTO Payments (UserID, Paid, DateTime) VALUES (9, 2.45,  '2018-11-23 07:45:18.0');
INSERT INTO Payments (UserID, Paid, DateTime) VALUES (10, 24.35, '2018-11-23 08:20:00.0');
INSERT INTO Payments (UserID, Paid, DateTime) VALUES (2, 7.45,  '2018-11-23 16:32:00.0');

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

INSERT INTO Cars (ModelID, Reg_number, Color) VALUES (1, 'B-AN102', 'Blue');
INSERT INTO Cars (ModelID, Reg_number, Color) VALUES (2, 'B-AN122', 'White');
INSERT INTO Cars (ModelID, Reg_number, Color) VALUES (1, 'B-BN105', 'Red');
INSERT INTO Cars (ModelID, Reg_number, Color) VALUES (2, 'B-EU201', 'Silver');
INSERT INTO Cars (ModelID, Reg_number, Color) VALUES (3, 'B-RU202', 'Grey');
INSERT INTO Cars (ModelID, Reg_number, Color) VALUES (4, 'B-UA578', 'Black');
INSERT INTO Cars (ModelID, Reg_number, Color) VALUES (3, 'B-FR738', 'Blue');
INSERT INTO Cars (ModelID, Reg_number, Color) VALUES (4, 'B-LT528', 'White');
INSERT INTO Cars (ModelID, Reg_number, Color) VALUES (5, 'B-PL180', 'Red');
INSERT INTO Cars (ModelID, Reg_number, Color) VALUES (6, 'B-LT521', 'Silver');
INSERT INTO Cars (ModelID, Reg_number, Color) VALUES (5, 'B-UA775', 'Green');
INSERT INTO Cars (ModelID, Reg_number, Color) VALUES (6, 'B-KU251', 'Yellow');
INSERT INTO Cars (ModelID, Reg_number, Color) VALUES (7, 'B-KU470', 'Pink');
INSERT INTO Cars (ModelID, Reg_number, Color) VALUES (8, 'B-ES287', 'White');
INSERT INTO Cars (ModelID, Reg_number, Color) VALUES (7, 'B-AU697', 'White');
INSERT INTO Cars (ModelID, Reg_number, Color) VALUES (8, 'B-SW475', 'Green');
INSERT INTO Cars (ModelID, Reg_number, Color) VALUES (9, 'B-UK757', 'Black');
INSERT INTO Cars (ModelID, Reg_number, Color) VALUES (10, 'B-AN532', 'Black');
INSERT INTO Cars (ModelID, Reg_number, Color) VALUES (9, 'B-RU326', 'Brown');
INSERT INTO Cars (ModelID, Reg_number, Color) VALUES (10, 'B-RU663', 'Beige');

INSERT INTO Rents (UserID, CarID, DateTime_start, GPSloc_start, DateTime_end, GPSloc_end, Cost, DistanceKM) VALUES (1, 1, '2018-11-20 08:10', '52.5100384, 13.3713969', '2018-11-20 08:20', '52.5158628, 13.3841342', 12.5, 2.7);
INSERT INTO Rents (UserID, CarID, DateTime_start, GPSloc_start, DateTime_end, GPSloc_end, Cost, DistanceKM) VALUES (4, 1, '2018-11-20 08:01', '52.5158628, 13.3841342', '2018-11-20 08:21', '52.519252, 13.2889583', 25.1, 8.4);
INSERT INTO Rents (UserID, CarID, DateTime_start, GPSloc_start, DateTime_end, GPSloc_end, Cost, DistanceKM) VALUES (2, 3, '2018-11-21 11:03', '52.515916, 13.2835856', '2018-11-20 11:10', '52.5165303, 13.3052846', 10.5, 3.1);
INSERT INTO Rents (UserID, CarID, DateTime_start, GPSloc_start, DateTime_end, GPSloc_end, Cost, DistanceKM) VALUES (5, 8, '2018-11-21 14:00', '52.5152188, 13.2999621', '2018-11-21 14:15', '52.4976656,13.2749799', 15.52, 7.0);
INSERT INTO Rents (UserID, CarID, DateTime_start, GPSloc_start, DateTime_end, GPSloc_end, Cost, DistanceKM) VALUES (6, 12, '2018-11-21 17:05', '52.5073495, 13.2588587', '2018-11-21 17:12', '52.5227001, 13.3192039', 7.75, 3.7);
INSERT INTO Rents (UserID, CarID, DateTime_start, GPSloc_start, DateTime_end, GPSloc_end, Cost, DistanceKM) VALUES (3, 4, '2018-11-22 18:52', '52.5073495, 13.2588587', '2018-11-22 19:00', '52.5227001, 13.3192039', 7.5, 3.7);
INSERT INTO Rents (UserID, CarID, DateTime_start, GPSloc_start, DateTime_end, GPSloc_end, Cost, DistanceKM) VALUES (1, 13, '2018-11-22 08:36', '52.5073495, 13.2588587', '2018-11-22 08:41', '52.5227001, 13.3192039', 4.53, 2.1);
INSERT INTO Rents (UserID, CarID, DateTime_start, GPSloc_start, DateTime_end, GPSloc_end, Cost, DistanceKM) VALUES (7, 9, '2018-11-22 09:53', '52.5073495, 13.2588587', '2018-11-22 10:00', '52.5227001, 13.3192039', 7.75, 3.7);
INSERT INTO Rents (UserID, CarID, DateTime_start, GPSloc_start, DateTime_end, GPSloc_end, Cost, DistanceKM) VALUES (8, 11, '2018-11-22 09:21', '52.5073495, 13.2588587', '2018-11-22 09:31', '52.5227001, 13.3192039', 10.05, 6.7);
INSERT INTO Rents (UserID, CarID, DateTime_start, GPSloc_start, DateTime_end, GPSloc_end, Cost, DistanceKM) VALUES (9, 10, '2018-11-23 07:39', '52.5073495, 13.2588587', '2018-11-23 07:45', '52.5227001, 13.2792039', 2.45, 1.8);
INSERT INTO Rents (UserID, CarID, DateTime_start, GPSloc_start, DateTime_end, GPSloc_end, Cost, DistanceKM) VALUES (10, 14, '2018-11-23 08:00', '52.5158628, 13.3841342', '2018-11-23 08:20', '52.519252, 13.2889583', 24.35, 8.4);
INSERT INTO Rents (UserID, CarID, DateTime_start, GPSloc_start, DateTime_end, GPSloc_end, Cost, DistanceKM) VALUES (2, 15, '2018-11-23 16:25', '52.5073495, 13.2588587', '2018-11-23 16:32', '52.5227001, 13.3192039', 7.45, 3.7);

INSERT INTO AvailableCars(CarID, Charge, Health, GPSloc) VALUES (1, 75, 'Good', '52.5356669, 13.3583623');
INSERT INTO AvailableCars(CarID, Charge, Health, GPSloc) VALUES (2, 90, 'Good', '52.5356669, 13.3583623');
INSERT INTO AvailableCars(CarID, Charge, Health, GPSloc) VALUES (3, 55, 'Normal', '52.5356669, 13.2083623');
INSERT INTO AvailableCars(CarID, Charge, Health, GPSloc) VALUES (4, 87, 'Good', '52.5356669, 13.3583623');
INSERT INTO AvailableCars(CarID, Charge, Health, GPSloc) VALUES (5, 72, 'Good', '52.5356669, 13.2083623');
INSERT INTO AvailableCars(CarID, Charge, Health, GPSloc) VALUES (6, 65, 'Normal', '52.5356669, 13.3583623');
INSERT INTO AvailableCars(CarID, Charge, Health, GPSloc) VALUES (7, 42, 'Good', '52.5356669, 13.2083623');
INSERT INTO AvailableCars(CarID, Charge, Health, GPSloc) VALUES (8, 98, 'Good', '52.5356669, 13.3583623');
INSERT INTO AvailableCars(CarID, Charge, Health, GPSloc) VALUES (9, 98, 'Good', '52.5144978, 13.4752225');
INSERT INTO AvailableCars(CarID, Charge, Health, GPSloc) VALUES (10, 98, 'Good', '52.5365921,13.2810933');
INSERT INTO AvailableCars(CarID, Charge, Health, GPSloc) VALUES (11, 98, 'Normal', '52.5356669, 13.3583623');
INSERT INTO AvailableCars(CarID, Charge, Health, GPSloc) VALUES (12, 98, 'Good', '52.5365921,13.2810933');
INSERT INTO AvailableCars(CarID, Charge, Health, GPSloc) VALUES (13, 98, 'Normal', '52.5356669, 13.3583623');
INSERT INTO AvailableCars(CarID, Charge, Health, GPSloc) VALUES (14, 98, 'Good', '52.5144978, 13.4752225');
INSERT INTO AvailableCars(CarID, Charge, Health, GPSloc) VALUES (15, 98, 'Normal', '52.5356669, 13.3583623');
INSERT INTO AvailableCars(CarID, Charge, Health, GPSloc) VALUES (16, 98, 'Good', '52.5356669, 13.3583623');
INSERT INTO AvailableCars(CarID, Charge, Health, GPSloc) VALUES (17, 98, 'Left light does not work' , '52.5144978, 13.4752225');

INSERT INTO Parks (NPlaces, GPSloc) VALUES (50,  '52.5184755, 13.3865654');
INSERT INTO Parks (NPlaces, GPSloc) VALUES (45,  '52.5174873, 13.3807356');
INSERT INTO Parks (NPlaces, GPSloc) VALUES (30,  '52.5174873, 13.380735');
INSERT INTO Parks (NPlaces, GPSloc) VALUES (20,  '52.5175205, 13.3786468');
INSERT INTO Parks (NPlaces, GPSloc) VALUES (55,  '52.5175205, 13.3786468');
INSERT INTO Parks (NPlaces, GPSloc) VALUES (100, '52.5175205, 13.3786468');
INSERT INTO Parks (NPlaces, GPSloc) VALUES (10,  '52.5176879, 13.3722649');


INSERT INTO ParkingHistory (ParkID, CarID, DateTime_arrive, DateTime_leave) VALUES (2, 3,  '2018-11-20 23:00', '2018-11-20 06:00');
INSERT INTO ParkingHistory (ParkID, CarID, DateTime_arrive, DateTime_leave) VALUES (3, 4,  '2018-11-21 23:00', '2018-11-21 06:00');
INSERT INTO ParkingHistory (ParkID, CarID, DateTime_arrive, DateTime_leave) VALUES (4, 5,  '2018-11-21 23:00', '2018-11-21 06:00');
INSERT INTO ParkingHistory (ParkID, CarID, DateTime_arrive, DateTime_leave) VALUES (5, 7,  '2018-11-22 23:00', '2018-11-22 06:00');
INSERT INTO ParkingHistory (ParkID, CarID, DateTime_arrive, DateTime_leave) VALUES (6, 8,  '2018-11-22 23:00', '2018-11-22 06:00');
INSERT INTO ParkingHistory (ParkID, CarID, DateTime_arrive, DateTime_leave) VALUES (7, 9,  '2018-11-23 23:00', '2018-11-23 06:00');
INSERT INTO ParkingHistory (ParkID, CarID, DateTime_arrive, DateTime_leave) VALUES (1, 10, '2018-11-23 23:00', '2018-11-23 06:00');
INSERT INTO ParkingHistory (ParkID, CarID, DateTime_arrive, DateTime_leave) VALUES (2, 11, '2018-11-20 23:00', '2018-11-20 06:00');
INSERT INTO ParkingHistory (ParkID, CarID, DateTime_arrive, DateTime_leave) VALUES (3, 12, '2018-11-20 23:00', '2018-11-20 06:00');
INSERT INTO ParkingHistory (ParkID, CarID, DateTime_arrive, DateTime_leave) VALUES (4, 13, '2018-11-22 23:00', '2018-11-20 06:00');
INSERT INTO ParkingHistory (ParkID, CarID, DateTime_arrive, DateTime_leave) VALUES (5, 14, '2018-11-22 23:00', '2018-11-22 06:00');
INSERT INTO ParkingHistory (ParkID, CarID, DateTime_arrive, DateTime_leave) VALUES (6, 15, '2018-11-23 23:00', '2018-11-23 06:00');
INSERT INTO ParkingHistory (ParkID, CarID, DateTime_arrive, DateTime_leave) VALUES (7, 16, '2018-11-23 23:00', '2018-11-23 06:00');
INSERT INTO ParkingHistory (ParkID, CarID, DateTime_arrive, DateTime_leave) VALUES (1, 17, '2018-11-21 23:00', '2018-11-21 06:00');
INSERT INTO ParkingHistory (ParkID, CarID, DateTime_arrive, DateTime_leave) VALUES (1, 18, '2018-11-21 23:00', '2018-11-21 06:00');
INSERT INTO ParkingHistory (ParkID, CarID, DateTime_arrive, DateTime_leave) VALUES (2, 19, '2018-11-22 23:00', '2018-11-22 06:00');
INSERT INTO ParkingHistory (ParkID, CarID, DateTime_arrive, DateTime_leave) VALUES (2, 20, '2018-11-22 23:00', '2018-11-22 06:00');

INSERT INTO ChargingStations (NSockets, GPSloc, CostHour, SocketShape) VALUES (10, '52.5195766, 13.3892441', 7.5, 'J1772');
INSERT INTO ChargingStations (NSockets, GPSloc, CostHour, SocketShape) VALUES (15, '52.5250275, 13.4190273', 5.85, 'Mennekes');
INSERT INTO ChargingStations (NSockets, GPSloc, CostHour, SocketShape) VALUES (10, '52.5302688, 13.4184802', 6.05, 'CCS Combo');
INSERT INTO ChargingStations (NSockets, GPSloc, CostHour, SocketShape) VALUES (15, '52.5195766, 13.3892441', 5.65, 'GB/T');
INSERT INTO ChargingStations (NSockets, GPSloc, CostHour, SocketShape) VALUES (12, '52.5214633, 13.3882999', 8.0, 'CHAdeMO');
INSERT INTO ChargingStations (NSockets, GPSloc, CostHour, SocketShape) VALUES (10, '52.5038338, 13.3803928', 5.55, 'Mennekes');
INSERT INTO ChargingStations (NSockets, GPSloc, CostHour, SocketShape) VALUES (10, '52.51024, 13.3754039', 6.95, 'CSS Combo');

INSERT INTO ChargingHistory (UID, CarID, DateTime_start, DateTime_end, Cost) VALUES (2, 6, '2018-11-20 11:45:00', '2018-11-20 15:45:00', 23.4);
INSERT INTO ChargingHistory (UID, CarID, DateTime_start, DateTime_end, Cost) VALUES (2, 2, '2018-11-21 09:30:00', '2018-11-21 19:30:00', 58.5);
INSERT INTO ChargingHistory (UID, CarID, DateTime_start, DateTime_end, Cost) VALUES (5, 7, '2018-11-21 16:30:00', '2018-11-21 20:30:00', 32);
INSERT INTO ChargingHistory (UID, CarID, DateTime_start, DateTime_end, Cost) VALUES (4, 11, '2018-11-21 08:30:00', '2018-11-21 20:30:00', 56.5);
INSERT INTO ChargingHistory (UID, CarID, DateTime_start, DateTime_end, Cost) VALUES (1, 3, '2018-11-22 17:20:00', '2018-11-22 19:40:00', 17.5);
INSERT INTO ChargingHistory (UID, CarID, DateTime_start, DateTime_end, Cost) VALUES (2, 8, '2018-11-22 16:30:00', '2018-11-22 20:30:00', 23.4);
INSERT INTO ChargingHistory (UID, CarID, DateTime_start, DateTime_end, Cost) VALUES (3, 12, '2018-11-22 16:30:00', '2018-11-22 20:30:00', 24,2);
INSERT INTO ChargingHistory (UID, CarID, DateTime_start, DateTime_end, Cost) VALUES (6, 4, '2018-11-23 08:30:00', '2018-11-23 10:30:00', 11.3);
INSERT INTO ChargingHistory (UID, CarID, DateTime_start, DateTime_end, Cost) VALUES (4, 13, '2018-11-23 12:00:00', '2018-11-23 13:30:00', 8,475);
INSERT INTO ChargingHistory (UID, CarID, DateTime_start, DateTime_end, Cost) VALUES (4, 9, '2018-11-23 10:30:00', '2018-11-23 20:30:00', 56.5);
INSERT INTO ChargingHistory (UID, CarID, DateTime_start, DateTime_end, Cost) VALUES (3, 10, '2018-11-24 12:30:00', '2018-11-24 13:30:00', 6.05);
INSERT INTO ChargingHistory (UID, CarID, DateTime_start, DateTime_end, Cost) VALUES (1, 14, '2018-11-24 16:30:00', '2018-11-24 18:30:00', 15);
INSERT INTO ChargingHistory (UID, CarID, DateTime_start, DateTime_end, Cost) VALUES (5, 5, '2018-11-24 16:30:00', '2018-11-24 17:30:00', 8.0);

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

INSERT INTO Parts (PartTypeID, WID, ProviderID, Paid, Date_bought) VALUES (1,  1, 1, 1500, '2018-11-21');
INSERT INTO Parts (PartTypeID, WID, ProviderID, Paid, Date_bought) VALUES (1,  5, 3, 1450, '2018-11-21');
INSERT INTO Parts (PartTypeID, WID, ProviderID, Paid, Date_bought) VALUES (2,  1, 1, 1200, '2018-11-20');
INSERT INTO Parts (PartTypeID, WID, ProviderID, Paid, Date_bought) VALUES (2,  5, 3, 1225, '2018-11-20');
INSERT INTO Parts (PartTypeID, WID, ProviderID, Paid, Date_bought) VALUES (3,  2, 2, 600,  '2018-11-20');
INSERT INTO Parts (PartTypeID, WID, ProviderID, Paid, Date_bought) VALUES (4,  2, 2, 1100, '2018-11-21');
INSERT INTO Parts (PartTypeID, WID, ProviderID, Paid, Date_bought) VALUES (5,  3, 5, 105,  '2018-11-21');
INSERT INTO Parts (PartTypeID, WID, ProviderID, Paid, Date_bought) VALUES (6,  3, 1, 75,   '2018-11-22');
INSERT INTO Parts (PartTypeID, WID, ProviderID, Paid, Date_bought) VALUES (7,  4, 1, 110,  '2018-11-20');
INSERT INTO Parts (PartTypeID, WID, ProviderID, Paid, Date_bought) VALUES (7,  1, 3, 105,  '2018-11-20');
INSERT INTO Parts (PartTypeID, WID, ProviderID, Paid, Date_bought) VALUES (8,  4, 3, 300,  '2018-11-22');
INSERT INTO Parts (PartTypeID, WID, ProviderID, Paid, Date_bought) VALUES (9,  5, 1, 150,  '2018-11-21');
INSERT INTO Parts (PartTypeID, WID, ProviderID, Paid, Date_bought) VALUES (10, 5, 1, 135,  '2018-11-23');
INSERT INTO Parts (PartTypeID, WID, ProviderID, Paid, Date_bought) VALUES (11, 1, 4, 950,  '2018-11-20');
INSERT INTO Parts (PartTypeID, WID, ProviderID, Paid, Date_bought) VALUES (12, 2, 5, 1050, '2018-11-22');
INSERT INTO Parts (PartTypeID, WID, ProviderID, Paid, Date_bought) VALUES (13, 3, 2, 65,   '2018-11-23');
INSERT INTO Parts (PartTypeID, WID, ProviderID, Paid, Date_bought) VALUES (14, 4, 2, 35,   '2018-11-20');
INSERT INTO Parts (PartTypeID, WID, ProviderID, Paid, Date_bought) VALUES (15, 5, 2, 210,  '2018-11-22');
INSERT INTO Parts (PartTypeID, WID, ProviderID, Paid, Date_bought) VALUES (15, 2, 5, 230,  '2018-11-22');
INSERT INTO Parts (PartTypeID, WID, ProviderID, Paid, Date_bought) VALUES (16, 1, 2, 310,  '2018-11-23');
INSERT INTO Parts (PartTypeID, WID, ProviderID, Paid, Date_bought) VALUES (17, 2, 3, 875,  '2018-11-20');
INSERT INTO Parts (PartTypeID, WID, ProviderID, Paid, Date_bought) VALUES (18, 3, 5, 675,  '2018-11-21');
INSERT INTO Parts (PartTypeID, WID, ProviderID, Paid, Date_bought) VALUES (19, 4, 2, 335,  '2018-11-20');
INSERT INTO Parts (PartTypeID, WID, ProviderID, Paid, Date_bought) VALUES (20, 5, 2, 535,  '2018-11-23');

INSERT INTO Repairs (WID, CarID, Date_start, Date_end) VALUES (1, 2, '2018-11-20', '2018-11-21');
INSERT INTO Repairs (WID, CarID, Date_start, Date_end) VALUES (5, 6, '2018-11-21', '2018-11-22');

INSERT INTO PartsUsed VALUES(1, 6);
INSERT INTO PartsUsed VALUES(2, 8);
