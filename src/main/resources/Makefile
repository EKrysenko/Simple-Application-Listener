default:
	javac CallingCOBOL.java;
	javah CallingCOBOL;
	mv CallingCOBOL.h adaptor.h;
	gcc -shared -fPIC adaptor.c -I/usr/lib/jvm/java-8-oracle/include/linux -I/usr/lib/jvm/java-8-oracle/include -lcob -olibadaptor.so;
	cobc -fimplicit-init sub-cob.cbl -free -olibsub-cob.so;
	
clean:
	rm CallingCOBOL.class;
	rm adaptor.h;
	rm libadaptor.so;
	rm libsub-cob.so;
