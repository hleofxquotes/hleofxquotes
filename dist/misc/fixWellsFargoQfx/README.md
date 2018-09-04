A java program to fix the Wells Fargo QFX missing end-of-line problem
  (around end of Aug 2018)

### Install

  You will need java installed.

  Download the zip file and unzip to get the jar file.

### To run

* Either double-click on the jar file
* Or from command-line
```
java -jar fixWellsFargoQfx-Build_20180902_10-exec.jar
```
 
The drag and drop the QFX file onto the tool window (target image).
Tool will process the content and convert the content to a new file that you can use to import.
The new file with a word '-fix' will be saved into the same folder as the original file. For example:

* Origin file is /home/user/Downloads/Checking1.qfx
* New file is /home/user/Downloads/Checking1-fix.qfx

After a succesful conversion, you will be prompted if you would like to 'open' the new file. 
Assuming that you have setup to have your personal finance tool to import of type QFX, 'open' the new file
will automatically import the converted.