package m06uf4practd_client.m06uf4practd_client;

import common.IPartida;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import common.Lookups;
import common.IUsuari;
import common.PartidaException;
import common.Usuari;
import java.util.Collections;
import java.util.Comparator;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.control.TableCell;
import javafx.scene.image.ImageView;
import javax.naming.NamingException;
import utils.Utils;

/**
 * Controlador de la vista 'Hall'. Aquesta vista actua com a sala de benvinguda
 * quan es logueja un usuari i/o com a sala d'espera mentres hi hagi una jugada
 * en curs.
 *
 * @author Txell Llanas
 */
public class HallController implements Initializable {

    private static final Logger log = Logger.getLogger(HallController.class.getName());
    private static int tempsTotal = 0;                                          // Duració en segons pel compte enrere
    Utils util = new Utils();                                                   // Classe amb mètodes globals
    private ObservableList<Usuari> llistaTop5 = FXCollections.observableArrayList(); // Recuperar 'Hall of Fame' (Top 5 millors jugadors) 
    private int firstPosition = 0;                                              // Definir la posició per la primera fila del llistat 'Hall of Fame' (Top 5 millors jugadors)
    static IUsuari usuari;                                                      // Recuperar usuari(s)
    static IPartida partida;

    @FXML
    private Label label_salutacio, label_puntuacio_usuari, label_nickname, label_posicio, minutsLabel, segonsLabel, dosPuntsLabel;
    @FXML
    private Button btn_ajuda, btn_sortir;
    @FXML
    private TableView<Usuari> tableView_top5;
    @FXML
    private TableColumn<Usuari, Integer> col_posicio, col_punts;
    @FXML
    private TableColumn<Usuari, String> col_nickname;
    @FXML
    private HBox titol, botonera, ranking_usuari;
    @FXML
    private ImageView icona_posicio;
    @FXML
    private ScrollPane pagina;
    

    /**
     * Inicialitza el controlador de la vista 'Hall'.
     *
     * Aquesta és la pantalla de benvinguda a l'usuari i també la sala d'espera
     * entre partides.
     *
     * L'usuari pot consultar-hi el rànquing dels millors 5 jugadors, la seva
     * pròpia puntuació i posició dins el rànquing, les instruccions del joc i
     * també sortir de l'aplicació.
     *
     * @param url The location used to resolve relative paths for the root
     * object, or null if the location is not known.
     * @param rb The resources used to localize the root object, or null if the
     * root object was not localized.
     *
     * @author Txell Llanas
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {

        // *** BOTONS MENÚ ***
        // Assignar mètodes als botons del menú
        btn_ajuda.setOnAction(event -> Utils.mostrarAjuda((btn_ajuda)));
        btn_sortir.setOnAction(event -> {
            Utils.sortir();
        });
        
        // *** RESUPERAR DADES DEL SERVIDOR ***
        try {

            // Obtenir una instàncies remotes
            usuari = Lookups.usuariEJBRemoteLookup();
            partida = Lookups.partidaEJBRemoteLookup();

            log.log(Level.INFO, "Connexió correcta al servidor remot");

        } catch (NamingException ex) {
            log.log(Level.SEVERE, "[ERROR] Error iniciant la connexió remota: ", ex + System.lineSeparator());
        }

        // *** COMPTADOR ***        
        tempsTotal = partida.timeRemaining();
        util.compteEnrere(tempsTotal, minutsLabel, dosPuntsLabel, segonsLabel, "joc");

        // *** HALL of FAME ***
        Label placeholder = new Label("Encara no hi ha campions/es");           // Especifico un texte d'ajuda per quan el llistat està buit
        tableView_top5.setPlaceholder(placeholder);
          
        // *** DADES USUARI *** 
        // Resetejar llistats
        tableView_top5.getItems().clear();        
        llistaTop5.clear();
        
        // Recuperar usuaris del servidor (Actualitzar dades)
        try {
            
            llistaTop5.addAll(usuari.getUsuaris());
            
        } catch (PartidaException ex) {            
            log.log(Level.SEVERE, "[EXCEPTION] Excepció llançada: ", ex + System.lineSeparator());            
        }
        
        // Ordenar llistat 'llistaTop5' en ordre descendent de puntuació
        Collections.sort(llistaTop5, Comparator.comparingInt(Usuari::getPuntuacio).reversed());
        
        // Recuperar usuari actual
        String email = LoginController.idSessio;
        String nickname = usuari.getUsuari(email).getNickname();
        String salutacio = "Hola, " + nickname + "!";
        int posicio = -1;
        
        // Mostrar llistat 'Hall of Fame'
        if (!llistaTop5.isEmpty()) 
        {
            for (int i = 0; i < llistaTop5.size(); i++)
            {                
                // Omplir llistat 'Hall of Fame' només amb usuaris amb puntuacions superiors a 0
                if (llistaTop5.get(i).getPuntuacio() > 0)
                {
                    tableView_top5.getItems().add(llistaTop5.get(i));
                }
            }

            // Localitzar posició usuari actual dins del ranking
            for (int i = 0; i < llistaTop5.size(); i++)
            {
                // Desar posició si es tenen punts
                if ( llistaTop5.get(i).getNickname().equals(nickname) && llistaTop5.get(i).getPuntuacio() > 0 )
                {
                    posicio = i+1;                        
                    label_posicio.setVisible(true);
                    icona_posicio.setVisible(true);
                    break; // Parem la iteració, ja que hem localitzat l'usuari

                } else {
                    label_posicio.setVisible(false);
                    icona_posicio.setVisible(false);
                }
            }

            if ( !tableView_top5.getItems().isEmpty() )
            {
                log.log(Level.INFO, ">> [INFO] Llistat d'usuaris correctament recuperat del servidor");
            } else {
                log.log(Level.INFO, ">> [INFO] El llistat d'usuaris és buit. Encara no hi ha usuaris amb puntuacions.");
            }            
        }
        
        // Actualitzar Labels UI
        label_salutacio.setText(salutacio);
        label_nickname.setText(nickname);
        label_puntuacio_usuari.setText(String.valueOf(usuari.getUsuari(email).getPuntuacio()));
        label_posicio.setText(String.valueOf(posicio));
        // * * * *  FI DADES USUARI(S)  * * * *

        
        // Enllaçar columnes TableView amb propietats objecte Usuari
        /** Crear una cel·la personalitzada per la columna 'col_posicio' 
         *  (llistar posicions de l'1 al 5)
         */
        col_posicio.setCellFactory(column -> {
            return new TableCell<Usuari, Integer>() {
                @Override
                protected void updateItem(Integer item, boolean empty) {
                    super.updateItem(item, empty);

                    // Obtenir índex fila actual
                    int index = getIndex();

                    if (index >= 0 && index < 5) {
                        // Assignar posició corresponent a la fila actual
                        setText(String.valueOf(index+1));
                    } else {
                        // No omplir la cel·la de les files següents
                        setText(null);
                    }
                }
            };
        });

        col_nickname.setCellValueFactory(new PropertyValueFactory<>("nickname"));
        col_punts.setCellValueFactory(new PropertyValueFactory<>("puntuacio"));        

        // Marcar usuari guanyador (si hi ha algú a la primera posició)
        tableView_top5.setRowFactory(tv -> {
            TableRow<Usuari> row = new TableRow<>();
            row.itemProperty().addListener((obs, oldVal, newVal) -> {

                if (newVal != null && row.getIndex() == firstPosition) {
                    row.getStyleClass().clear();
                    row.getStyleClass().addAll("guanyador", "txt-resaltat");
                } else {
                    row.getStyleClass().removeAll("guanyador", "txt-resaltat");
                }
            });
            return row;
        });

        // Mostrar/ocultar posició i puntuació de l'usuari actual
        mostrarRankingUsuari(nickname);

    }

    /**
     * Mètode que mostra una pastilla amb la posició i puntuació actuals de
     * l'usuari loguejat. Primer es verifica si l'usuari especificat es troba
     * dins els 5 millors jugadors del 'Hall of Fame'. Si es troba dins, oculta
     * la pastilla, ja que ja es troba dins el llistat dels 5 millors jugadors.
     * Sinó, la mostra per informar a l'usuari en quina situació està.
     *
     * @param nickname String amb el nickname de l'usuari a cercar.
     * @author Txell Llanas
     */
    private void mostrarRankingUsuari(String nickname) {

        int targetIndex = -1;                                                   // Índex de l'element a cercar dins el llistat
        final int targetIndexFinal = targetIndex;

        // Cercar l'índex de l'element amb el nickname de l'usuari dins el llistat Top 5
        for (int i = 0; i < tableView_top5.getItems().size(); i++) {
            if (tableView_top5.getItems().get(i).getNickname().equals(nickname)) {
                targetIndex = i;
                break;
            }
        }

        if (targetIndex != -1 && targetIndex < 5)
        {
            // Si l'usuari es troba dins els primers 5 registres, ocultar l'HBox amb la posició/puntuació de l'usuari
            ranking_usuari.setVisible(false);
            ranking_usuari.setManaged(false);

            // Marcar la fila on es troba l'usuari per ressaltar-lo...
            final int indexUsuari = targetIndex;
            tableView_top5.setRowFactory(tv -> {
                TableRow<Usuari> row = new TableRow<>();
                row.itemProperty().addListener((obs, oldVal, newVal) -> {

                    if (newVal != null) {
                        if (row.getIndex() == indexUsuari && row.getIndex() > firstPosition) {
                            row.getStyleClass().addAll("usuari", "txt-clar");
                        } else {
                            row.getStyleClass().removeAll("usuari", "txt-clar");
                        }
                        if (row.getIndex() == firstPosition) {
                            row.getStyleClass().addAll("guanyador", "txt-resaltat");
                        } else {
                            row.getStyleClass().removeAll("guanyador", "txt-resaltat");
                        }
                    }
                });
                return row;
            });

        } else {
            // Si no, mostrar-lo
            ranking_usuari.setVisible(true);
            ranking_usuari.setManaged(true);
        }

        // Afegir un listener al llistat d'elements del TableView per detectar canvis
        llistaTop5.addListener((ListChangeListener<Usuari>) canvi -> {
            while (canvi.next()) {
                // Si hi ha canvis al llistat, torna a verificar si l'element es troba dins els primers 5 registres
                if (targetIndexFinal != -1 && targetIndexFinal < 5) {
                    ranking_usuari.setVisible(false);
                } else {
                    ranking_usuari.setVisible(true);
                }
            }
        });

    }

}