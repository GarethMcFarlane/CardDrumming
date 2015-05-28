Requirements:
-	Java 1.8
-	Ant


Compile Instructions

1.	In this directory, type 'ant' into a shell.  This will compile the files.



Run Instructions - Linux

1.	To calibrate the program, type './calibrate' into this directory.  Choose your object and hold it up to the camera.  Change the HSV sliders until you can only see the white threshold of your object.
	When you have these values, input them into HSV.txt and HSV2.txt respectively.  This allows for two different coloured objects to be detected.
2.	Once you have calibrated your two objects.  Close the program and type './run' in shell.  This will start the program.  Move your objects over the drums to start playing them.

Run Instructions - Windows

Navigate to the bin directory then:

For calibration: 	java -Djava.library.path=../lib CardDrumming calibrate
For running:		java -Djava.library.path=../lib CardDrumming run		

