DROP TABLE Item IF EXISTS;
DROP TABLE Invoice IF EXISTS;
DROP TABLE Product IF EXISTS;
DROP TABLE Customer IF EXISTS;

CREATE TABLE Customer(ID INTEGER PRIMARY KEY,FirstName VARCHAR(20),LastName VARCHAR(30),Street VARCHAR(50),City VARCHAR(25));
CREATE TABLE Product(ID INTEGER PRIMARY KEY,Name VARCHAR(30),Price DECIMAL);
CREATE TABLE Invoice(ID INTEGER PRIMARY KEY,CustomerID INTEGER,Total DECIMAL, FOREIGN KEY (CustomerId) REFERENCES Customer(ID) ON DELETE CASCADE);
CREATE TABLE Item(InvoiceID INTEGER,Item INTEGER,ProductID INTEGER,Quantity INTEGER,Cost DECIMAL,PRIMARY KEY(InvoiceID,Item), FOREIGN KEY (InvoiceId) REFERENCES Invoice (ID) ON DELETE CASCADE, FOREIGN KEY (ProductId) REFERENCES Product(ID) ON DELETE CASCADE);
COMMIT;


INSERT INTO Customer VALUES(0,'Laura','Steel','429 Seventh Av.','Dallas');
INSERT INTO Product VALUES(0,'Iron Iron',54);
INSERT INTO Customer VALUES(1,'Susanne','King','366 - 20th Ave.','Olten');
INSERT INTO Product VALUES(1,'Chair Shoe',248);
INSERT INTO Customer VALUES(2,'Anne','Miller','20 Upland Pl.','Lyon');
INSERT INTO Product VALUES(2,'Telephone Clock',248);
INSERT INTO Customer VALUES(3,'Michael','Clancy','542 Upland Pl.','San Francisco');
INSERT INTO Product VALUES(3,'Chair Chair',254);
INSERT INTO Customer VALUES(4,'Sylvia','Ringer','365 College Av.','Dallas');
INSERT INTO Product VALUES(4,'Ice Tea Shoe',128);

INSERT INTO Invoice VALUES(0,0,0.0);
INSERT INTO Invoice VALUES(1,2,0.0);

INSERT INTO Item VALUES(0,12,1,11,1.5);

UPDATE Product SET Price=ROUND(Price*.1,2);
UPDATE Item SET Cost=Cost*(SELECT Price FROM Product prod WHERE ProductID=prod.ID);
UPDATE Invoice SET Total=SELECT SUM(Cost*Quantity) FROM Item WHERE InvoiceID=Invoice.ID;

COMMIT;
