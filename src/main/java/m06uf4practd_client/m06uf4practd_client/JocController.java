package m06uf4practd_client.m06uf4practd_client;

import common.IPartida;
import common.IUsuari;
import common.Lookups;
import common.PartidaException;
import common.Usuari;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Collections;
import java.util.Comparator;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javax.naming.NamingException;
import utils.Utils;

/**
 * FXML Controller class
 *
 * @author mllanas
 */
public class JocController implements Initializable {

    private static final Logger log = Logger.getLogger(JocController.class.getName());
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
    private ImageView avatar_usuari, icona_posicio;
    @FXML
    private Label minutsLabel, segonsLabel, dosPuntsLabel, label_posicio, label_nickname,
            label_puntuacio_usuari, Label_dificultat;

    private String nivellPartida = "Alt";
    private String email = "";
    
    // Graella
    private int columnes = 4;
    private final int FILES = 6;
    private int FILA_ACTUAL = 1;
    private int COLUMNA_ACTUAL = 1;
    public static List<String> winningWords = new ArrayList<>();
    private Usuari jugador;
    private int rondesSuperades = 0;
    private int tempsTotal = 300;
    private boolean graellaDesactivada = false;
    private int reiniciosPartida = 0;

    // Teclat
    private final String[] firstRowLetters = {"Q", "W", "E", "R", "T", "Y", "U", "I", "O", "P"};
    private final String[] secondRowLetters = {"A", "S", "D", "F", "G", "H", "J", "K", "L"};
    private final String[] thirdRowLetters = {"ENVIAR", "Z", "X", "C", "V", "B", "N", "M", "←"};

    // Recuperar usuari(s)
    static IPartida partida;
    static IUsuari usuari;

    // Recuperar 'Hall of Fame' (Top 5 millors jugadors)
    private ObservableList<Usuari> llistaUsuaris = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        // Llegir dades usuaris (posició, nickname, punts) del servidor
        try {

            // Obtenir una instància remota de la classe 'UsuariEJB'
            usuari = Lookups.usuariEJBRemoteLookup();
            partida = Lookups.partidaEJBRemoteLookup();
            partida.checkPartida("joc");

        } catch (NamingException ex) {

            log.log(Level.SEVERE, "[ERROR] Error iniciant la connexió remota: ", ex + System.lineSeparator());
        }

        // Assignar mètodes als botons del menú
        btn_ajuda.setOnAction(event -> Utils.mostrarAjuda((btn_ajuda)));
        btn_sortir.setOnAction(event -> {
            Utils.sortir();
        });

        // * * * *  DADES USUARI(S)  * * * *
        // Resetejar llistat
        llistaUsuaris.clear();

        // Recuperar usuaris del servidor (Actualitzar dades)
        try {

            llistaUsuaris.addAll(usuari.getUsuaris());

        } catch (PartidaException ex) {

            log.log(Level.SEVERE, "[ERROR] Error iniciant la connexió remota: ", ex + System.lineSeparator());

        }

        // Ordenar llistat d'usuaris en ordre descendent de puntuació
        Collections.sort(llistaUsuaris, Comparator.comparingInt(Usuari::getPuntuacio).reversed());

        // Recuperar usuari actual
        email = LoginController.idSessio;
        jugador = usuari.getUsuari(email);
        String nickname = jugador.getNickname();
        int posicio = 0;

        try {
            partida.afegirJugador(jugador);
        } catch (PartidaException ex) {
            Logger.getLogger(JocController.class.getName()).log(Level.SEVERE, null, ex);
        }

        // Mostrar posició de l'usuari actual
        for (int i = 0; i < llistaUsuaris.size(); i++) {

            if (llistaUsuaris.get(i).getNickname().equals(nickname) && llistaUsuaris.get(i).getPuntuacio() > 0) {
                posicio = i + 1;
                label_posicio.setVisible(true);
                icona_posicio.setVisible(true);
                break; // Parem la iteració, ja que hem localitzat l'usuari

            } else {
                label_posicio.setVisible(false);
                icona_posicio.setVisible(false);

                log.log(Level.INFO, ">> [INFO] El llistat d'usuaris és buit. Encara no hi ha puntuacions.");
            }
        }

        log.log(Level.INFO, ">> [INFO] Llistat d'usuaris correctament recuperat del servidor");

        // Actualitzar Labels UI
        label_nickname.setText(nickname);
        label_puntuacio_usuari.setText(String.valueOf(usuari.getUsuari(email).getPuntuacio()));
        label_posicio.setText(String.valueOf(posicio));
        // * * * *  FI DADES USUARI(S)  * * * *

        // Mostrar compte enrere
        tempsTotal = partida.timeRemaining();
        util.compteEnrere(tempsTotal, minutsLabel, dosPuntsLabel, segonsLabel, "hall");
        // Generar graella i escollir paraula a endevinar segons nivell de dificultat
        nivellPartida = partida.getDificultatPartidaActual();
        switch (nivellPartida) {
            case "Mig":
                columnes = ++columnes;                                          // Nivell 2: 5 lletres

                // Mostrar nivell de dificultat
                Label_dificultat.setText("Dificultat: Mitja");
                pastilla_dificultat.getStyleClass().add("bg-taronja");
                break;
            case "Alta":
                columnes = columnes + 2;                                        // Nivell 3: 6 lletres

                // Mostrar nivell de dificultat
                Label_dificultat.setText("Dificultat: Alta");
                pastilla_dificultat.getStyleClass().add("bg-vermell");
                break;
            default:                                                            // Nivell 1: 4 lletres                
                // Mostrar nivell de dificultat
                Label_dificultat.setText("Dificultat: Baixa");
                pastilla_dificultat.getStyleClass().add("bg-verd");
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
     * @author Víctor García - Crida a mètodes quan es prem una tecla
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
                    try {
                        procesarLletraPolsada(lletraPolsada);
                    } catch (PartidaException ex) {
                        Logger.getLogger(JocController.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        });
    }

    /**
     * Completa la graella de joc, desactivant-la si es final de partida o
     * reiniciant-la si no ho és. Si es final de partida, la graella es
     * desactiva. Si no es final de partida, la graella es reinicia,
     * incrementant el nombre de reinicis de partida.
     *
     * @param finalPartida Indica si es tracta del final de partida o no
     * @author Víctor García
     */
    private void graellaCompleta(boolean finalPartida) {
        if (!finalPartida) {
            graellaDesactivada = true;
            reiniciosPartida++;
            reiniciarPartida();
            label_puntuacio_usuari.setText(String.valueOf(usuari.getUsuari(email).getPuntuacio()));
            graellaDesactivada = false;
        } else {
            graellaDesactivada = true;
        }

    }

    /**
     * Reinicia la partida buidant totes les caselles de la graella i
     * restabliment de la posició actual a la primera posició. Si el nombre de
     * reinicis de partida és igual o superior a 20, es mostra la graella
     * completa.
     *
     * @author Víctor García
     */
    private void reiniciarPartida() {
        // Vaciar todas las casillas de la grilla
        for (int fila = 0; fila < FILES; fila++) {
            for (int columna = 0; columna < columnes; columna++) {
                int indiceNodo = fila * columnes + columna;
                Node nodoEtiqueta = graella.getChildren().get(indiceNodo);
                if (nodoEtiqueta instanceof Label) {
                    Label etiqueta = (Label) nodoEtiqueta;
                    etiqueta.setText("");
                }
            }
        }
        // Restablecer la posición actual a la primera posición
        FILA_ACTUAL = 1;
        COLUMNA_ACTUAL = 1;

        if (reiniciosPartida >= 20) {
            graellaCompleta(true);
        }
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
    private void procesarLletraPolsada(String lletra) throws PartidaException {
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
                int tempsParaula = tempsTotal - partida.timeRemaining();
                filaActualText = obtenirTextFilaActual();
                filaActualText = filaActualText.toLowerCase();
                String resultat = partida.comprovarParaula(filaActualText, rondesSuperades, jugador);
                partida.actualitzarPuntuacio(jugador, resultat, rondesSuperades, tempsParaula);
                if (resultat.contains("+") || resultat.contains("-")) {
                    if (!(FILA_ACTUAL == FILES)) {
                        FILA_ACTUAL++;
                        COLUMNA_ACTUAL = 1;
                    } else {
                        graellaCompleta(false);
                    }
                } else {
                    // logica guanya paraula
                    graellaCompleta(false);
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
