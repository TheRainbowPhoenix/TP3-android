package ml.pho3.tp1;

import java.io.Serializable;

public class Country implements Serializable {
    private String mCapital;
    private String mImgFile;
    private String mLanguage;
    private String mCurrency;
    private int mPopulation;
    private int mArea;

    public Country(String mCapital, String mImgFile, String mLanguage, String mCurrency, int mPopulation, int mArea) {
        this.mCapital = mCapital;
        this.mImgFile = mImgFile;
        this.mLanguage = mLanguage;
        this.mCurrency = mCurrency;
        this.mPopulation = mPopulation;
        this.mArea = mArea;
    }

    public String getmCapital() {
        return mCapital;
    }

    public void setmCapital(String mCapital) {
        this.mCapital = mCapital;
    }

    public String getmImgFile() {
        return mImgFile;
    }

    public void setmImgFile(String mImgFile) {
        this.mImgFile = mImgFile;
    }

    public String getmLanguage() {
        return mLanguage;
    }

    public void setmLanguage(String mLanguage) {
        this.mLanguage = mLanguage;
    }

    public String getmCurrency() {
        return mCurrency;
    }

    public void setmCurrency(String mCurrency) {
        this.mCurrency = mCurrency;
    }

    public int getmPopulation() {
        return mPopulation;
    }

    public void setmPopulation(int mPopulation) {
        this.mPopulation = mPopulation;
    }

    public int getmArea() {
        return mArea;
    }

    public void setmArea(int mArea) {
        this.mArea = mArea;
    }
}
