package nl.inholland.controller;

import javafx.scene.control.Menu;
import nl.inholland.model.User;

public interface InitializableMenu {
    void initialize(User currentUser, Menu clickedMenu);
}
