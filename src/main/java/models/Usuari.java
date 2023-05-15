package models;

/**
 *
 * @author mllanas
 */
public class Usuari {
    
    private int posicio;
    private String nickname;
    private int puntuacio;

    public Usuari() {
    }
    public Usuari(int posicio, String nickname, int punts) {
        this.posicio = posicio;
        this.nickname = nickname;
        this.puntuacio = punts;
    }

    public int getPosicio() {
        return posicio;
    }

    public void setPosicio(int posicio) {
        this.posicio = posicio;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public int getPuntuacio() {
        return puntuacio;
    }

    public void setPunts(int punts) {
        this.puntuacio = punts;
    }

    
    
    
    
}
