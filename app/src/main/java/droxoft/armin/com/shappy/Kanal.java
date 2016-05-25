package droxoft.armin.com.shappy;

public class Kanal {
    String kanaladi;
    String id;
    String kanalurl;
    String kurankisi;
    String date;
    int likesayisi;
    int likedurumu;
    String distance;
    boolean official;
    String kisisayisi;
    String yenimesajvarmi;
    String kacyenimesaj;


    public Kanal(boolean official){
        this.official = official;
    }
    public String getKanaladi() {
        return kanaladi;
    }

    public void setKanaladi(String kanaladi) {
        this.kanaladi = kanaladi;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getKisisayisi() {
        return kisisayisi;
    }

    public void setKisisayisi(String kisisayisi) {
        this.kisisayisi = kisisayisi;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public int getLikedurumu() {
        return likedurumu;
    }

    public void setLikedurumu(int likedurumu) {
        this.likedurumu = likedurumu;
    }

    public int getLikesayisi() {
        return likesayisi;
    }

    public void setLikesayisi(int likesayisi) {
        this.likesayisi = likesayisi;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getKurankisi() {
        return kurankisi;
    }

    public void setKurankisi(String kurankisi) {
        this.kurankisi = kurankisi;
    }

    public String getKanalurl() {
        return kanalurl;
    }

    public void setKanalurl(String kanalurl) {
        this.kanalurl = kanalurl;
    }

    public String getKacyenimesaj() {
        return kacyenimesaj;
    }

    public void setKacyenimesaj(String kacyenimesaj) {
        this.kacyenimesaj = kacyenimesaj;
    }

    public String getYenimesajvarmi() {
        return yenimesajvarmi;
    }

    public void setYenimesajvarmi(String yenimesajvarmi) {
        this.yenimesajvarmi = yenimesajvarmi;
    }
}
