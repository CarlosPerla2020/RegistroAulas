package edu.ufg.aulas.controladores;

import edu.ufg.aulas.entidades.Usuario;
import edu.ufg.aulas.util.HibernateUtil;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import org.hibernate.Session;
import org.hibernate.query.Query;
import java.security.MessageDigest;

public class LoginController {

    @FXML private TextField txtCorreo;
    @FXML private PasswordField txtClave;
    @FXML private Label lblMensaje;

    @FXML
    private void iniciarSesion(ActionEvent event) {
        String correo = txtCorreo.getText();
        String clave = txtClave.getText();

        if (correo == null || correo.trim().isEmpty() || clave == null || clave.trim().isEmpty()) {
            lblMensaje.setText("Ey, por favor llena todos los campos.");
            lblMensaje.setStyle("-fx-text-fill: red;");
            return;
        }

        String claveMD5 = convertirAMD5(clave);
        
        // Mensaje de carga en color azul
        lblMensaje.setText("Conectando a la base de datos... Espera un momento.");
        lblMensaje.setStyle("-fx-text-fill: blue;");

        // Creamos un hilo secundario para no congelar la pantalla
        new Thread(() -> {
            try (Session session = HibernateUtil.getSessionFactory().openSession()) {
                Query<Usuario> query = session.createQuery("FROM Usuario WHERE correo = :correo AND clave = :clave", Usuario.class);
                query.setParameter("correo", correo);
                query.setParameter("clave", claveMD5);
                
                Usuario usuario = query.uniqueResult();
                
                // Platform.runLater devuelve el control a la pantalla para actualizar los textos o cambiar de vista
                Platform.runLater(() -> {
                    if (usuario != null) {
                        lblMensaje.setText("¡Bienvenido!");
                        lblMensaje.setStyle("-fx-text-fill: green;");
                        try {
                            edu.ufg.aulas.registroaulas.App.setRoot("/vistas/home");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        lblMensaje.setText("Correo o contraseña incorrectos.");
                        lblMensaje.setStyle("-fx-text-fill: red;");
                    }
                });
            } catch (Exception e) {
                Platform.runLater(() -> {
                    lblMensaje.setText("Error al conectar con la base de datos (revisa la consola).");
                    lblMensaje.setStyle("-fx-text-fill: red;");
                });
                e.printStackTrace();
            }
        }).start();
    }

    private String convertirAMD5(String texto) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] array = md.digest(texto.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : array) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (Exception e) {
            return null;
        }
    }
}