## CHIP-8 Emulator


> CHIP-8 is an interpreted programming language, developed by Joseph Weisbecker. It was initially used on the COSMAC VIP and Telmac 1800 8-bit microcomputers in the mid-1970s. CHIP-8 programs are run on a CHIP-8 virtual machine. It was made to allow video games to be more easily programmed for these computers.

Details about the system and how opcodes are mapped can be found on [Wikipedia](https://en.wikipedia.org/wiki/CHIP-8) or [here](http://devernay.free.fr/hacks/chip8/C8TECH10.HTM) for more tehnical details.


### Prerequisites
```
Apache Maven
```
### Installing & Running - from CLI
Run:
```
mvn package
```
Followed by:
```
java -jar target/gs-maven-0.1.0.jar
```

### `Game Modes`

* **Normal mode** - 
Just the usual Sudoku experience 
* **Hard mode** - 
Less initial filled squares
* **5 Mistakes Mode** - 
Allows user to do up to 5 mistakes per game




Game Screen           |  Settings Screen
:-------------------------:|:-------------------------:
![](https://github.com/Nurckye/le-monde-de-sudoku/blob/master/githubMedia/sudokuMain.png)  |  ![](https://github.com/Nurckye/le-monde-de-sudoku/blob/master/githubMedia/sudokuSettings.png)




