package m06uf4practd_client.m06uf4practd_client;

import java.net.URL;
import java.util.ArrayList;
import java.util.Random;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.util.Duration;
import utils.Utils;

/**
 * FXML Controller class
 *
 * @author mllanas
 */
public class JocController implements Initializable {

    Utils util = new Utils();

    @FXML
    private ScrollPane pagina;
    @FXML
    private HBox titol, botonera, ranking_usuari, pastilla_dificultat;
    @FXML
    private Button btn_ajuda, btn_sortir;
    @FXML
    private GridPane graella, teclatFila1, teclatFila2, teclatFila3;
    @FXML
    private ImageView avatar_usuari;
    @FXML
    private Label minutsLabel, segonsLabel, dosPuntsLabel, label_posicio, label_nickname,
            label_puntuacio_usuari, Label_dificultat;

    private int nivellPartida = 1;

    // Graella
    private int columnes = 4;
    private final int FILES = 6;
    private int FILA_ACTUAL = 1;
    private int COLUMNA_ACTUAL = 1;
    public static final ArrayList<String> winningWords = new ArrayList<>();
    private String winningWord;
    private boolean graellaDesactivada = false;

    // Teclat
    private final String[] firstRowLetters = {"Q", "W", "E", "R", "T", "Y", "U", "I", "O", "P"};
    private final String[] secondRowLetters = {"A", "S", "D", "F", "G", "H", "J", "K", "L"};
    private final String[] thirdRowLetters = {"ENVIAR", "Z", "X", "C", "V", "B", "N", "M", "←"};

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        // Assignar mètodes als botons del menú
        btn_ajuda.setOnAction(event -> Utils.mostrarAjuda((btn_ajuda)));
        btn_sortir.setOnAction(event -> {
            Utils.sortir();
        });

        // Inicialitzar dades usuari
        String nickname = "Usuari";  // TODO: recuperar-ho del servidor
        label_nickname.setText(nickname);

        // Mostrar compte enrere
        //util.compteEnrere(7, minutsLabel, dosPuntsLabel, segonsLabel, "hall");
        // Generar graella i escollir paraula a endevinar segons nivell de dificultat
        nivellPartida = new Random().nextInt(3) + 1;
        switch (nivellPartida) {
            case 2:
                columnes = ++columnes;                                          // Nivell 2: 5 lletres

                // Mostrar nivell de dificultat
                Label_dificultat.setText("Dificultat: Mitja");
                pastilla_dificultat.getStyleClass().add("bg-taronja");

                // TODO: Carregar llistat de paraules per endevinar de 5 lletres
                winningWords.add("cars");

                break;
            case 3:
                columnes = columnes + 2;                                        // Nivell 3: 6 lletres

                // Mostrar nivell de dificultat
                Label_dificultat.setText("Dificultat: Alta");
                pastilla_dificultat.getStyleClass().add("bg-vermell");

                // TODO: Carregar llistat de paraules per endevinar de 6 lletres
                winningWords.add("roads");

                break;
            default:                                                            // Nivell 1: 4 lletres                
                // Mostrar nivell de dificultat
                Label_dificultat.setText("Dificultat: Baixa");
                pastilla_dificultat.getStyleClass().add("bg-verd");

                // TODO: Carregar llistat de paraules per endevinar de 4 lletres
                winningWords.add("sky");

                break;
        }
        crearGraella(graella, columnes, FILES);

        // Generar teclat
        crearTeclat(teclatFila1, teclatFila2, teclatFila3);
    }

    /**
     * Mètode per generar la graella del joc de forma dinàmica segons el nivell
     * de dificultat, que és decidit de forma random. Mostra una graella de 'N'
     * columnes per 6 files, que són el nombre d'intents que té l'usuari per
     * endevinar la paraula amagada.
     *
     * @param graella GridPane que conté les caselles de les lletres a
     * endevinar.
     * @param columnes valor enter que defineix el nombre de lletres a
     * endevinar.
     * @param files valor enter que determina el nombre d'intents per endevinar
     * la paraula amagada.
     *
     * @author Txell Llanas
     */
    public void crearGraella(GridPane graella, int columnes, int files) {

        for (int i = 1; i <= files; i++) {
            for (int j = 1; j <= columnes; j++) {
                Label label = new Label();
                label.getStyleClass().addAll("casella", "casella-buida");
                graella.add(label, j, i);
            }
        }

    }

    /**
     * Mètode per crear el teclat de forma dinàmica, segons les lletres
     * designades dins una matriu de Strings per a cada fila del teclat.
     *
     * @param teclatFila1 GridPane amb les tecles de la primera fila del teclat.
     * @param teclatFila2 GridPane amb les tecles de la segona fila del teclat.
     * @param teclatFila3 GridPane amb les tecles de la tercera fila del teclat.
     *
     * @author Txell Llanas
     */
    public void crearTeclat(GridPane teclatFila1, GridPane teclatFila2, GridPane teclatFila3) {

        for (int i = 0; i < firstRowLetters.length; i++) {
            Label label = new Label();
            label.getStyleClass().add("tecla");
            label.setText(firstRowLetters[i]);
            teclatFila1.add(label, i, 1);

            aplicarEstilTeclaApretada(label);

            eventsTeclat(label);
        }

        for (int i = 0; i < secondRowLetters.length; i++) {
            Label label = new Label();
            label.getStyleClass().add("tecla");
            label.setText(secondRowLetters[i]);
            teclatFila2.add(label, i, 2);

            aplicarEstilTeclaApretada(label);

            eventsTeclat(label);
        }

        for (int i = 0; i < thirdRowLetters.length; i++) {
            Label label = new Label();

            if (i == 0 || i == thirdRowLetters.length - 1) {                    // Tecles ENVIAR i ELIMINAR
                label.getStyleClass().add("tecla");
                label.setStyle("-fx-padding: 0 10 0 10;");
            } else {
                label.getStyleClass().add("tecla");
            }

            label.setText(thirdRowLetters[i]);
            teclatFila3.add(label, i, 3);

            aplicarEstilTeclaApretada(label);

            eventsTeclat(label);
        }

    }

    /**
     * Mètode per gestionar els esdeveniments de les tecles del teclat.
     *
     * @param label L'etiqueta (tecla) a la qual s'associa l'esdeveniment.
     * @author Víctor García
     */
    private void eventsTeclat(Label label) {
        // Listener per a quan l'usuari prem una tecla del teclat
        label.setOnMouseClicked(event -> {
            String lletraPolsada = label.getText();
            if (graellaDesactivada) {
                return; // Si la graella està plena, s'inhabilita el teclat
            } else {
                if (!esFinal() || lletraPolsada.equals("←") || lletraPolsada.equals("ENVIAR")) {
                    procesarLletraPolsada(lletraPolsada);
                }
            }
        });
    }

    /**
     * Mètode per indicar que la graella està completa i desactivar el teclat.
     *
     * @author Víctor García
     */
    private void graellaCompleta() {
        graellaDesactivada = true;
    }

    /**
     * Verifica si la posició actual a la graella és la posició final.
     *
     * @return `true` si la posició actual és la posició final i conté text,
     * sinó `false`.
     * @author Víctor García
     */
    private boolean esFinal() {
        Node nodeEtiqueta = graella.getChildren().get((FILA_ACTUAL - 1) * columnes + (COLUMNA_ACTUAL - 1));
        if (nodeEtiqueta instanceof Label) {
            Label etiqueta = (Label) nodeEtiqueta;
            String text = etiqueta.getText();

            if (COLUMNA_ACTUAL == (columnes) && FILA_ACTUAL == FILES && !text.isEmpty() && !text.isBlank()) {
                return true;
            }
        }

        return false;
    }

    /**
     * Processa la lletra premuda en el teclat i realitza les accions
     * corresponents.
     *
     * @param lletra La lletra premuda en el teclat.
     * @author Víctor García
     */
    private void procesarLletraPolsada(String lletra) {
        String filaActualText = "";

        if (graellaDesactivada) {
            return; // Comprovem si la graella està plena
        }

        if (lletra.equals("←")) {
            if ((COLUMNA_ACTUAL < (columnes))) {
                retrocedirPosicio();
                esborrarCasellaActual();
            } else {
                Node nodeEtiqueta = graella.getChildren().get((FILA_ACTUAL - 1) * columnes + (COLUMNA_ACTUAL - 1));
                if (nodeEtiqueta instanceof Label) {
                    Label etiqueta = (Label) nodeEtiqueta;
                    String textCasella = etiqueta.getText();
                    if (!textCasella.isEmpty()) {
                        esborrarCasellaActual();
                    } else {
                        retrocedirPosicio();
                        esborrarCasellaActual();
                    }
                }
            }
        } else if (lletra.equals("ENVIAR")) {
            boolean hasText = false;

            Node nodeEtiquetaa = graella.getChildren().get((FILA_ACTUAL - 1) * columnes + (COLUMNA_ACTUAL - 1));
            if (nodeEtiquetaa instanceof Label) {
                Label etiquetaa = (Label) nodeEtiquetaa;
                String text = etiquetaa.getText();

                if (!text.isEmpty() || !text.isBlank()) {
                    hasText = true;
                }
            }

            if (COLUMNA_ACTUAL == columnes && hasText) {
                filaActualText = obtenirTextFilaActual();

                if (!(FILA_ACTUAL == FILES)) {
                    FILA_ACTUAL++;
                    COLUMNA_ACTUAL = 1;
                } else {
                    graellaCompleta();
                }

            } else {
                Node nodeEtiqueta = graella.getChildren().get((FILA_ACTUAL - 1) * columnes + (COLUMNA_ACTUAL - 1));

                if (nodeEtiqueta instanceof Label) {
                    Label etiqueta = (Label) nodeEtiqueta;
                    String textCasella = etiqueta.getText();

                    if (!textCasella.isEmpty()) {
                        // Agreguem la lletra a la fila actual
                        filaActualText += textCasella;
                    }
                }
            }
        } else {
            Node nodeEtiqueta = graella.getChildren().get((FILA_ACTUAL - 1) * columnes + (COLUMNA_ACTUAL - 1));
            if (nodeEtiqueta instanceof Label) {
                Label etiqueta = (Label) nodeEtiqueta;
                etiqueta.setText(lletra);
            }

            // Actualitzem la posició de la graella només si no som a l'última fila
            if ((COLUMNA_ACTUAL < (columnes))) {
                COLUMNA_ACTUAL++;
            }

        }
    }

    /**
     * Obté el text de la fila actual de la graella.
     *
     * @return El text de la fila actual de la graella.
     * @author Víctor García
     */
    private String obtenirTextFilaActual() {
        StringBuilder textFila = new StringBuilder();
        int filaInici = (FILA_ACTUAL - 1) * columnes;
        int filaFinal = filaInici + columnes;

        for (int i = filaInici; i < filaFinal; i++) {
            Node nodoEtiqueta = graella.getChildren().get(i);
            if (nodoEtiqueta instanceof Label) {
                Label etiqueta = (Label) nodoEtiqueta;
                String textoCasilla = etiqueta.getText();
                textFila.append(textoCasilla);
            }
        }

        return textFila.toString();
    }

    /**
     * Esborra el contingut de la casella actual de la graella.
     *
     * Aquest mètode esborra el text de la casella actual de la graella, sempre
     * i quan la graella no estigui desactivada.
     *
     * @author Víctor García
     */
    private void esborrarCasellaActual() {
        if (!graellaDesactivada) {
            Node nodeEtiquetaActual = graella.getChildren().get((FILA_ACTUAL - 1) * columnes + (COLUMNA_ACTUAL - 1));
            if (nodeEtiquetaActual instanceof Label) {
                Label etiquetaActual = (Label) nodeEtiquetaActual;
                if (!etiquetaActual.getText().isEmpty()) {
                    etiquetaActual.setText("");
                }
            }
        }
    }

    /**
     * Retrocedeix la posició a la columna anterior de la graella.
     *
     * Aquest mètode decrementa la columna actual en 1, sempre i quan la columna
     * actual sigui major que 1. S'utilitza per retrocedir a la casella anterior
     * quan l'usuari prem la tecla "←" en el teclat.
     *
     * @author Víctor García
     */
    private void retrocedirPosicio() {
        if (COLUMNA_ACTUAL > 1) {
            COLUMNA_ACTUAL--;
        }
    }

    /**
     * Mètode per definir l'estil d'una tecla clicada amb el ratolí.
     *
     * @param label lletra que correspon a una tecla determinada.
     *
     * @author Txell Llanas
     */
    private void aplicarEstilTeclaApretada(Label label) {

        // TODO: quan Pablo/Víctor creïn mètode per traslladar lletra a la graella, no cal capturar l'event, només aplicar l'estil
        // Afegir estil quan el botó del ratolí s'apreta
        label.setOnMousePressed(event -> {
            label.getStyleClass().add("tecla-pressed");

            // TODO: PABLO / VÍCTOR,  eliminar d'aquí...
            // EXEMPLE DE COM MOSTRAR un Toast (2 tipus: guanya/perd), no funciona per si sol a initialize();
            //Utils.mostrarToast((Stage)pagina.getScene().getWindow(), "guanya", Duration.seconds(2));
            //Utils.mostrarToast((Stage) pagina.getScene().getWindow(), "perd", Duration.seconds(2));
        });

        // Netejar l'estil quan el botó del ratolí s'allibera
        label.setOnMouseReleased(event -> {
            label.getStyleClass().remove("tecla-pressed");
        });

    }

}
