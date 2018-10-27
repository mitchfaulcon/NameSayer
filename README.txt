=====================================================
          Welcome to the NameSayer README                 
=====================================================

This application was created along with GitHub user william-shin-387

WHAT IS NAMESAYER?

NameSayer is a pronunciation tool dedicated towards users who wish to practice the pronunciation of names.


TARGET USER

NameSayer is intended for lecturers who wish to learn the names of the students in their classes.


SPECIAL FEATURES

NameSayer's special features are:
> A Tutorial upon application launch
> A Settings page
> The ability to edit custom names entered in the practice list


REQUIREMENTS

Note that Java and the ffmpeg package is required for full functionality.

JAVA
Follow this link to download and install Java: https://www.java.com/en/download/

FFMPEG
To install FFmpeg onto your machine: type the following commands into the terminal:

> sudo add-apt-repository ppa:mc3man/trusty-media 
> sudo apt-get update 
> sudo apt-get install ffmpeg



RUNNING THE FILE


Please follow the following instructions on a Linux-based
machine to launch NameSayer:

1. Check that directory containing 'NameSayer.jar' also contains
	> Names (.wav database folder)
	> Resources (required for features of NameSayer)

2. Type the following command into the terminal:
------------------------------------------------------------------

<preferred directory address>$ java -jar NameSayer.jar

For example: ~/Desktop$ java -jar NameSayer.jar

------------------------------------------------------------------



Ensure that you have placed the .jar file in the same location
as where you want your name attempts and quality information to be saved.

Once NameSayer is started, several folders and files may appear in the directory.
These are:
	> The folder "Attempts" which will contain all of your attempts at names.
	> The text file "GOODCreations.txt" which will contain a list of filenames rated as 'good'.
	> The text file "BADCreations.txt" which will contain a list of filenames rated as 'bad'.

So in one directory there should be:
	-NameSayer.jar (executable jar file)
	-Names (.wav database folder)
	-Attempts (.wav attempts folder)
	-GOODCreations.txt (good quality filename list)
	-BADCreations.txt (bad quality filename list)
		




