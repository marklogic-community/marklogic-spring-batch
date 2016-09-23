CREATE TABLE users (
  id         INTEGER PRIMARY KEY,
  name VARCHAR(30),
  email  VARCHAR(50)
);

CREATE TABLE comments (
  id         INTEGER PRIMARY KEY,
  userId INTEGER,
  comment VARCHAR(500)
);

CREATE TABLE addresses (
  id         INTEGER PRIMARY KEY,
  userId INTEGER,
  street VARCHAR(50),
  city VARCHAR(50),
  zipCode VARCHAR(5)
);
