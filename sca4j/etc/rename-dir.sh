#!/bin/bash
for i in `find ../modules/trunk -name f3.properties`
do
  j=${#i}
  k=${i:0:$j-13}sca4j.properties
  echo $k
  svn rename $i $k
done
