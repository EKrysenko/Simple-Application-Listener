#!/usr/bin/env bash
#Default path to temporary file storage filesystem (tmpfs)
shm="/dev/shm"
#Path to named pipe. Used in SHM and HDD data transfer protocols for communication between processes
pipe="/lgi/FILE.in"
#Path to folder with start.sh, send_*.txt, buffer and received text files
results=$(pwd)
#Number of steps to launch tests
steps=5
for protocol in 1 2 3
do
	if [ $protocol = 1 ]; then
		type="ipc"
		mv send_0.txt send.txt
		docker run --name ${type}_consumer --ipc host -v $shm:$shm -v $pipe:$pipe listener SHM consumer >/dev/null &
		docker run --name ${type}_producer --ipc host -v $shm:$shm -v $pipe:$pipe -v $results:$results listener SHM producer >/dev/null
		wait
		mv send.txt send_0.txt
	elif [ $protocol = 2 ]; then
		type="tcp"
		mv send_0.txt send.txt
		docker run --name ${type}_consumer --net host -v $results:$results listener TCP consumer >/dev/null &
		docker run --name ${type}_producer --net host -v $results:$results listener TCP producer >/dev/null
		wait
		mv send.txt send_0.txt
	else
		type="hdd"
		mv send_0.txt send.txt
		docker run --name ${type}_consumer -v $pipe:$pipe -v $results:$results listener HDD consumer >/dev/null &
		docker run --name ${type}_producer -v $pipe:$pipe -v $results:$results listener HDD producer >/dev/null
		wait
		mv send.txt send_0.txt
	fi

	printf '%s\n' "$type benchmarks"
  for i in 0.015 0.025 0.1 0.5 1 50 100 150 200
	do
		filesize=$(wc -c <send_${i}.txt)
		printf '%s\n' --------------------
		printf '%s\n' "$filesize bytes"
		mv send_${i}.txt send.txt
		j=0
		while [ $j -lt $steps ]
		do
			docker start ${type}_consumer >/dev/null
			docker start ${type}_producer >/dev/null
			sleep 10
      j=$[$j+1]
		done
		mv send.txt send_${i}.txt
		printf '%s\n' --------------------
		docker logs ${type}_producer
		echo "" > $(docker inspect --format='{{.LogPath}}' ${type}_producer)
		printf '%s\n' --------------------
	done
done
