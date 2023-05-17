/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package m06uf4practd_client.m06uf4practd_client;

import common.IUsuari;
import common.Lookups;
import common.PartidaException;
import common.Usuari;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javax.naming.NamingException;

/**
 * FXML Controller class
 *
 * @author izan
 */
public class LoginController implements Initializable {

    @FXML
    private TextField textFieldNickname;
    @FXML
    private TextField textFieldEmail;
    @FXML
    private Button primaryButton;

    static IUsuari usuari;

    String idSessio;

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
        if (textFieldEmail.getText().trim().length() <= 0 && textFieldNickname.getText().trim().length() <= 0) {
            //missatge de error
        } else {
            //login i/o alta
            try {

                //login
                Usuari u = usuari.getUsuari(textFieldEmail.getText());

                //si no hi ha login
                if (u == null && textFieldNickname.getText().trim().length() > 0) {
                    System.out.println("Se crea ususario");
                    usuari.crearUsuari(textFieldEmail.getText().trim(),
                            textFieldNickname.getText().trim());
                    idSessio = usuari.getUsuari(textFieldEmail.getText()).getEmail();

                } else {
                    //error
                }

            } catch (PartidaException ex) {
                Logger.getLogger("Error creant usuari: " + System.lineSeparator()).log(Level.SEVERE, null, ex);
            }
        }
    }

}
