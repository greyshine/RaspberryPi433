# RaspberryPi433

Simple  application for using raspberry pi with wireless socket on 433Mhz basis.
Using Java as language for building the server.

<img src="http://www.imageno.com/thumbs/20161112/loo1quk7z57a.jpg"/>

# prerequisites

* have a raspberry pi (I am using the model b) with java8 installed  
  https://www.raspberrypi.org/downloads/raspbian/  
  https://www.raspberrypi.org/forums/viewtopic.php?f=81&t=101543
* install some 433 Mhz hardware for sending signals and evaluating the right codes
* install some core libraries for sending codes to the devices
  I used:  
  http://wiringpi.com  
  http://github.com/ninjablocks/433Utils  
  by this german manual (worked like charme): http://tutorials-raspberrypi.de/raspberry-pis-ueber-433mhz-funk-kommunizieren-lassen/  
  this is my socket devices: Brennenstuhl Funkschalt-Set RCS 1000 N Comfort
  
# usage 

here the version 1.0 is used as example, runs on port 4711 and uses basic http authentification.

* download the jar from dist/FunksteckerRpi-1.0.jar folder 
* run `java -jar FunksteckerRpi-1.0.jar config.jar`
* put into your browser http://amfabe2:dikafe@127.0.0.1:4711
* switch your sockets...

file `config.jar` might look like this:

```
{
	"port" : 4711,
	"user":"amfabe2",
	"password":"dikafe",
	"codesend" : "/opt/433Utils/RPi_utils/codesend",
	"sendrepeats" : 4,
	"switches" : {
		"WZ-amSessel":{
			"code-0": 1361,
			"code-1": 1364
		},
		"WZ-LampeFenster":{
			"code-0": 1322,
			"code-1": 1345
		}
	}
}
```
switches declares the sockets. code-0 is the code to turn the socket on. code-0 turns off.
That makes is actually a 433Mhz codesending application.

# Raspberry cable connection to sender FS1000A

<img src="http://www.imageno.com/image.php?id=kx8me22654wb&kk=3597610361" />
