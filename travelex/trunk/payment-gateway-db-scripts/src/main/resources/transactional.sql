PROMPT Creating TGBP transactional tables

PROMPT Drop/Create submission

DROP TABLE submission CASCADE CONSTRAINTS
/

CREATE TABLE submission 
( id                   NUMBER(16) NOT NULL,
  message_id           VARCHAR2(30),
  file_name            VARCHAR2(50) NOT NULL, 
  submission_date      DATE NOT NULL, 
  submission_header    VARCHAR2(1000), 
  input_data           BLOB
)
/

PROMPT Drop/Create instruction

DROP TABLE instruction CASCADE CONSTRAINTS
/

CREATE TABLE instruction 
( id                   NUMBER(16) NOT NULL,
  sub_id               NUMBER(16) NOT NULL,
  out_ins_id           NUMBER(16), 
  amount               NUMBER(16,2) NOT NULL, 
  currency             VARCHAR2(3) NOT NULL, 
  value_date           DATE NOT NULL, 
  data                 VARCHAR2(3000) NOT NULL,
  group_data           VARCHAR2(2000) NOT NULL
)
/

PROMPT Drop/Create output_instruction

DROP TABLE output_instruction CASCADE CONSTRAINTS
/

CREATE TABLE output_instruction 
( id                   NUMBER(16) NOT NULL,
  out_sub_id           NUMBER(16),
  clearing_mechanism   VARCHAR2(10) NOT NULL,
  source_iden          VARCHAR2(3000),
  target_iden          VARCHAR2(2000),
  out_pay_data         VARCHAR2(3000),
  amount               NUMBER(16,2)
)
/

PROMPT Drop/Create output_submission

DROP TABLE output_submission CASCADE CONSTRAINTS
/

CREATE TABLE output_submission 
( id                   NUMBER(16) NOT NULL,
  clearing_mechanism   VARCHAR2(10) NOT NULL,
  file_name            VARCHAR2(100),
  output_date          DATE NOT NULL,
  output_file          BLOB
)
/
