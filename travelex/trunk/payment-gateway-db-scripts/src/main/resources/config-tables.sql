PROMPT Creating TGBP configuration tables

PROMPT Drop/Create routing_config_on_value_date

DROP TABLE routing_config_on_value_date
/

CREATE TABLE routing_config_on_value_date 
( config_id            NUMBER(16)   NOT NULL, 
  currency             VARCHAR2(3)  NOT NULL, 
  clearing_mechanism   VARCHAR2(10) NOT NULL, 
  clearing_period      NUMBER(3)    NOT NULL, 
  threshold_amount     NUMBER(10,2) NOT NULL, 
  charge               NUMBER(10,2) NOT NULL 
)
/

PROMPT Drop/Create output_ins_batching_config

DROP TABLE output_ins_batching_config
/

CREATE TABLE output_ins_batching_config 
( clearing_mechanism   VARCHAR2(10) NOT NULL, 
  threshold_count      NUMBER(10)   NOT NULL, 
  threshold_amount     NUMBER(10,2) NOT NULL 
)
/

PROMPT Drop/Create remittance_account_config

DROP TABLE remittance_account_config
/

CREATE TABLE remittance_account_config 
( clearing_mechanism              VARCHAR2(10)  NOT NULL, 
  bank_id                         VARCHAR2(10)  NOT NULL,
  bank_name                       VARCHAR2(100) NOT NULL,
  account_number                  VARCHAR2(20)  NOT NULL,
  account_holder_name             VARCHAR2(50)  NOT NULL,
  account_holder_addr_line1       VARCHAR2(200) NOT NULL,
  account_holder_city             VARCHAR2(50)  NOT NULL,
  account_holder_state            VARCHAR2(50)  NOT NULL,
  account_holder_zip              VARCHAR2(15)  NOT NULL
)
/


