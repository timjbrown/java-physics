package controller;

import model.World;
import view.Window;

public class Runner {
    public static void main(String[] args) {
        Window window = new Window();
        World world = new World();
        Controller controller = new Controller(window, world);

        window.pack();
        window.setVisible(true);
    }
}
