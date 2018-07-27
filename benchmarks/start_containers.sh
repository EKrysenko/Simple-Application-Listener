#!/usr/bin/env bash
shm="/dev/shm"
lowSize=$1
highSize=$2
seconds=$3
steps=21

execute_test() {
	printf '%s\n' "$type benchmarks $seconds for packages from $lowSize to $highSize"
	echo "$type benchmarks $seconds for packages from $lowSize to $highSize" >> ${type}_times_${seconds}sec_${lowSize}_${highSize}
	consumerCont=$( docker ps -a | grep ${type}_consumer )
	producerCont=$( docker ps -a | grep ${type}_producer )
	if [ "$consumerCont" = "" ]; then
	    docker run --name ${type}_consumer --${network} host -v ${shm}:${shm} listener ${type} consumer ${lowSize} ${lowSize} ${seconds} >/dev/null &
		docker run --name ${type}_producer --${network} host -v ${shm}:${shm} listener ${type} producer ${lowSize} ${lowSize} ${seconds} >/dev/null
		docker wait ${type}_producer >/dev/null
		j=1
		while [ ${j} -lt ${steps} ]
		do
			docker start ${type}_producer >/dev/null
			docker wait ${type}_producer >/dev/null
			j=$[$j+1]
		done
		docker logs ${type}_producer >>${type}_times_${seconds}sec_${lowSize}_${highSize}
		docker rm -f ${type}_consumer >/dev/null
		docker rm -f ${type}_producer >/dev/null
	fi
}

for protocol in 1 2
do
	if [ ${protocol} = 1 ]; then
		type="SHM"
		network="ipc"
	else
		type="TCP"
		network="net"
	fi
	execute_test
done
