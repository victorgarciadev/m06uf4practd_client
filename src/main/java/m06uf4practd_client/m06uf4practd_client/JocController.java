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

            // Agrega el controlador de eventos al botón
            label.setOnMouseClicked(event -> {
                // Lógica para manejar el evento del botón pulsado
                String letraPulsada = label.getText();
                // Llama a un método o realiza las acciones que deseas con la letra pulsada
                procesarLletraPulsada(letraPulsada);
            });
        }

        for (int i = 0; i < secondRowLetters.length; i++) {
            Label label = new Label();
            label.getStyleClass().add("tecla");
            label.setText(secondRowLetters[i]);
            teclatFila2.add(label, i, 2);

            aplicarEstilTeclaApretada(label);

            // Agrega el controlador de eventos al botón
            label.setOnMouseClicked(event -> {
                // Lógica para manejar el evento del botón pulsado
                String letraPulsada = label.getText();
                // Llama a un método o realiza las acciones que deseas con la letra pulsada
                procesarLletraPulsada(letraPulsada);
            });
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

            // Agrega el controlador de eventos al botón
            label.setOnMouseClicked(event -> {
                // Lógica para manejar el evento del botón pulsado
                String letraPulsada = label.getText();
                // Llama a un método o realiza las acciones que deseas con la letra pulsada
                procesarLletraPulsada(letraPulsada);
            });
        }

    }

    private void procesarLletraPulsada(String letra) {  
        String filaActualTexto = "";
      
        if (letra.equals("←")) {
            if ((COLUMNA_ACTUAL < (columnes))) {
                retrocederPosicion();
                borrarCasillaActual();
            } else {
                Node nodoEtiqueta = graella.getChildren().get((FILA_ACTUAL - 1) * columnes + (COLUMNA_ACTUAL - 1));
                if (nodoEtiqueta instanceof Label) {
                    Label etiqueta = (Label) nodoEtiqueta;
                    String textoCasilla = etiqueta.getText();
                    if (!textoCasilla.isEmpty()) {
                        // Agregar la letra al String filaActualTexto
                        borrarCasillaActual();
                    } else {
                        retrocederPosicion();
                        borrarCasillaActual();
                    }
                }
            }
        } else if (letra.equals("ENVIAR")) { //&& COLUMNA_ACTUAL == columnes
            System.out.println("ENVIAR");
            // Comprobar si estamos en la última posición de la fila
            System.out.println("COLUMNA ACTUAL --> " + COLUMNA_ACTUAL);
            System.out.println("COLUMNES ---> " + columnes);
            if (COLUMNA_ACTUAL == (columnes)) {
                filaActualTexto = obtenerTextoFilaActual();
                System.out.println("Texto de la fila actual: " + filaActualTexto);
                // Realizar la acción de enviar

                FILA_ACTUAL++;
                COLUMNA_ACTUAL = 1;
            } else {
                Node nodoEtiqueta = graella.getChildren().get((FILA_ACTUAL - 1) * columnes + (COLUMNA_ACTUAL - 1));
                if (nodoEtiqueta instanceof Label) {
                    Label etiqueta = (Label) nodoEtiqueta;
                    String textoCasilla = etiqueta.getText();
                    if (!textoCasilla.isEmpty()) {
                        // Agregar la letra al String filaActualTexto
                        filaActualTexto += textoCasilla;
                    }
                }
            }
        } else {
            System.out.println("OTRO");
            System.out.println("COLUMNA ACTUAL --> " + COLUMNA_ACTUAL);
            System.out.println("COLUMNES ---> " + columnes);

            Node nodoEtiqueta = graella.getChildren().get((FILA_ACTUAL - 1) * columnes + (COLUMNA_ACTUAL - 1));
            if (nodoEtiqueta instanceof Label) {
                Label etiqueta = (Label) nodoEtiqueta;
                etiqueta.setText(letra);
            }

            if ((COLUMNA_ACTUAL < (columnes))) {
                // Actualizar la posición actual en la grilla solo si no estamos en la última posición
                COLUMNA_ACTUAL++;
            }
        }
    }

    private String obtenerTextoFilaActual() {
        StringBuilder textoFila = new StringBuilder();
        int filaInicio = (FILA_ACTUAL - 1) * columnes;
        int filaFin = filaInicio + columnes;

        for (int i = filaInicio; i < filaFin; i++) {
            Node nodoEtiqueta = graella.getChildren().get(i);
            if (nodoEtiqueta instanceof Label) {
                Label etiqueta = (Label) nodoEtiqueta;
                String textoCasilla = etiqueta.getText();
                textoFila.append(textoCasilla);
            }
        }

        return textoFila.toString();
    }

    private void borrarCasillaActual() {
        Node nodoEtiquetaActual = graella.getChildren().get((FILA_ACTUAL - 1) * columnes + (COLUMNA_ACTUAL - 1));
        if (nodoEtiquetaActual instanceof Label) {
            Label etiquetaActual = (Label) nodoEtiquetaActual;
            if (!etiquetaActual.getText().isEmpty()) {
                etiquetaActual.setText("");
            }
        }
    }

    private void retrocederPosicion() {
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
