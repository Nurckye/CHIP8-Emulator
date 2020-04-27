
package chip8emulator;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.*;

class Keychecker extends KeyAdapter {
    private Hashtable<Character, Integer> key_transition = new Hashtable<Character, Integer>();
    private boolean[] seen_arr;

    Keychecker(boolean[] keys) {
        super();
        this.seen_arr = keys;

        key_transition.put('1', 0x1);
        key_transition.put('2', 0x2);
        key_transition.put('3', 0x3);
        key_transition.put('4', 0xC);
        key_transition.put('q', 0x4);
        key_transition.put('w', 0x5);
        key_transition.put('e', 0x6);
        key_transition.put('r', 0xD);
        key_transition.put('a', 0x7);
        key_transition.put('s', 0x8);
        key_transition.put('d', 0x9);
        key_transition.put('f', 0xE);
        key_transition.put('z', 0xA);
        key_transition.put('x', 0x0);
        key_transition.put('c', 0xB);
        key_transition.put('v', 0xF);
        Arrays.fill(this.seen_arr, false);
    }

    @Override
    public void keyPressed(KeyEvent event) {
        Chip8.wasKeyPressed = true;
        char ch = event.getKeyChar();
        if (event.getKeyCode() == KeyEvent.VK_ESCAPE)
            System.exit(0);
        if (this.key_transition.containsKey(ch))
            this.seen_arr[this.key_transition.get(ch)] = true;
    }

    @Override
    public void keyReleased(KeyEvent event) {
        char ch = event.getKeyChar();
        if (this.key_transition.containsKey(ch))
            this.seen_arr[this.key_transition.get(ch)] = false;
    }
}