#!/bin/bash
for i in `find .. -name NOTICE.txt`
do
  echo $i
  cp NOTICE.txt $i
done
