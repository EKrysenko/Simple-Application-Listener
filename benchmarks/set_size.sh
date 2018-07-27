#!/usr/bin/env bash
for sizes in 1 2 3 4 5 6 7 8 9 10 11 12 13
do
  if [ ${sizes} = 1 ]; then
    lowSize="1000"
    highSize="1000"
  elif [ ${sizes} = 2 ]; then
    lowSize="5000"
    highSize="5000"
  elif [ ${sizes} = 3 ]; then
    lowSize="10000"
    highSize="10000"
  elif [ ${sizes} = 4 ]; then
    lowSize="20000"
    highSize="20000"
  elif [ ${sizes} = 5 ]; then
    lowSize="40000"
    highSize="40000"
  elif [ ${sizes} = 6 ]; then
    lowSize="65000"
    highSize="65000"
  elif [ ${sizes} = 7 ]; then
    lowSize="1000"
    highSize="65000"
  elif [ ${sizes} = 8 ]; then
    lowSize="5000"
    highSize="40000"
  elif [ ${sizes} = 9 ]; then
    lowSize="10000"
    highSize="20000"
  elif [ ${sizes} = 10 ]; then
    lowSize="1000"
    highSize="5000"
  elif [ ${sizes} = 11 ]; then
    lowSize="5000"
    highSize="10000"
  elif [ ${sizes} = 12 ]; then
    lowSize="20000"
    highSize="40000"
  elif [ ${sizes} = 13 ]; then
    lowSize="40000"
    highSize="65000"
  else
    lowSize="1000"
    highSize="65000"
  fi
  $(pwd)/start_containers.sh "$lowSize" "$highSize" "$1"
done
