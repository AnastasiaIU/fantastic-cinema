package org.example.UI;

import javafx.scene.control.Menu;
import org.example.Model.User;

public interface InitializableMenu {
    void initialize(User currentUser, Menu clickedMenu);
}
