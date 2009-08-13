#!/bin/bash
for i in `find .. -name LICENSE.txt`
do
  cp LICENSE.txt $i 
done
