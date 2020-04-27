package chip8emulator;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class Chip8 {
    private static Chip8 single_instance = null;

    private short opcode;
    // 4k memory
    private byte[] memory;
    // 15 8-bit general purpose registers, 16th for the carry flag
    private byte[] V = new byte[16];
    // Index register
    private short I;
    private short pc;
    // 64x32 screen, black and white
    public boolean[][] gfx = new boolean[32][64];
    private byte delay_timer;
    private byte sound_timer;
    private short[] stack;
    private short sp;
    public boolean[] key = new boolean[16];
    public boolean drawFlag = true;
    public static boolean wasKeyPressed = false;

    public static int getRandomIntegerBetweenRange(int min, int max) {
        return (int) (Math.random() * ((max - min) + 1)) + min;
    }

    // constructor
    private Chip8() {
        this.pc = 0x200; // Program counter starts at 0x200
        this.opcode = 0; // Reset current opcode
        this.I = 0; // Reset index register
        this.sp = 0; // Reset stack pointer
        this.memory = new byte[4096];
        this.stack = new short[16];
        short[] chip8_fontset = { 0xF0, 0x90, 0x90, 0x90, 0xF0, // 0
                0x20, 0x60, 0x20, 0x20, 0x70, // 1
                0xF0, 0x10, 0xF0, 0x80, 0xF0, // 2
                0xF0, 0x10, 0xF0, 0x10, 0xF0, // 3
                0x90, 0x90, 0xF0, 0x10, 0x10, // 4
                0xF0, 0x80, 0xF0, 0x10, 0xF0, // 5
                0xF0, 0x80, 0xF0, 0x90, 0xF0, // 6
                0xF0, 0x10, 0x20, 0x40, 0x40, // 7
                0xF0, 0x90, 0xF0, 0x90, 0xF0, // 8
                0xF0, 0x90, 0xF0, 0x10, 0xF0, // 9
                0xF0, 0x90, 0xF0, 0x90, 0x90, // A
                0xE0, 0x90, 0xE0, 0x90, 0xE0, // B
                0xF0, 0x80, 0x80, 0x80, 0xF0, // C
                0xE0, 0x90, 0x90, 0x90, 0xE0, // D
                0xF0, 0x80, 0xF0, 0x80, 0xF0, // E
                0xF0, 0x80, 0xF0, 0x80, 0x80 // F
        };
        for (int i = 0; i < 80; ++i)
            this.memory[i] = (byte) chip8_fontset[i];
    }

    // singleton pattern
    public static Chip8 initialize() {
        if (single_instance == null)
            single_instance = new Chip8();
        return single_instance;
    }

    // loads game in memory from file
    public void load_game(String game_path) {
        File file = new File(game_path);
        try {
            int i = 512;
            byte[] res = Files.readAllBytes(file.toPath());
            for (byte b : res)
                this.memory[i++] = b;

        } catch (IOException ie) {
            ie.printStackTrace();
            System.exit(0);
        }
    }

    // loads the next opcode
    private void load_opcode() {
        this.opcode = (short) (this.memory[pc] << 8 | (this.memory[pc + 1] & 0x00FF));
        // System.out.printf("0x%02X\n", this.opcode);
    }

    // emulates one cycle
    public void emulate_cycle() {
        // Fetch
        this.load_opcode();
        // Decode
        int x, y;
        byte N, NN;
        switch (this.opcode & 0xF000) {
            case 0x0000:
                switch (this.opcode & 0x000F) {
                    case 0x0000: // 0x00E0: Clears the screen
                        // Execute opcode
                        for (int i = 0; i < 32; ++i)
                            for (int j = 0; j < 64; ++j)
                                this.gfx[i][j] = false;
                        this.pc += 2;
                        break;

                    case 0x00E: // 0x00EE: Returns from subroutine
                        --this.sp;
                        this.pc = this.stack[this.sp];
                        this.pc += 2;
                        break;
                }
                break;

            case 0x1000: // 1NNN: Jumps to address NNN
                pc = (short) (this.opcode & 0x0FFF);
                break;

            case 0x2000: // 2NNN Calls the subroutine at address NNN
                this.stack[sp] = pc;
                ++sp;
                pc = (short) (this.opcode & 0x0FFF);
                break;

            case 0x3000: // 3XNN Skip the following instruction if the value of register VX equals NN
                x = (this.opcode & 0x0F00) >> 8;
                NN = (byte) (this.opcode & 0x00FF);
                if (this.V[x] == NN)
                    pc += 4;
                else
                    pc += 2;
                break;

            case 0x4000: // 4XNN Skip the following instruction if the value of reg VX doesn't equal NN
                x = (this.opcode & 0x0F00) >> 8;
                NN = (byte) (this.opcode & 0x00FF);
                if (this.V[x] == NN)
                    pc += 2;
                else
                    pc += 4;
                break;

            case 0x5000: // 5XY0 Skips the next instruction if VX equals VY
                x = (this.opcode & 0x0F00) >> 8;
                y = (this.opcode & 0x00F0) >> 4;
                if (this.V[x] == this.V[y])
                    pc += 4;
                else
                    pc += 2;
                break;

            case 0x6000: // 6XNN Sets VX to NN
                x = (this.opcode & 0x0F00) >> 8;
                NN = (byte) (this.opcode & 0x00FF);
                this.V[x] = NN;
                pc += 2;
                break;

            case 0x7000: // 7XNN Adds NN to VX. (Carry flag is not changed)
                x = (this.opcode & 0x0F00) >> 8;
                NN = (byte) (this.opcode & 0x00FF);
                this.V[x] += NN;
                pc += 2;
                break;

            case 0x8000:
                switch (this.opcode & 0x000F) {
                    case 0x0000: // 8XY0 Sets VX to the value of VY.
                        x = (this.opcode & 0x0F00) >> 8;
                        y = (this.opcode & 0x00F0) >> 4;
                        this.V[x] = this.V[y];
                        pc += 2;
                        break;
                    case 0x0001: // 8XY1 Sets VX to VX or VY. (Bitwise OR operation)
                        x = (this.opcode & 0x0F00) >> 8;
                        y = (this.opcode & 0x00F0) >> 4;
                        this.V[x] = (byte) (this.V[x] | this.V[y]);
                        pc += 2;
                        break;
                    case 0x0002: // 8XY2 Sets VX to VX and VY. (Bitwise AND operation)
                        x = (this.opcode & 0x0F00) >> 8;
                        y = (this.opcode & 0x00F0) >> 4;
                        this.V[x] = (byte) (this.V[x] & this.V[y]);
                        pc += 2;
                        break;
                    case 0x0003: // 8XY3 Sets VX to VX xor VY.
                        x = (this.opcode & 0x0F00) >> 8;
                        y = (this.opcode & 0x00F0) >> 4;
                        this.V[x] = (byte) (this.V[x] ^ this.V[y]);
                        pc += 2;
                        break;
                    case 0x0004: // 8XY4 Adds VY to VX. VF is set to 1 when there's a carry, and to 0 when there
                                 // isn't.
                        x = (this.opcode & 0x0F00) >> 8;
                        y = (this.opcode & 0x00F0) >> 4;
                        if (this.V[x] > (byte) 255 - this.V[y])
                            this.V[0xF] = 1;
                        else
                            this.V[0xF] = 0;
                        this.V[x] += this.V[y];
                        pc += 2;
                        break;
                    case 0x0005: // 8XY5 VY is subtracted from VX. VF is set to 0 when there's a borrow, and 1
                                 // when there isn't.
                        x = (this.opcode & 0x0F00) >> 8;
                        y = (this.opcode & 0x00F0) >> 4;
                        if (this.V[x] > this.V[y]) // vx - vy > 0 no borrow
                            this.V[0xF] = 1;
                        else
                            this.V[0xF] = 0;
                        this.V[x] -= this.V[y];
                        pc += 2;
                        break;
                    case 0x0006: // 8XY6 Stores the least significant bit of VX in VF and then shifts VX to the
                                 // right by 1.
                        x = (this.opcode & 0x0F00) >> 8;
                        this.V[0xF] = (byte) (this.V[x] & 0x1);
                        this.V[x] >>= 1;
                        pc += 2;
                        break;
                    case 0x0007: // 8XY7 Sets VX to VY minus VX. VF is set to 0 when there's a borrow, and 1 when
                                 // there isn't.
                        x = (this.opcode & 0x0F00) >> 8;
                        y = (this.opcode & 0x00F0) >> 4;
                        if (this.V[x] > this.V[y]) // vx - vy > 0 no borrow
                            this.V[0xF] = 0;
                        else
                            this.V[0xF] = 1;
                        this.V[x] = (byte) (this.V[y] - this.V[x]);
                        pc += 2;
                        break;
                    case 0x000E: // 8XYE Stores the most significant bit of VX in VF and then shifts VX to the
                                 // left by 1.
                        x = (this.opcode & 0x0F00) >> 8;
                        this.V[0xF] = (byte) ((this.V[x] >> 7) & 0x01);
                        this.V[x] <<= 1;
                        pc += 2;
                        break;
                }
                break;

            case 0x9000:
                x = (this.opcode & 0x0F00) >> 8;
                y = (this.opcode & 0x00F0) >> 4;
                if (this.V[x] != this.V[y])
                    pc += 4;
                else
                    pc += 2;
                break;

            case 0xA000: // ANNN: Sets I to the address NNN
                this.I = (short) (this.opcode & 0x0FFF);
                pc += 2;
                break;

            case 0xB000: // BNNN Jumps to the address NNN plus V0.
                pc = (short) (this.V[0] + (this.opcode & 0x0FFF));
                break;

            case 0xC000: // CXNN Sets VX to the result of a bitwise and operation on a random number
                         // (Typically: 0 to 255) and NN.
                x = (this.opcode & 0x0F00) >> 8;
                int rv = getRandomIntegerBetweenRange(0, 255);
                this.V[x] = (byte) (rv & (this.opcode & 0x00FF));
                pc += 2;
                break;

            case 0xD000: // DXYN Draws a sprite at coordinate (VX, VY) that has a width of 8 pixels and a
                         // height of N pixels
                x = (this.opcode & 0x0F00) >> 8;
                y = (this.opcode & 0x00F0) >> 4;
                N = (byte) (this.opcode & 0x000F);

                byte curr;
                int posX, posY;
                for (byte i = 0; i < N; ++i) {
                    // System.out.println(this.I);
                    curr = this.memory[this.I + i];

                    for (int j = 0; j < 8; ++j) {
                        if ((curr & (0x80 >> j)) != 0) {
                            posY = (this.V[y] + i) % 32;
                            posX = (this.V[x] + j) % 64;

                            if (this.gfx[posY][posX])
                                this.V[0xF] = 1;
                            this.gfx[posY][posX] = !this.gfx[posY][posX];
                        }
                    }
                }
                this.drawFlag = true;
                pc += 2;
                break;
            case 0xE000:
                x = (this.opcode & 0x0F00) >> 8;
                switch (this.opcode & 0x00FF) {
                    case 0x009E: // EX9E Skips the next instruction if the key stored in VX is pressed.
                        // for (boolean val : this.key) {
                        // System.out.print(val);
                        // System.out.print(" ");
                        // }
                        // System.out.print("\n");
                        if (this.key[this.V[x]])
                            pc += 4;
                        else
                            pc += 2;
                        break;
                    case 0x00A1: // EX9E Skips the next instruction if the key stored in VX is not pressed.
                        if (this.key[this.V[x]])
                            pc += 2;
                        else
                            pc += 4;
                        break;
                }
                break;
            case 0xF000:
                x = (this.opcode & 0x0F00) >> 8;
                switch (this.opcode & 0x00FF) {
                    case 0x0007: // FX07 Sets VX to the value of the delay timer.
                        this.V[x] = this.delay_timer;
                        pc += 2;
                        break;
                    case 0x000A: // FX0A *Blocking Operation* A key press is awaited, and then stored in VX.
                        if (!Chip8.wasKeyPressed)
                            return;
                        Chip8.wasKeyPressed = false;
                        pc += 2;
                        break;
                    case 0x0015: // FX15 Sets the delay timer to VX
                        this.delay_timer = this.V[x];
                        pc += 2;
                        break;
                    case 0x0018: // FX18 Sets the sound timer to VX
                        this.sound_timer = this.V[x];
                        pc += 2;
                        break;
                    case 0x001E: // FX1E Adds VX to I. VF is set to 1 when there is a range overflow
                                 // (I+VX>0xFFF), and to 0 when there isn't.
                        this.I += this.V[x];
                        pc += 2;
                        break;
                    case 0x0029: // FX29 Sets I to the location of the sprite for the character in VX.
                        this.I = (short) (Byte.toUnsignedInt(this.V[x]) * 5);
                        pc += 2;
                        break;
                    case 0x0033: // FX33 Stores the binary-coded decimal representation of VX, with the most
                                 // significant of three digits at the address in I, the middle digit at I plus
                                 // 1, and the least significant digit at I plus 2. (In other words, take the
                                 // decimal representation of VX, place the hundreds digit in memory at location
                                 // in I, the tens digit at location I+1, and the ones digit at location I+2.)
                        this.memory[this.I] = (byte) (this.V[x] / 100);
                        this.memory[this.I + 1] = (byte) ((this.V[x] / 10) % 10);
                        this.memory[this.I + 2] = (byte) ((this.V[x] % 100) % 10);
                        pc += 2;
                        break;
                    case 0x0055: // Stores V0 to VX (including VX) in memory starting at address I. The offset
                                 // from I is increased by 1 for each value written, but I itself is left
                                 // unmodified.
                        for (int i = 0; i <= x; ++i)
                            this.memory[this.I + i] = this.V[i];
                        pc += 2;
                        break;
                    case 0x0065:
                        for (int i = 0; i <= x; ++i)
                            this.V[i] = this.memory[this.I + i];
                        pc += 2;
                        break;
                }
                break;

            default:
                System.out.println("Opcode not in list");
                System.exit(0);
                // more opcodesx //
        }

        if (this.delay_timer > 0)
            --this.delay_timer;

        if (this.sound_timer > 0) {
            if (this.sound_timer == 1) {
                Sound s = new Sound();
                s.start();
            }
            --this.sound_timer;
        }
    }
}