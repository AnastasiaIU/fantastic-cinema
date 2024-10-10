package org.example.ui;

import javafx.scene.control.Menu;
import org.example.model.User;

public interface InitializableMenu {
    void initialize(User currentUser, Menu clickedMenu);
}
