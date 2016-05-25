package droxoft.armin.com.shappy;


public class GrupMesaj {

    public boolean side;
    public String mesac;
    public String date;
    public String nick;
    public int renkkatalogu;

    public GrupMesaj(boolean side, String s, String date, String nick, int renkkatalogu) {
        this.side = side;
        mesac = s;
        this.date = date;
        this.nick = nick;
        this.renkkatalogu = renkkatalogu;
    }
}