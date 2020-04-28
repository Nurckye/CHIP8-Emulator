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

### `Space Invaders`
![](https://github.com/Nurckye/CHIP8-Emulator/blob/master/media/spaceinvaders.png) 

### `Pong`
![](https://github.com/Nurckye/CHIP8-Emulator/blob/master/media/pong.png) 







