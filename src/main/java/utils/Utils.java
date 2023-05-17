package utils;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Optional;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import org.apache.logging.log4j.LogManager;
import m06uf4practd_client.m06uf4practd_client.App;

/**
 * Classe amb mètodes utilitzats de forma recurrent al llarg de l'aplicació.
 * 
 * @author Txell Llanas
 */
public class Utils {

    static final org.apache.logging.log4j.Logger logger = LogManager.getLogger(App.class);
    static int tempsTotal;
    static Label minutsLabel;
    static Label segonsLabel;
    static Label dosPuntsLabel;
    static String nomVista;
    private static MediaPlayer mediaPlayer;
    static final int TEMPS_MINIM_PER_AVISAR = 5;

    /**
     * Mètode per calcular el compte enrere entre partides, partint del valor
     * 'tempsTotal' i actualitzant els valors dels Labels 'minutsLabel' i
     * 'segonsLabel' a cada iteració. Un cop esgotat el temps, es redirigeix a
     * la vista indicada.
     *
     * @param tempsTotal Duració en segons del compte enrere
     * @param minutsLabel Duració en segons dels minuts restants
     * @param segonsLabel Duració en segons dels segons restants
     * @param nomVista Nom de l'arxiu FXML a mostrar
     *
     * @author Txell Llanas
     */
    public void compteEnrere(int tempsTotal, Label minutsLabel, Label dosPuntsLabel, Label segonsLabel, String nomVista) {

        Utils.tempsTotal = tempsTotal;
        Utils.minutsLabel = minutsLabel;
        Utils.segonsLabel = segonsLabel;
        Utils.dosPuntsLabel = dosPuntsLabel;
        Utils.nomVista = nomVista;

        // Definir el so d'avís per avisar a l'usuari abans no s'esgoti el temps disponible
        try {
            // Definir l'arxiu d'àudio
            String arxiuAudio = getClass().getResource("/beep.wav").toURI().toString();
            // Carregar l'arxiu d'àudio
            Media media = new Media(arxiuAudio);
            // Inicialitzar el MediaPlayer amb l'arxiu d'àudio
            mediaPlayer = new MediaPlayer(media);

        } catch (URISyntaxException e) {
            logger.error("[ERROR] No s'ha pogut localitzar el recurs d'àudio: " + e.getMessage());
        }

        actualitzarComptador();

        new Thread(() -> {

            try {

                while (Utils.tempsTotal > 0) {

                    if (Utils.tempsTotal <= TEMPS_MINIM_PER_AVISAR) {

                        // Canviar color xifres
                        minutsLabel.setStyle("-fx-text-fill: red;");
                        dosPuntsLabel.setStyle("-fx-text-fill: red;");
                        segonsLabel.setStyle("-fx-text-fill: red;");

                        // Reproduir so
                        mediaPlayer.play();
                        Thread.sleep(1000);                                     // retard entre iniciar reproducció i parar reproducció                        
                        mediaPlayer.stop();
                        Utils.tempsTotal--;
                        actualitzarComptador();

                    } else {

                        // Seguir descomptant segons
                        Thread.sleep(1000);
                        Utils.tempsTotal--;
                        actualitzarComptador();
                    }
                }

                App.setRoot(nomVista);                                          // Redirigir a la vista indicada

            } catch (InterruptedException ex) {                                 // error en calcular el compte enrere

                logger.error("[ERROR] S'ha interromput el compte enrere: " + ex.getMessage());

            } catch (IOException ex) {                                          // error en carregar la vista fxml                
                logger.error("[ERROR] No s'ha pogut carregat la pantalla del joc: " + ex.getMessage());
            }
        }).start();
    }

    /**
     * Mètode per calcular el compte enrere entre partides. Aquest compte enrere
     * es mostra en un comptador que hi ha habilitat dins la vista.
     *
     * @author Txell Llanas
     */
    private void actualitzarComptador() {

        int minuts = tempsTotal / 60;
        int segons = tempsTotal % 60;

        Platform.runLater(() -> {
            minutsLabel.setText(String.format("%02d", minuts));
            segonsLabel.setText(String.format("%02d", segons));
        });
    }

    /**
     * Mètode d'ajuda a l'usuari. Obre una nova finestra amb les instruccions
     * del joc.
     *
     * @param boto botó que desencadena l'acció de mostrar la nova finestra
     *
     * @author Txell Llanas
     */
    //@FXML
    public static void mostrarAjuda(Button boto) {
        
        try {
            
            FXMLLoader loader = new FXMLLoader(Utils.class.getResource("/m06uf4practd_client/m06uf4practd_client/instruccions.fxml"));
            Parent root = loader.load();
            
            // Crear una nova finestra de tipus modal
            Stage helpStage = new Stage();
            helpStage.initModality(Modality.APPLICATION_MODAL);
            helpStage.initOwner(boto.getScene().getWindow());
            helpStage.setTitle("Instruccions del joc");
            
            // Carregar l'escena dins la nova finestra
            Scene scene = new Scene(root);
            helpStage.setScene(scene);
            
            // Mostrar vista d''ajuda
            helpStage.showAndWait();
            
        }catch (IOException ex) {

            logger.error("[ERROR] No s'ha pogut carregar la pantalla d'ajuda: " + ex.getMessage());

        }

    }

    /**
     * Mètode per tancar l'aplicació. Obre una diàleg de confirmació per
     * preguntar a l'usuari si realment vol abandonar l'aplicació. En cas
     * afirmatiu, tanca la UI i deslogueja l'usuari.
     *
     * @author Txell Llanas
     */
    
    @FXML
    public static void sortir() {

        // Crear diàleg de confirmació
        Alert confirmDialog = new Alert(Alert.AlertType.CONFIRMATION);
        confirmDialog.setTitle("Abandonar el joc");
        confirmDialog.setHeaderText("Segur que vols sortir?");
        confirmDialog.setContentText("Si surts, es tancarà l'aplicació i et desconnectaràs del servidor.");

        // Configurar botons diàleg
        ButtonType btnYes = new ButtonType("Sortir");
        ButtonType btnNo = new ButtonType("No", ButtonBar.ButtonData.CANCEL_CLOSE);
        confirmDialog.getButtonTypes().setAll(btnYes, btnNo);

        // Mostrar diàleg i esperar a que l'usuari respongui
        Optional<ButtonType> result = confirmDialog.showAndWait();

        // Si l'usuari clica "Sortir", tancar l'aplicació
        if (result.isPresent() && result.get() == btnYes) {
            Platform.exit();
        }

    }

    /**
     * Getter per accedir a l'element MediaPalyer des de l'App.java.
     *
     * @return objecte Mediaplayer que cal llegir des del mètode start() de
     * l'App.java
     *
     * @author Txell Llanas
     */
    public static MediaPlayer getMediaPlayer() {
        return mediaPlayer;
    }
    
    /**
     * Mètode que mostra un missatge a l'usuari en guanyar/perdre una partida.
     * 
     * @author Txell Llanas
     */
    
    public static void mostrarToast(Stage ownerStage, String resultat, Duration duration) {
    
        // Carregar l'escena amb l'arxiu FXML especificat
        FXMLLoader loader = null;
        if( resultat.equals("guanya") ){
            loader = new FXMLLoader(Utils.class.getResource("/m06uf4practd_client/m06uf4practd_client/toastGuanya.fxml"));
        } else if( resultat.equals("perd") ) {
            loader = new FXMLLoader(Utils.class.getResource("/m06uf4practd_client/m06uf4practd_client/toastPerd.fxml"));
        }
        Parent root = null;
        try {
            root = loader.load();
        } catch (IOException ex) {
            logger.error("[ERROR] No s'ha pogut mostrar el Toast: " + ex.getMessage());
        }

        // Crear un nou Stage i carregar-hi l'escena del Toast
        Stage toastStage = new Stage();
        toastStage.initStyle(StageStyle.TRANSPARENT);
        
        Scene scene = new Scene(root);
        scene.setFill(Color.TRANSPARENT);
        toastStage.setScene(scene);

        // Establir propietats del nou Stage perquè es mostri com un diàleg
        toastStage.initModality(Modality.APPLICATION_MODAL);
        toastStage.initOwner(ownerStage);
        
        // Mostrar el Toast
        toastStage.show();
        Timeline timeline = new Timeline();
        KeyFrame key = new KeyFrame(duration, new KeyValue(toastStage.opacityProperty(), 0));
        timeline.getKeyFrames().add(key);
        timeline.setOnFinished(event -> toastStage.close());
        timeline.play();

    }

}
