DROP INDEX IDX_QUOTE_DATEID;
DROP INDEX IDX_QUOTE_STOCKID;
DROP TABLE T_HOLDING;
DROP TABLE ICICITRANSACTION;
DROP TABLE CORPRESULT;
DROP TABLE FUNDTRANSACTION;
DROP TABLE CA_BONUS_MAPPING;
DROP TABLE CA_SPLIT_MAPPING;
DROP TABLE QUOTE;
DROP TABLE MARKETDATE;
DROP TABLE TRADE;
DROP TABLE SELLTRANSACTION;
DROP TABLE IPO;
DROP TABLE CA_DIVIDENT;
DROP TABLE CA_SPLIT;
DROP TABLE CA_BONUS;
DROP TABLE CA_DEMERGER_MAPPING;
DROP TABLE CA_DEMERGERLIST;
DROP TABLE CA_DEMERGERBASE;
DROP TABLE CA_MERGER;
DROP TABLE BUYTRANSACTION;
DROP TABLE STOPLOSS;
DROP TABLE WATCHLIST;
DROP TABLE WATCHLISTGROUP;
DROP TABLE ICICI_STOCKCODE_MAPPING;
DROP TABLE YAHOO_STOCKCODE_MAPPING;
DROP TABLE STOCKDAYSTAT;
DROP TABLE SCORECARD;
DROP TABLE STOCKMASTER;
DROP TABLE PORTFOLIO;
DROP TABLE TRADINGACC;

CREATE TABLE STOCKMASTER (
ID INT PRIMARY KEY GENERATED BY DEFAULT AS IDENTITY (START WITH 1000),
STOCKCODE VARCHAR(10) NOT NULL,
COMPANYNAME VARCHAR(100),
SERIES SMALLINT,
PAIDUPVALUE FLOAT(2),
MARKETLOT SMALLINT,
FACEVALUE FLOAT(2),
DATEOFLIST INTEGER,
ISIN VARCHAR(15),
LISTED SMALLINT NOT NULL,
CONSTRAINT UNIQUE_LISTED_STOCKCODE UNIQUE(STOCKCODE, LISTED)
);

CREATE INDEX IDX_STOCKMASTER_STOCKCODE ON STOCKMASTER (STOCKCODE);

CREATE TABLE CORPRESULT (
ID INT PRIMARY KEY GENERATED BY DEFAULT AS IDENTITY (START WITH 1000),
STOCKID INT,
STARTDATE INTEGER,
ENDDATE INTEGER,
TIMELINE SMALLINT,
PERIOD SMALLINT,
AUDITED SMALLINT,
CONSOLIDATED SMALLINT,
BANKING SMALLINT,
RESULTYEAR INTEGER,
EPS FLOAT(2),
FACEVALUE FLOAT(2),
SHARECAPITAL FLOAT(2),
NETPL FLOAT(2),
NONRECURRINGINCOMR FLOAT(2),
NONRECURRINGEXPENSE FLOAT(2),
ADJUSTEDNETPL FLOAT(2),
INTERESTCOST FLOAT(2),
NETSALES FLOAT(2),
FOREIGN KEY (STOCKID) REFERENCES STOCKMASTER(ID)
);

CREATE TABLE ICICI_STOCKCODE_MAPPING (
STOCKID INT,
ICICICODE VARCHAR(15) NOT NULL,
FOREIGN KEY (STOCKID) REFERENCES STOCKMASTER(ID),
CONSTRAINT UNIQUE_ICICICODE UNIQUE(ICICICODE)
);

CREATE TABLE YAHOO_STOCKCODE_MAPPING (
STOCKID INT,
YAHOOCODE VARCHAR(10),
FOREIGN KEY (STOCKID) REFERENCES STOCKMASTER(ID)
);

CREATE TABLE MARKETDATE (
DATEVAL INT PRIMARY KEY,
NSEQUOTES SMALLINT
);

CREATE TABLE QUOTE (
STOCKID INT,
DATEID INT,
SOPEN FLOAT(2),
SHIGH FLOAT(2),
SLOW FLOAT(2),
SCLOSE FLOAT(2),
SPREVCLOSE FLOAT(2),
SVOLUME FLOAT(2),
STRADEVALUE FLOAT(2),
SPERDELIVERYQTY FLOAT(2),
FOREIGN KEY (STOCKID) REFERENCES STOCKMASTER(ID),
FOREIGN KEY (DATEID) REFERENCES MARKETDATE(DATEVAL)
);

CREATE INDEX IDX_QUOTE_DATEID ON QUOTE (DATEID);

CREATE INDEX IDX_QUOTE_STOCKID ON QUOTE (STOCKID);

CREATE INDEX IDX_QUOTE_STOCKID_DATEID ON QUOTE (STOCKID,DATEID);

CREATE TABLE PORTFOLIO (
ID INT PRIMARY KEY GENERATED BY DEFAULT AS IDENTITY (START WITH 1000),
NAME VARCHAR(20) NOT NULL UNIQUE,
ALERT SMALLINT
);

CREATE TABLE TRADINGACC (
ID INT PRIMARY KEY GENERATED BY DEFAULT AS IDENTITY (START WITH 1000),
NAME VARCHAR(20) NOT NULL UNIQUE,
BROKERAGETYPE VARCHAR(20)
);

CREATE TABLE FUNDTRANSACTION (
TRADINGACCID INT NOT NULL,
PORTFOLIOID INT NOT NULL,
TDATE INT NOT NULL,
AMOUNT FLOAT(2) NOT NULL,
REASONCODE SMALLINT NOT NULL,
DETAIL VARCHAR(50),
FOREIGN KEY (PORTFOLIOID) REFERENCES PORTFOLIO(ID),
FOREIGN KEY (TRADINGACCID) REFERENCES TRADINGACC(ID)
);

CREATE TABLE BUYTRANSACTION (
ID INT CONSTRAINT BUYTRANSACTION_PRIMARY_KEY PRIMARY KEY GENERATED BY DEFAULT AS IDENTITY (START WITH 5000),
STOCKID INT NOT NULL,
TDATE INT NOT NULL,
QTY FLOAT(2) NOT NULL,
PRICE FLOAT(2) NOT NULL,
DELIVERYTYPE SMALLINT NOT NULL,
PORTFOLIOID INT NOT NULL,
TRADINGACCID INT NOT NULL,
BROKERAGE FLOAT(2),
SERVICETAX FLOAT(2),
STT FLOAT(4),
FOREIGN KEY (PORTFOLIOID) REFERENCES PORTFOLIO(ID),
FOREIGN KEY (TRADINGACCID) REFERENCES TRADINGACC(ID),
FOREIGN KEY (STOCKID) REFERENCES STOCKMASTER(ID)
);

CREATE TABLE ICICITRANSACTION (
ID INT CONSTRAINT ICICITRANSACTION_PRIMARY_KEY PRIMARY KEY GENERATED BY DEFAULT AS IDENTITY (START WITH 5000),
ICICICODE VARCHAR(10),
STOCKID INT REFERENCES STOCKMASTER(ID),
TDATE INT NOT NULL,
QTY FLOAT(2) NOT NULL,
PRICE FLOAT(2) NOT NULL,
TRANSACTIONTYPE SMALLINT NOT NULL,
DELIVERYTYPE SMALLINT NOT NULL,
PORTFOLIOID INT REFERENCES PORTFOLIO(ID),
BROKERAGE FLOAT(2),
ORDERREF VARCHAR(20) NOT NULL UNIQUE
);

CREATE TABLE T_HOLDING (
BUYID INT NOT NULL,
HOLDINGQTY FLOAT(2),
DIVIDENT FLOAT(2),
FOREIGN KEY (BUYID) REFERENCES BUYTRANSACTION(ID)
);

CREATE TABLE SELLTRANSACTION (
ID INT PRIMARY KEY GENERATED BY DEFAULT AS IDENTITY (START WITH 1000),
TDATE INT NOT NULL,
QTY FLOAT(2) NOT NULL,
PRICE FLOAT(2) NOT NULL,
BROKERAGE FLOAT(2),
SERVICETAX FLOAT(2),
STT FLOAT(4)
);

CREATE TABLE TRADE(
ID INT PRIMARY KEY GENERATED BY DEFAULT AS IDENTITY (START WITH 1000),
BUYID INT NOT NULL,
SELLID INT  NOT NULL,
QTY FLOAT(2) NOT NULL,
DIVIDENT FLOAT(2),
FOREIGN KEY (BUYID) REFERENCES BUYTRANSACTION(ID),
FOREIGN KEY (SELLID) REFERENCES SELLTRANSACTION(ID)
);

CREATE TABLE STOPLOSS(
ID INT PRIMARY KEY GENERATED BY DEFAULT AS IDENTITY (START WITH 1000),
STOCKID INT NOT NULL,
PORTFOLIOID INT NOT NULL,
STOPLOSS2 FLOAT(2),
STOPLOSS1 FLOAT(2),
TARGET1 FLOAT(2),
TARGET2 FLOAT(2),
FOREIGN KEY (STOCKID) REFERENCES STOCKMASTER(ID),
FOREIGN KEY (PORTFOLIOID) REFERENCES PORTFOLIO(ID)
);

CREATE TABLE WATCHLISTGROUP (
ID INT PRIMARY KEY GENERATED BY DEFAULT AS IDENTITY (START WITH 1000),
NAME VARCHAR(20) NOT NULL UNIQUE,
ALERT SMALLINT
);

CREATE TABLE WATCHLIST(
ID INT PRIMARY KEY GENERATED BY DEFAULT AS IDENTITY (START WITH 1000),
STOCKID INT NOT NULL,
WATCHLISTGROUPID INT NOT NULL,
CEIL FLOAT(2),
FLOOR FLOAT(2),
FOREIGN KEY (STOCKID) REFERENCES STOCKMASTER(ID),
FOREIGN KEY (WATCHLISTGROUPID) REFERENCES WATCHLISTGROUP(ID)
);

CREATE TABLE IPO(
ID INT PRIMARY KEY GENERATED BY DEFAULT AS IDENTITY (START WITH 1000),
IPOCODE VARCHAR(20) NOT NULL,
APPLYDATE INT,
APPLYQTY FLOAT(2),
APPLYPRICE FLOAT(2),
APPLIEDAMOUNT FLOAT(2),
ALLOTMENTID INT,
REFUNDDATE INT,
REFUNDAMOUNT INT,
PORTFOLIOID INT NOT NULL,
TRADINGACCID INT NOT NULL,
FOREIGN KEY (PORTFOLIOID) REFERENCES PORTFOLIO(ID),
FOREIGN KEY (TRADINGACCID) REFERENCES TRADINGACC(ID),
FOREIGN KEY (ALLOTMENTID) REFERENCES BUYTRANSACTION(ID)
);

CREATE TABLE CA_DIVIDENT(
ID INT PRIMARY KEY GENERATED BY DEFAULT AS IDENTITY (START WITH 1000),
STOCKID INT NOT NULL,
EXDATE INT NOT NULL,
DIVIDENT FLOAT(4) NOT NULL,
ISPERCENTAGE SMALLINT,
FOREIGN KEY (STOCKID) REFERENCES STOCKMASTER(ID)
);

CREATE TABLE CA_SPLIT(
ID INT PRIMARY KEY GENERATED BY DEFAULT AS IDENTITY (START WITH 1000),
STOCKID INT NOT NULL,
EXDATE INT NOT NULL,
NEWFACEVALUE INT NOT NULL,
OLDFACEVALUE INT NOT NULL,
FOREIGN KEY (STOCKID) REFERENCES STOCKMASTER(ID)
);

CREATE TABLE CA_SPLIT_MAPPING(
SPLITID INT NOT NULL,
TRANSACTIONID INT NOT NULL,
FOREIGN KEY (SPLITID) REFERENCES CA_SPLIT(ID),
FOREIGN KEY (TRANSACTIONID) REFERENCES BUYTRANSACTION(ID),
CONSTRAINT UNIQUE_SPLIT_MAPPING UNIQUE(SPLITID,TRANSACTIONID)
);

CREATE TABLE CA_BONUS(
ID INT PRIMARY KEY GENERATED BY DEFAULT AS IDENTITY (START WITH 1000),
STOCKID INT NOT NULL,
EXDATE INT NOT NULL,
ALLOTMENTDATE INT,
BONUS FLOAT(2) NOT NULL,
BASE INT NOT NULL,
FOREIGN KEY (STOCKID) REFERENCES STOCKMASTER(ID)
);

CREATE TABLE CA_BONUS_MAPPING(
BONUSID INT NOT NULL,
TRANSACTIONID INT NOT NULL,
FOREIGN KEY (BONUSID) REFERENCES CA_BONUS(ID),
FOREIGN KEY (TRANSACTIONID) REFERENCES BUYTRANSACTION(ID),
CONSTRAINT UNIQUE_BONUS_MAPPING UNIQUE(BONUSID,TRANSACTIONID)
);

CREATE TABLE CA_DEMERGERBASE(
ID INT PRIMARY KEY GENERATED BY DEFAULT AS IDENTITY (START WITH 1000),
STOCKID INT NOT NULL,
EXDATE INT NOT NULL,
BASE INT NOT NULL,
FOREIGN KEY (STOCKID) REFERENCES STOCKMASTER(ID)
);

CREATE TABLE CA_MERGER(
ID INT PRIMARY KEY GENERATED BY DEFAULT AS IDENTITY (START WITH 1000),
STOCKID INT NOT NULL,
EXDATE INT NOT NULL,
BASERATIO INT NOT NULL,
PARENTID INT NOT NULL,
PARENTRATIO INT NOT NULL,
FOREIGN KEY (STOCKID) REFERENCES STOCKMASTER(ID),
FOREIGN KEY (PARENTID) REFERENCES STOCKMASTER(ID)
);

CREATE TABLE CA_DEMERGERLIST(
DEMERGERID INT NOT NULL,
STOCKID INT NOT NULL,
BOOKVALUERATIO FLOAT(2) NOT NULL,
FOREIGN KEY (STOCKID) REFERENCES STOCKMASTER(ID),
FOREIGN KEY (DEMERGERID) REFERENCES CA_DEMERGERBASE(ID)
);

CREATE TABLE CA_DEMERGER_MAPPING(
DEMERGERID INT NOT NULL,
TRANSACTIONID INT NOT NULL,
FOREIGN KEY (DEMERGERID) REFERENCES CA_DEMERGERBASE(ID),
FOREIGN KEY (TRANSACTIONID) REFERENCES BUYTRANSACTION(ID),
CONSTRAINT UNIQUE_DEMERGER_MAPPING UNIQUE(DEMERGERID,TRANSACTIONID)
);

CREATE TABLE SCORECARD(
STOCKID INT NOT NULL,
DATEID INT NOT NULL,
OPENHIGN SMALLINT,
CLOSEABOVEPH SMALLINT,
CLOSEABOVE5DH SMALLINT,
CLOSEABOVE30DH SMALLINT,
CLOSEABOVE52WH SMALLINT,
CLOSEBELOWPL SMALLINT,
CLOSEBELOW5DL SMALLINT,
CLOSEBELOW30DL SMALLINT,
CLOSEBELOW52WL SMALLINT,
FLASHQUOTE SMALLINT,
BUYSELL SMALLINT,
MACD SMALLINT,
POSITIVEMOVE5D SMALLINT,
FLASHDELIVERY SMALLINT,
VOLUMEALERT SMALLINT
);

CREATE TABLE STOCKDAYSTAT(
STOCKID INT NOT NULL,
DATEID INT NOT NULL,
HIGH5D FLOAT(2),
HIGH30D FLOAT(2),
HIGH52W FLOAT(2),
HIGHLIFETIME FLOAT(2),
LOW5D FLOAT(2),
LOW30D FLOAT(2),
LOW52W FLOAT(2),
LOWLIFETIME FLOAT(2)
);

DROP FUNCTION CALCULATEDIVIDENT;
CREATE FUNCTION CALCULATEDIVIDENT
(STOCKID INTEGER, BUYDATE INTEGER, SELLDATE INTEGER, QTY INTEGER, DAYTRADE INTEGER)
RETURNS DOUBLE
LANGUAGE JAVA PARAMETER STYLE JAVA
READS SQL DATA
EXTERNAL NAME 'pm.dao.derby.function.DividentFunction.getDivident';

DROP FUNCTION CALCULATEDIVIDENTFORFINYEAR;
CREATE FUNCTION CALCULATEDIVIDENTFORFINYEAR
(STOCKID INTEGER, BUYDATE INTEGER, SELLDATE INTEGER, QTY INTEGER, DAYTRADE INTEGER, FINYEAR INTEGER)
RETURNS DOUBLE
LANGUAGE JAVA PARAMETER STYLE JAVA
READS SQL DATA
EXTERNAL NAME 'pm.dao.derby.function.DividentFunction.getDividentForFinancialYear';

DROP FUNCTION CALCULATEDIVIDENTFORHOLDING;
CREATE FUNCTION CALCULATEDIVIDENTFORHOLDING
(BUYID INTEGER, QTY INTEGER)
RETURNS DOUBLE
LANGUAGE JAVA PARAMETER STYLE JAVA
READS SQL DATA
EXTERNAL NAME 'pm.dao.derby.function.DividentFunction.getDividentForHolding';

DROP FUNCTION CALCULATEDIVIDENTFORTRADE;
CREATE FUNCTION CALCULATEDIVIDENTFORTRADE
(BUYID INTEGER, SELLID INTEGER, QTY INTEGER)
RETURNS DOUBLE
LANGUAGE JAVA PARAMETER STYLE JAVA
READS SQL DATA
EXTERNAL NAME 'pm.dao.derby.function.DividentFunction.getDividentForTrade';

DROP FUNCTION SUMTRADEDQTY;
CREATE FUNCTION SUMTRADEDQTY
(BUYID INTEGER)
RETURNS DOUBLE
LANGUAGE JAVA PARAMETER STYLE JAVA
READS SQL DATA
EXTERNAL NAME 'pm.dao.derby.function.DividentFunction.getTradedQty';

DROP FUNCTION HOLDINGQTYOF;
CREATE FUNCTION HOLDINGQTYOF
(BUYID INTEGER)
RETURNS DOUBLE
LANGUAGE JAVA PARAMETER STYLE JAVA
READS SQL DATA
EXTERNAL NAME 'pm.dao.derby.function.DividentFunction.getHoldingQty';

DROP PROCEDURE RESETDIVIDENT;
CREATE PROCEDURE RESETDIVIDENT (STOCKID INTEGER)
LANGUAGE JAVA PARAMETER STYLE JAVA
MODIFIES SQL DATA
EXTERNAL NAME 'pm.dao.derby.function.DividentFunction.resetDivident';

CREATE TRIGGER ADD_HOLDING_ON_BUYTRANSACTION_INSERT
AFTER INSERT ON BUYTRANSACTION
REFERENCING NEW AS NEWROW
FOR EACH ROW MODE DB2SQL
INSERT INTO T_HOLDING VALUES
(NEWROW.ID, NEWROW.QTY, CALCULATEDIVIDENT(NEWROW.STOCKID, NEWROW.TDATE, 0, NEWROW.QTY, NEWROW.DELIVERYTYPE));

CREATE TRIGGER HOLDING_UPDATE_ON_BUYTRANSACTION_UPDATE
AFTER UPDATE ON BUYTRANSACTION
REFERENCING NEW AS NEWROW  OLD AS OLDROW
FOR EACH ROW MODE DB2SQL
UPDATE T_HOLDING SET
HOLDINGQTY = HOLDINGQTYOF(NEWROW.ID),
DIVIDENT = CALCULATEDIVIDENT(NEWROW.STOCKID, NEWROW.TDATE, 0, NEWROW.QTY, NEWROW.DELIVERYTYPE)
WHERE BUYID = NEWROW.ID;

CREATE TRIGGER REDUCE_HOLDING_ON_TRADE_INSERT
AFTER INSERT ON TRADE
REFERENCING NEW AS NEWROW
FOR EACH ROW MODE DB2SQL
UPDATE T_HOLDING SET
HOLDINGQTY = HOLDINGQTYOF(NEWROW.BUYID),
DIVIDENT = CALCULATEDIVIDENTFORHOLDING(BUYID, HOLDINGQTY)
WHERE BUYID = NEWROW.BUYID;

CREATE TRIGGER UPDATE_HOLDING_ON_TRADE_UPDATE
AFTER UPDATE ON TRADE
REFERENCING NEW AS NEWROW OLD AS OLDROW
FOR EACH ROW MODE DB2SQL
UPDATE T_HOLDING SET
HOLDINGQTY = HOLDINGQTYOF(BUYID),
DIVIDENT = CALCULATEDIVIDENTFORHOLDING(BUYID, HOLDINGQTY)
WHERE BUYID in (NEWROW.BUYID, OLDROW.BUYID);

CREATE TRIGGER DIVIDENT_INSERT
AFTER INSERT ON CA_DIVIDENT
REFERENCING NEW AS NEWROW
FOR EACH ROW MODE DB2SQL
CALL RESETDIVIDENT(NEWROW.STOCKID);

CREATE TRIGGER DIVIDENT_UPDATE
AFTER UPDATE ON CA_DIVIDENT
REFERENCING NEW AS NEWROW
FOR EACH ROW MODE DB2SQL
CALL RESETDIVIDENT(NEWROW.STOCKID);

INSERT INTO STOCKMASTER (STOCKCODE, COMPANYNAME, SERIES, DATEOFLIST, LISTED) VALUES ('^BSESN', 'SENSEX', 1, 19900101, 1);
INSERT INTO STOCKMASTER (STOCKCODE, COMPANYNAME, SERIES, DATEOFLIST, LISTED) VALUES ('^Nifty', 'S&P CNX NIFTY', 2, 19900101, 1);
INSERT INTO STOCKMASTER (STOCKCODE, COMPANYNAME, SERIES, DATEOFLIST, LISTED) VALUES ('^NiftyJun', 'CNX NIFTY JUNIOR', 2, 19900101, 1);
INSERT INTO STOCKMASTER (STOCKCODE, COMPANYNAME, SERIES, DATEOFLIST, LISTED) VALUES ('^CNX100', 'CNX 100', 2, 19900101, 1);
INSERT INTO STOCKMASTER (STOCKCODE, COMPANYNAME, SERIES, DATEOFLIST, LISTED) VALUES ('^CNX500', 'S&P CNX 500', 2, 19900101, 1);
INSERT INTO STOCKMASTER (STOCKCODE, COMPANYNAME, SERIES, DATEOFLIST, LISTED) VALUES ('^Midcap', 'CNX MIDCAP', 2, 19900101, 1);
INSERT INTO STOCKMASTER (STOCKCODE, COMPANYNAME, SERIES, DATEOFLIST, LISTED) VALUES ('^MIDCAP50', 'NIFTY MIDCAP 50', 2, 19900101, 1);
INSERT INTO STOCKMASTER (STOCKCODE, COMPANYNAME, SERIES, DATEOFLIST, LISTED) VALUES ('^Defty', 'S&P CNX DEFTY', 2, 19900101, 1);
INSERT INTO STOCKMASTER (STOCKCODE, COMPANYNAME, SERIES, DATEOFLIST, LISTED) VALUES ('^CNXIT', 'CNXIT', 2, 19900101, 1);
INSERT INTO STOCKMASTER (STOCKCODE, COMPANYNAME, SERIES, DATEOFLIST, LISTED) VALUES ('^BANK', 'BANK NIFTY', 2, 19900101, 1);
INSERT INTO STOCKMASTER (STOCKCODE, COMPANYNAME, SERIES, DATEOFLIST, LISTED) VALUES ('^ESGINDIA', 'S&P ESG INDIA INDEX', 2, 19900101, 1);
INSERT INTO STOCKMASTER (STOCKCODE, COMPANYNAME, SERIES, DATEOFLIST, LISTED) VALUES ('^Infra', 'CNX INFRASTRUCTURE', 2, 19900101, 1);
INSERT INTO STOCKMASTER (STOCKCODE, COMPANYNAME, SERIES, DATEOFLIST, LISTED) VALUES ('^Realty', 'CNX REALTY', 2, 19900101, 1);

