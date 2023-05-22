package m06uf4practd_client.m06uf4practd_client;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.io.IOException;
import static javafx.application.Application.launch;
import javafx.scene.media.MediaPlayer;
import utils.Utils;

/**
 * Classe que defineix l'objecte Aplicació. Permet configurar la finestra de
 * l'aplicació i la vista a mostrar.
 *
 * @author Txell Llanas
 */
public class App extends Application {

    private static final Logger logger = LogManager.getLogger(App.class);
    private static Scene scene;
    private static final String TITOL_JOC = "Benvinguts a Wordle Online!";

    @Override
    public void start(Stage stage) {

        try {

            // Propietats de la finestra
            scene = new Scene(loadFXML("login"), 870, 760);

            stage.setScene(scene);
            stage.setTitle(TITOL_JOC);
            stage.setMinWidth(670);
            stage.setMinHeight(500);

            // Maximitzar finestra
            stage.setMaximized(true);
            //stage.setFullScreen(true);

            stage.show();

            stage.setOnCloseRequest(event -> {
                if (Utils.getMediaPlayer() != null && Utils.getMediaPlayer().getStatus() == MediaPlayer.Status.PLAYING) {
                    Utils.getMediaPlayer().stop();
                }
                Utils.sortirCreu();
            });

        } catch (IOException ex) {
            logger.error("[ERROR] >> No s'ha pogut carregat la interfície d'usuari.");
        }

    }

    /**
     * Mètode per carregar una vista *.fxml dins un Stage definit com a
     * principal (Root).
     *
     * @param fxml Layout *.fxml a visualitzar dins l'Stage
     * @throws IOException Excepció a mostrar en cas que no es trobi l'arxiu
     * FXML
     *
     * @author Txell Llanas
     */
    public static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
    }

    /**
     * Mètode per carregar una vista *.fxml dins un Stage definit com a
     * principal (Root).
     *
     * @param fxml Layout *.fxml a visualitzar dins l'Stage
     * @throws IOException Excepció a mostrar en cas que no es trobi l'arxiu
     * FXML
     *
     * @author Txell Llanas
     */
    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));
        return fxmlLoader.load();
    }

    /**
     * Executa l'aplicació.
     *
     * @param args args Array d'arguments que es poden passar a l'aplicació.
     *
     * @author Txell Llanas
     */
    public static void main(String[] args) {
        launch();
    }

}
