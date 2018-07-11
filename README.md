### Data transfer time measuring branch

The application is created for measuring Data transfer performance through 
different protocols:
* TCP
* IPC (shared memory)
* HDD (host disk drive)

**The instruction is applied for linux-based system only.**

**To run application and measure data transfer time successfully:**

Prepare your environment:
* create named pipe (*mkfifo %filename%*)
* create empty text file at your HDD to use HDD-protocol
* fill property file "./config.properties" with paths for your environment

 Run two instances with arguments:
* TCP/SHM/HDD - as first argument to choose transfer protocol
* producer/consumer - as second argument to start supplier or receiver

**Both producer and consumer of the same transfer protocol 
are needed for successful measuring.**