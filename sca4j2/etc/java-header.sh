#!/bin/bash
for i in `find . -name *.java`
do
  cp $i $i.old
  cat java-header $i.old > $i
  rm $i.old 
done
