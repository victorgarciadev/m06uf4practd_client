/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package m06uf4practd_client.m06uf4practd_client;

import common.IUsuari;
import common.Lookups;
import common.PartidaException;
import common.Usuari;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javax.naming.NamingException;
import org.apache.logging.log4j.LogManager;

/**
 * FXML Controller class
 *
 * @author izan
 */
public class LoginController implements Initializable {

    private static final org.apache.logging.log4j.Logger logger = LogManager.getLogger(App.class);

    @FXML
    private TextField textFieldNickname;
    @FXML
    private TextField textFieldEmail;
    @FXML
    private Button primaryButton;

    static IUsuari usuari;

    public static String idSessio = null;
    
    public Usuari u;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {

        try {
            usuari = Lookups.usuariEJBRemoteLookup();
        } catch (NamingException ex) {
            Logger.getLogger("Error iniciant: " + System.lineSeparator()).log(Level.SEVERE, null, ex);
        }

    }

    @FXML
    private void entrarBtnClick(ActionEvent event) {
        try {
            idSessio = null;
            if (textFieldEmail.getText().trim().length() > 0 &&
                    validateEmail(textFieldEmail.getText())) {
                //login
                u = usuari.getUsuari(textFieldEmail.getText());
                //si no hi ha login es crea usuari
                if (u == null) {
                    if (textFieldNickname.getText().trim().length() > 0) {
                        System.out.println("Se crea ususario");
                        usuari.crearUsuari(textFieldEmail.getText().trim(),
                                textFieldNickname.getText().trim());
                        idSessio = usuari.getUsuari(textFieldEmail.getText()).getEmail();
                        u = usuari.getUsuari(textFieldEmail.getText());
                        usuari.setUsuariJugant(u);
                    } else {
                        showALerta("Es requereix un nickname");
                    }
                } else {
                    if (u.isJugadorActual() == 0) {
                        usuari.setUsuariJugant(u);
                        idSessio = u.getEmail();
                    } else {
                        showALerta("El jugador ja està logejat.");
                    }
                    
                }
            } else {
                showALerta("Es requereix un email vàlid");
            }
        } catch (PartidaException ex) {
            logger.info("Error iniciant sessió: " + System.lineSeparator() + ex);
            showALerta("Error iniciant sessió");
        }
        logger.info(">>>>>>>" + idSessio);
        try {
            if (idSessio != null) {
                App.setRoot("hall");
            }

        } catch (IOException ex) {
            usuari.setUsuariDesactiu(u);
            logger.info(ex);
            showALerta("No es pot carregar el HALL");
        }
    }

    public void showALerta(String msg) {
        Alert alerta = new Alert(AlertType.INFORMATION);
        alerta.setTitle("ATENCIÓ");
        alerta.setContentText(msg);

        alerta.showAndWait();
    }
    
    public static boolean validateEmail(String email){
        Pattern pattern = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }
    
}
