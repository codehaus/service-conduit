Insert into ROUTING_CONFIG_ON_VALUE_DATE (ID,CURRENCY,CLEARING_MECHANISM,CLEARING_PERIOD,THRESHOLD_AMOUNT,CHARGE) values (1,'GBP','BACS',3,10000000000,0)
/
Insert into ROUTING_CONFIG_ON_VALUE_DATE (ID,CURRENCY,CLEARING_MECHANISM,CLEARING_PERIOD,THRESHOLD_AMOUNT,CHARGE) values (2,'GBP','FPS', 1,10000,5)
/
Insert into ROUTING_CONFIG_ON_VALUE_DATE (ID,CURRENCY,CLEARING_MECHANISM,CLEARING_PERIOD,THRESHOLD_AMOUNT,CHARGE) values (3,'GBP','CHAPS',1,10000000000,20)
/
Insert into ROUTING_CONFIG_ON_VALUE_DATE (ID,CURRENCY,CLEARING_MECHANISM,CLEARING_PERIOD,THRESHOLD_AMOUNT,CHARGE) values (4,'USD','NACHA',3,3000,0)
/
Insert into ROUTING_CONFIG_ON_VALUE_DATE (ID,CURRENCY,CLEARING_MECHANISM,CLEARING_PERIOD,THRESHOLD_AMOUNT,CHARGE) values (5,'USD','FEDWIRE',1,10000000000,50)
/

Insert into REMITTANCE_ACCOUNT_CONFIG (ID,CLEARING_MECHANISM,BANK_ID,BANK_NAME,ACCOUNT_NUMBER,ACCOUNT_HOLDER_NAME,ACCOUNT_HOLDER_ADDR_LINE1,ACCOUNT_HOLDER_CITY,ACCOUNT_HOLDER_STATE,ACCOUNT_HOLDER_ZIP) values (1,'BACS','123489','BoE','89562374','BACS','21 Vale Drive','London','London','LO6 567')
/
Insert into REMITTANCE_ACCOUNT_CONFIG (ID,CLEARING_MECHANISM,BANK_ID,BANK_NAME,ACCOUNT_NUMBER,ACCOUNT_HOLDER_NAME,ACCOUNT_HOLDER_ADDR_LINE1,ACCOUNT_HOLDER_CITY,ACCOUNT_HOLDER_STATE,ACCOUNT_HOLDER_ZIP) values (2,'FPS','456989','BoE','89562374','FPS','21 Vale Drive','London','London','LO6 567')
/
Insert into REMITTANCE_ACCOUNT_CONFIG (ID,CLEARING_MECHANISM,BANK_ID,BANK_NAME,ACCOUNT_NUMBER,ACCOUNT_HOLDER_NAME,ACCOUNT_HOLDER_ADDR_LINE1,ACCOUNT_HOLDER_CITY,ACCOUNT_HOLDER_STATE,ACCOUNT_HOLDER_ZIP) values (3,'CHAPS','562389','BoE','89562374','CHAPS','21 Vale Drive','London','London','LO6 567')
/
Insert into REMITTANCE_ACCOUNT_CONFIG (ID,CLEARING_MECHANISM,BANK_ID,BANK_NAME,ACCOUNT_NUMBER,ACCOUNT_HOLDER_NAME,ACCOUNT_HOLDER_ADDR_LINE1,ACCOUNT_HOLDER_CITY,ACCOUNT_HOLDER_STATE,ACCOUNT_HOLDER_ZIP) values (4,'FEDWIRE','895689','BoNY','78562374','FEDWIRE','52 Times Square','NewYork','NewYork','T67865')
/
Insert into REMITTANCE_ACCOUNT_CONFIG (ID,CLEARING_MECHANISM,BANK_ID,BANK_NAME,ACCOUNT_NUMBER,ACCOUNT_HOLDER_NAME,ACCOUNT_HOLDER_ADDR_LINE1,ACCOUNT_HOLDER_CITY,ACCOUNT_HOLDER_STATE,ACCOUNT_HOLDER_ZIP) values (5,'NACHA','235689','BoNY','41562374','NACHA','52 Times Square','NewYork','NewYork','T67865')
/

Insert into OUTPUT_INS_BATCHING_CONFIG (ID,CLEARING_MECHANISM,THRESHOLD_COUNT,THRESHOLD_AMOUNT) values (1,'BACS',1,10000000000)
/
Insert into OUTPUT_INS_BATCHING_CONFIG (ID,CLEARING_MECHANISM,THRESHOLD_COUNT,THRESHOLD_AMOUNT) values (2,'FPS',1,10000000000)
/
Insert into OUTPUT_INS_BATCHING_CONFIG (ID,CLEARING_MECHANISM,THRESHOLD_COUNT,THRESHOLD_AMOUNT) values (3,'CHAPS',1,10000000000)
/
Insert into OUTPUT_INS_BATCHING_CONFIG (ID,CLEARING_MECHANISM,THRESHOLD_COUNT,THRESHOLD_AMOUNT) values (4,'FEDWIRE',1,10000000000)
/
Insert into OUTPUT_INS_BATCHING_CONFIG (ID,CLEARING_MECHANISM,THRESHOLD_COUNT,THRESHOLD_AMOUNT) values (5,'NACHA',1,10000000000)
/
