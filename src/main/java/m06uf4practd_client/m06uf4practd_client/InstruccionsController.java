package m06uf4practd_client.m06uf4practd_client;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;

/**
 * Controlador de la vista 'Instruccions'. Aquesta vista mostra un texte d'ajuda
 * que mostra les normes del joc.
 *
 * @author Txell Llanas
 */
public class InstruccionsController implements Initializable {

    @FXML
    private Label descripcioVerd;
    @FXML
    private Label descripcioTaronja;
    @FXML
    private Label descripcioGris;
    @FXML
    private Label instruccions;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        // Carregar texte per les instruccions del joc
        try {
            InputStream inputStream = getClass().getResourceAsStream("/instruccions.txt");
            if (inputStream != null) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line).append("\n");
                }
                String fileContent = sb.toString();
                instruccions.setText(fileContent);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        // Descripcions per cada color
        descripcioVerd.setText("VERD: Significa que la lletra està present dins la paraula i es troba a la posició CORRECTA.");
        descripcioTaronja.setText("GROC: Significa que la lletra està present dins la paraula, però a la posició INCORRECTA.");
        descripcioGris.setText("GRIS: Significa que la lletra NO està present dins la paraula.");
        
    }
    
    
    
}
