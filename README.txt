Readme - SwingLabs SwingX Project - http://swingx.dev.java.net


SwingX is a library of components and utilities extending the Java Swing library; read more at our website, 
http://swingx.dev.java.net, and Wiki page, http://wiki.java.net/bin/view/Javadesktop/SwingLabsSwingX


Getting the Latest Source
=========================

1) Check out the lastest code
Download the latest release from our SVN repository; full instructions are at
https://swingx.dev.java.net/servlets/ProjectSource

Building the Source
===================
SwingX relies on Maven for controlling compilation, building docs, testing, etc. You can use our POM files to build the project, some IDEs can directly invoke Maven for you.

To compile from the command line, you'll need to have Apache Maven 3.x installed; see http://maven.apache.org. 

You can build SwingX by going to the command line and typing
mvn package

That should be it--this will test and build swingx.jar in the target directory. 
