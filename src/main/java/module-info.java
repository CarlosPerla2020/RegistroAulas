module edu.ufg.aulas.registroaulas {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.base;
    
    // Permisos para Hibernate y SQL
    requires jakarta.persistence;
    requires java.sql;
    requires java.naming;
    requires org.hibernate.orm.core;

    opens edu.ufg.aulas.registroaulas to javafx.fxml;
    exports edu.ufg.aulas.registroaulas;
    
    opens edu.ufg.aulas.controladores to javafx.fxml;
    
    // Permiso para que Hibernate pueda leer tus entidades
    opens edu.ufg.aulas.entidades to org.hibernate.orm.core;
    exports edu.ufg.aulas.entidades;
}