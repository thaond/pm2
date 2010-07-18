CREATE TABLE FO_STOCKMASTER (
  ID INT PRIMARY KEY GENERATED BY DEFAULT AS IDENTITY (START WITH 1000),
  STOCKID INT NOT NULL,
  FOREIGN KEY (STOCKID) REFERENCES STOCKMASTER(ID),
  CONSTRAINT UNIQUE_FO_STOCKCODE UNIQUE(STOCKID)
);

CREATE TABLE FO_STOCK_EXPIRY (
  ID INT PRIMARY KEY GENERATED BY DEFAULT AS IDENTITY (START WITH 1000),
  FOSTOCKID INT NOT NULL,
  EXPIRYDATE INT NOT NULL,
  FOREIGN KEY (FOSTOCKID) REFERENCES FO_STOCKMASTER(ID),
  CONSTRAINT UNIQUE_FO_STOCK_EXPIRY UNIQUE(FOSTOCKID, EXPIRYDATE)
);

CREATE TABLE FUTURE_QUOTES (
  ID INT PRIMARY KEY GENERATED BY DEFAULT AS IDENTITY (START WITH 1000),
  FOEXPIRYID INT NOT NULL,
  DATEID INT NOT NULL,
  SOPEN FLOAT(2) NOT NULL,
  SHIGH FLOAT(2) NOT NULL,
  SLOW FLOAT(2) NOT NULL,
  SCLOSE FLOAT(2) NOT NULL,
  ACTIVECONTRACTS INT,
  OPENINTEREST INT,
  CHANGEINOPENINTEREST INT,
  FOREIGN KEY (FOEXPIRYID) REFERENCES FO_STOCK_EXPIRY(ID),
  FOREIGN KEY (DATEID) REFERENCES MARKETDATE(DATEVAL)
);

CREATE TABLE OPTION_QUOTES (
  ID INT PRIMARY KEY GENERATED BY DEFAULT AS IDENTITY (START WITH 1000),
  FOEXPIRYID INT NOT NULL,
  STRIKEPRICE FLOAT(2) NOT NULL,
  TYPE SMALLINT NOT NULL,
  DATEID INT NOT NULL,
  SOPEN FLOAT(2) NOT NULL,
  SHIGH FLOAT(2) NOT NULL,
  SLOW FLOAT(2) NOT NULL,
  SCLOSE FLOAT(2) NOT NULL,
  ACTIVECONTRACTS INT,
  OPENINTEREST INT,
  CHANGEINOPENINTEREST INT,
  FOREIGN KEY (FOEXPIRYID) REFERENCES FO_STOCK_EXPIRY(ID),
  FOREIGN KEY (DATEID) REFERENCES MARKETDATE(DATEVAL)
);