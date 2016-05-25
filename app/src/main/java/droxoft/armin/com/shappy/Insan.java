package droxoft.armin.com.shappy;

import java.util.Calendar;

public class Insan {

    String id,name,uzaklik,durum,faceprofilur,okul,borndate,cinsiyet,yas,coverphotourl,burc;
    String resmiacik , resimpath , bandurumu;
    String yenimesajvarmi, kacyenimesaj;

    public Insan(){
    }

    public String getYas() {
        return yas;
    }

    public void setYas(String yas) {
        this.yas = yas;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUzaklik() {
        return uzaklik;
    }

    public void setUzaklik(String uzaklik) {
        this.uzaklik = uzaklik;
    }

    public String getDurum() {
        return durum;
    }

    public void setDurum(String durum) {
        this.durum = durum;
    }

    public String getFaceprofilur() {
        return faceprofilur;
    }

    public void setFaceprofilur(String faceprofilur) {
        this.faceprofilur = faceprofilur;
    }

    public String getResimpath() {
        return resimpath;
    }

    public void setResimpath(String resimpath) {
        this.resimpath = resimpath;
    }

    public String getResmiacik() {
        return resmiacik;
    }

    public void setResmiacik(String resmiacik) {
        this.resmiacik = resmiacik;
    }

    public String getBandurumu() {
        return bandurumu;
    }

    public void setBandurumu(String bandurumu) {
        this.bandurumu = bandurumu;
    }

    public String getYenimesajvarmi() {
        return yenimesajvarmi;
    }

    public void setYenimesajvarmi(String yenimesajvarmi) {
        this.yenimesajvarmi = yenimesajvarmi;
    }

    public String getKacyenimesaj() {
        return kacyenimesaj;
    }

    public void setKacyenimesaj(String kacyenimesaj) {
        this.kacyenimesaj = kacyenimesaj;
    }

    public String getOkul() {
        return okul;
    }

    public void setOkul(String okul) {
        this.okul = okul;
    }

    public String getBorndate() {
        return borndate;
    }

    public void setBorndate(String borndate) {
        this.borndate = borndate;

    }

    public String getCinsiyet() {
        return cinsiyet;
    }

    public void setCinsiyet(String cinsiyet) {
        this.cinsiyet = cinsiyet;
    }

    private int getAge(int year, int month, int day) {
        int age;

        final Calendar calenderToday = Calendar.getInstance();
        int currentYear = calenderToday.get(Calendar.YEAR);
        int currentMonth = 1 + calenderToday.get(Calendar.MONTH);
        int todayDay = calenderToday.get(Calendar.DAY_OF_MONTH);

        age = currentYear - year;

        if(month > currentMonth){
            --age;
        }
        else if(month == currentMonth){
            if(day > todayDay){
                --age;
            }
        }
        return age;

    }

    public String getCoverphotourl() {
        return coverphotourl;
    }

    public void setCoverphotourl(String coverphotourl) {
        this.coverphotourl = coverphotourl;
    }

    public String getBurc() {
        return burc;
    }

    public void setBurc(String burc) {
        this.burc = burc;
    }
}
