package edu.ufg.aulas.controladores;

import edu.ufg.aulas.entidades.Aula;
import edu.ufg.aulas.util.HibernateUtil;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

public class HomeController {

    @FXML private TextField txtNombre;
    @FXML private TextField txtEdificio;
    @FXML private ComboBox<Integer> cmbPlanta;
    @FXML private TextField txtCapacidad;
    @FXML private ComboBox<String> cmbTipoAula;
    @FXML private TextField txtResponsable;
    @FXML private ComboBox<String> cmbEstado;
    @FXML private Label lblMensaje;

    @FXML
    public void initialize() {
        // Llenamos los ComboBox rápido con los datos del PDF
        cmbPlanta.getItems().addAll(1, 2, 3, 4, 5);
        cmbTipoAula.getItems().addAll("Normal", "Centro de cómputo", "Laboratorio", "Especial");
        cmbEstado.getItems().addAll("Asignada", "Asignada temporalmente", "En mantenimiento");
    }

    @FXML
    private void guardarAula(ActionEvent event) {
        try {
            // Validación 1: Ningún campo vacío
            if (txtNombre.getText().isEmpty() || txtEdificio.getText().isEmpty() || 
                txtCapacidad.getText().isEmpty() || txtResponsable.getText().isEmpty() ||
                cmbPlanta.getValue() == null || cmbTipoAula.getValue() == null || cmbEstado.getValue() == null) {
                lblMensaje.setText("Error: Llena todos los campos.");
                lblMensaje.setStyle("-fx-text-fill: red;");
                return;
            }

            int capacidad = Integer.parseInt(txtCapacidad.getText());
            int planta = cmbPlanta.getValue();

            // Validación 2: Enteros mayores que 0
            if (capacidad <= 0 || planta <= 0) {
                lblMensaje.setText("Error: Planta y capacidad deben ser mayores a 0.");
                lblMensaje.setStyle("-fx-text-fill: red;");
                return;
            }

            try (Session session = HibernateUtil.getSessionFactory().openSession()) {
                // Validación 3: No duplicados (mismo nombre en mismo edificio)
                Query<Long> query = session.createQuery("SELECT COUNT(a) FROM Aula a WHERE a.nombre = :nom AND a.edificio = :edi", Long.class);
                query.setParameter("nom", txtNombre.getText());
                query.setParameter("edi", txtEdificio.getText());
                
                if (query.uniqueResult() > 0) {
                    lblMensaje.setText("Error: Ya existe esa aula en ese edificio.");
                    lblMensaje.setStyle("-fx-text-fill: red;");
                    return;
                }

                // Guardar en la base de datos
                Transaction tx = session.beginTransaction();
                Aula nuevaAula = new Aula();
                nuevaAula.setNombre(txtNombre.getText());
                nuevaAula.setEdificio(txtEdificio.getText());
                nuevaAula.setPlanta(planta);
                nuevaAula.setCapacidad(capacidad);
                nuevaAula.setTipoAula(cmbTipoAula.getValue());
                nuevaAula.setResponsable(txtResponsable.getText());
                nuevaAula.setEstado(cmbEstado.getValue());
                
                session.persist(nuevaAula);
                tx.commit();
                
                lblMensaje.setText("¡Aula guardada con éxito!");
                lblMensaje.setStyle("-fx-text-fill: green;");
                limpiarFormulario(null);
            }
        } catch (NumberFormatException e) {
            lblMensaje.setText("Error: La capacidad debe ser un número.");
            lblMensaje.setStyle("-fx-text-fill: red;");
        } catch (Exception e) {
            lblMensaje.setText("Error al guardar en la base de datos.");
            lblMensaje.setStyle("-fx-text-fill: red;");
            e.printStackTrace();
        }
    }

    @FXML
    private void limpiarFormulario(ActionEvent event) {
        txtNombre.clear();
        txtEdificio.clear();
        txtCapacidad.clear();
        txtResponsable.clear();
        cmbPlanta.getSelectionModel().clearSelection();
        cmbTipoAula.getSelectionModel().clearSelection();
        cmbEstado.getSelectionModel().clearSelection();
        lblMensaje.setText("");
    }

    @FXML
    private void cerrarSesion(ActionEvent event) {
        try {
            edu.ufg.aulas.registroaulas.App.setRoot("/vistas/login");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}