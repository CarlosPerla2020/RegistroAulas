module edu.ufg.aulas.registroaulas {
    requires javafx.controls;
    requires javafx.fxml;

    opens edu.ufg.aulas.registroaulas to javafx.fxml;
    exports edu.ufg.aulas.registroaulas;
}
