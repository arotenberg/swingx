# SwingX, salvaged

This repository contains a copy of the source code for the SwingX library. The code was downloaded
from the SwingX development SVN repository on java.net before Oracle acquired Sun and took that site
offline permanently. The code is therefore more recent than any released version of SwingX. It might
have had version number 1.5.6 had it ever been released.

The code was downloaded from https://svn.java.net/svn/swingx~svn/trunk. The downloaded commit was
SVN revision 4316 by user "kleopatra", made at 11:59:46 AM, Wednesday, October 09, 2013.

## Eclipse import and the SwingXSet demo program

The original SwingX source code included Maven pom.xml files, but did not include any development
environment configuration files. The build-swingx-demos branch of this repository adds Eclipse
project files so that the source code can be imported in Eclipse easily.

The build-swingx-demos branch also modifies the pom files to build the swingx-demos project, which
is disabled by default. swingx-demos contains SwingXSet, which is a useful demo program that shows
off various nonstandard Swing components added by SwingX, similar to what the SwingSet program does
for the base set of Swing components.

## The original SwingX readme

Readme - SwingLabs SwingX Project - http://swingx.dev.java.net


SwingX is a library of components and utilities extending the Java Swing library; read more at our website, 
http://swingx.dev.java.net, and Wiki page, http://wiki.java.net/bin/view/Javadesktop/SwingLabsSwingX


### Getting the Latest Source

1) Check out the lastest code
Download the latest release from our SVN repository; full instructions are at
https://swingx.dev.java.net/servlets/ProjectSource

### Building the Source

SwingX relies on Maven for controlling compilation, building docs, testing, etc. You can use our POM files to build the project, some IDEs can directly invoke Maven for you.

To compile from the command line, you'll need to have Apache Maven 3.x installed; see http://maven.apache.org. 

You can build SwingX by going to the command line and typing
mvn package

That should be it--this will test and build swingx.jar in the target directory. 
