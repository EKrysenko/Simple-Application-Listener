#!/usr/bin/env bash
for time in 1 2 3 4 5
do
    if [ ${time} = 1 ]; then
        seconds="10"
    elif [ ${time} = 2 ]; then
        seconds="60"
    elif [ ${time} = 3 ]; then
        seconds="120"
    elif [ ${time} = 4 ]; then
        seconds="600"
    elif [ ${time} = 5 ]; then
        seconds="36000"
	else
        seconds="1"
	fi
    $(pwd)/set_size.sh "$seconds"
done
