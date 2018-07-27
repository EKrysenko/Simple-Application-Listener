### Data transfer time measuring branch

The application is created for measuring Data transfer performance through 
different protocols:

* TCP  
* IPC (shared memory)  

**The instruction is applied for linux-based system only.**

**To run application and measure data transfer time successfully:**

Prepare your environment:

* make sure that Docker is installed on your computer:  
    `docker -v`

* create named pipe 'sharedMemory' somewhere on your computer to use SHM and HDD protocols:  
    `mkfifo sharedMemory`
                
* fill property file "./config.properties" with your paths to named pipe name

Build Docker image:

* create listener-0.1-jar-with-dependencies.jar artifact file

* put Dockerfile, listener-0.1-jar-with-dependencies.jar and config.properties in the same folder

* build Docker image: run the following command from that folder  
    `docker build --tag listener .`

Run tests:

* run start_testing.sh script:  
    `./start_testing.sh`

**Number of seconds, package sizes and number of steps is set in .sh scripts**

Some useful docker commands:

* Run Docker container with SHM consumer:    
    `docker run --name SHM_consumer --ipc host 
    -v <path_to_/dev/shm/>:<path_to_/dev/shm/> 
    listener SHM consumer <smallest package size> <biggest package size> <seconds to run test>`
   
* Run Docker container with SHM producer:  
    `docker run --name SHM_producer --ipc host 
    -v <path_to_/dev/shm/>:<path_to_/dev/shm/> 
    listener SHM producer <smallest package size> <biggest package size> <seconds to run test>`
    
* Run Docker container with TCP consumer:  
    `docker run --name TCP_consumer --net host 
    listener TCP consumer <smallest package size> <biggest package size> <seconds to run test>` 

* Run Docker container with TCP producer:  
    `docker run --name TCP_producer --net host 
    listener TCP producer <smallest package size> <biggest package size> <seconds to run test>`


**Both producer and consumer of the same transfer protocol 
are needed for successful measuring.**
**Consumer must be launched first.**