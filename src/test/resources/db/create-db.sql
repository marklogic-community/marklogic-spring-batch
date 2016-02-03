CREATE TABLE users (
  id         INTEGER PRIMARY KEY,
  name VARCHAR(30),
  email  VARCHAR(50)
);

CREATE TABLE comments (
  id         INTEGER PRIMARY KEY,
  userId INTEGER,
  comment VARCHAR(50)
);