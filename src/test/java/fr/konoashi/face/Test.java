package fr.konoashi.face;

import fr.konoashi.face.event.EventManager;

public class Test {
    public static void main(String[] args) {
        Face face = new Face(25569, "abeabe", 1);
        EventManager.register(new Event());
        face.run();
    }
}
