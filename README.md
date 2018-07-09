### This is oprhan dev_ops branch of project

The application is created for measuring Data transfer performance through 
different protocols:
* TCP
* IPC (shared memory)
* HDD (host disk drive)

**To run application and measure data transfer time successfully:**

1. Prepare your environment:
* supply text file you want to send, set path at Constants
* create named pipe (*mkfifo %filename%*), set path at Constants
* create buffer file at your HDD to use HDD-protocol

2. Run two instances with arguments:
* TCP/SHM/HDD - as first argument to choose transfer protocol
* producer/consumer - as second argument to start supplier or receiver

**Both producer and consumer of the same transfer protocol 
are needed for successful measuring.**