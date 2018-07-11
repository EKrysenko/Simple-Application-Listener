### Data transfer time measuring branch

The application is created for measuring Data transfer performance through 
different protocols:
* TCP
* IPC (shared memory)
* HDD (host disk drive)

**The instruction is applied for linux-based system only.**

**To run application and measure data transfer time successfully:**

Prepare your environment:

* make sure that Docker is installed on your computer: 

    `docker -v`
* create named pipe somewhere on your computer to use SHM and HDD protocols: 

    `mkfifo <pipename>`
* create empty buffer text file in any folder ("results" folder) to use HDD protocol: 

    `touch <buffername>`
* create empty text file in the "results" folder for producer to write received data into: 

    `touch <receivedname>`
* unzip send.zip archive file from 'benchmarks' folder to the "results" folder: 

    `unzip send.zip`
* place start.sh file in the "results" folder
* fill property file "./config.properties" with your paths to pipename, buffername, receivedname and send.txt files
* replace "shm", "pipe", "results" and "steps" variables values with your own in start.sh script

Build Docker image:
* create listener-0.1.jar artifact file
* put Dockerfile, listener-0.1.jar and config.properties in the one folder
* build Docker image: run the following command from that folder `docker build --tag listener .`

Run tests:
* change directory to "results" folder: `cd ~/results`
* run start.sh script: `./start.sh`

Some useful docker commands:
* Run Docker container with SHM consumer:

    `docker run --name ipc_consumer --ipc host 
    -v <path_to_/dev/shm/>:<path_to_/dev/shm/> 
    -v <path_to_pipe_file>:<path_to_pipe_file> 
    listener SHM consumer`
   
* Run Docker container with SHM producer:

    `docker run --name ipc_producer --ipc host 
    -v <path_to_/dev/shm/>:<path_to_/dev/shm/> 
    -v <path_to_pipe_file>:<path_to_pipe_file> 
    -v <path_to_results_folder>:<path_to_results_folder>
    listener SHM producer`
    
* Run Docker container with TCP consumer:

    `docker run --name tcp_consumer --net host 
    -v <path_to_results_folder>:<path_to_results_folder>
    listener TCP consumer`

* Run Docker container with TCP producer:

    `docker run --name tcp_producer --net host 
    -v <path_to_results_folder>:<path_to_results_folder>
    listener TCP producer`

* Run Docker container with HDD consumer:

    `docker run --name hdd_consumer 
    -v <path_to_pipe_file>:<path_to_pipe_file> 
    -v <path_to_results_folder>:<path_to_results_folder>
    listener HDD consumer`

* Run Docker container with HDD producer:

    `docker run --name hdd_producer 
    -v <path_to_pipe_file>:<path_to_pipe_file> 
    -v <path_to_results_folder>:<path_to_results_folder>
    listener HDD producer`

**Both producer and consumer of the same transfer protocol 
are needed for successful measuring.**
**Consumer must be launched first.**