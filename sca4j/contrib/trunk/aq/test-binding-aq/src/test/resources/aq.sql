BEGIN 
  sys.dbms_aqadm.create_queue_table ( 
    queue_table          => 'TESTER_Q'
    , queue_payload_type => 'SYS.AQ$_JMS_MESSAGE'
    , sort_list          => 'ENQ_TIME'
    , comment            => 'TEST QUEUE TABLE'
    , multiple_consumers => FALSE
    , message_grouping   => DBMS_AQADM.NONE
    , storage_clause     => 'TABLESPACE USERS
LOGGING'
    , compatible         => '8.1'
    , primary_instance   => '0'
    , secondary_instance => '0'); 
COMMIT;
END;
/

BEGIN 
  sys.dbms_aqadm.create_queue(
      queue_name     => 'RES_Q'
    , queue_table    => 'TESTER_Q'
    , queue_type     => sys.dbms_aqadm.NORMAL_QUEUE
    , max_retries    => '0'
    , retry_delay    => '0'
    , retention_time => '0'
    , comment        => '');
END;
/

BEGIN 
  sys.dbms_aqadm.start_queue(
    queue_name => 'RES_Q'
    , enqueue  => TRUE
    , dequeue  => TRUE);
END;
/

BEGIN 
  sys.dbms_aqadm.create_queue(
      queue_name     => 'REQ_Q'
    , queue_table    => 'TESTER_Q'
    , queue_type     => sys.dbms_aqadm.NORMAL_QUEUE
    , max_retries    => '0'
    , retry_delay    => '0'
    , retention_time => '0'
    , comment        => '');
END;
/

BEGIN 
  sys.dbms_aqadm.start_queue(
    queue_name => 'REQ_Q'
    , enqueue  => TRUE
    , dequeue  => TRUE);
END;
/
BEGIN 
  sys.dbms_aqadm.create_queue(
      queue_name     => 'FIRE_FORGET_Q'
    , queue_table    => 'TESTER_Q'
    , queue_type     => sys.dbms_aqadm.NORMAL_QUEUE
    , max_retries    => '0'
    , retry_delay    => '0'
    , retention_time => '0'
    , comment        => '');
END;
/

BEGIN 
  sys.dbms_aqadm.start_queue(
    queue_name => 'FIRE_FORGET_Q'
    , enqueue  => TRUE
    , dequeue  => TRUE);
END;
/