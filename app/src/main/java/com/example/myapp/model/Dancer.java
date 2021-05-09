package com.example.myapp.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.Objects;

public class Dancer {

    @SerializedName("id")
    @Expose
    private Integer id;

    @SerializedName("code")
    @Expose
    private Integer code;

    @SerializedName("sex")
    @Expose
    private String sex;

    @SerializedName("fullname")
    @Expose
    private String fullname;

    @SerializedName("ratingd")
    private String ratingd;

    @SerializedName("bg")
    private Integer bg;

    @SerializedName("rs")
    private Integer rs;

    @SerializedName("m")
    private Integer m;

    @SerializedName("s")
    private Integer s;

    @SerializedName("ch")
    private Integer ch;

    @SerializedName("resd")
    private Integer resd;

    @SerializedName("club")
    private String club;

    @SerializedName("ratingc")
    private String ratingc;

    @SerializedName("e")
    private Integer e;

    @SerializedName("d")
    private Integer d;

    @SerializedName("c")
    private Integer c;

    @SerializedName("b")
    private Integer b;

    @SerializedName("a")
    private Integer a;

    @SerializedName("resc")
    private Integer resc;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Dancer dancer = (Dancer) o;
        return Objects.equals(id, dancer.id) &&
                Objects.equals(code, dancer.code) &&
                Objects.equals(sex, dancer.sex) &&
                Objects.equals(fullname, dancer.fullname) &&
                Objects.equals(ratingd, dancer.ratingd) &&
                Objects.equals(bg, dancer.bg) &&
                Objects.equals(rs, dancer.rs) &&
                Objects.equals(m, dancer.m) &&
                Objects.equals(s, dancer.s) &&
                Objects.equals(ch, dancer.ch) &&
                Objects.equals(resd, dancer.resd) &&
                Objects.equals(club, dancer.club) &&
                Objects.equals(ratingc, dancer.ratingc) &&
                Objects.equals(e, dancer.e) &&
                Objects.equals(d, dancer.d) &&
                Objects.equals(c, dancer.c) &&
                Objects.equals(b, dancer.b) &&
                Objects.equals(a, dancer.a) &&
                Objects.equals(resc, dancer.resc);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, code, sex, fullname, ratingd, bg, rs, m, s, ch, resd, club, ratingc, e, d, c, b, a, resc);
    }

    public Dancer(Integer code, String sex, String fullname, String ratingd, Integer bg, Integer rs, Integer m, Integer s, Integer ch, Integer resd, String club, String ratingc, Integer e, Integer d, Integer c, Integer b, Integer a, Integer resc) {
        this.code = code;
        this.sex = sex;
        this.fullname = fullname;
        this.ratingd = ratingd;
        this.bg = bg;
        this.rs = rs;
        this.m = m;
        this.s = s;
        this.ch = ch;
        this.resd = resd;
        this.club = club;
        this.ratingc = ratingc;
        this.e = e;
        this.d = d;
        this.c = c;
        this.b = b;
        this.a = a;
        this.resc = resc;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getRatingd() {
        return ratingd;
    }

    public void setRatingd(String ratingd) {
        this.ratingd = ratingd;
    }

    public Integer getBg() {
        return bg;
    }

    public void setBg(Integer bg) {
        this.bg = bg;
    }

    public Integer getRs() {
        return rs;
    }

    public void setRs(Integer rs) {
        this.rs = rs;
    }

    public Integer getM() {
        return m;
    }

    public void setM(Integer m) {
        this.m = m;
    }

    public Integer getS() {
        return s;
    }

    public void setS(Integer s) {
        this.s = s;
    }

    public Integer getCh() {
        return ch;
    }

    public void setCh(Integer ch) {
        this.ch = ch;
    }

    public Integer getResd() {
        return resd;
    }

    public void setResd(Integer resd) {
        this.resd = resd;
    }

    public String getClub() {
        return club;
    }

    public void setClub(String club) {
        this.club = club;
    }

    public String getRatingc() {
        return ratingc;
    }

    public void setRatingc(String ratingc) {
        this.ratingc = ratingc;
    }

    public Integer getE() {
        return e;
    }

    public void setE(Integer e) {
        this.e = e;
    }

    public Integer getD() {
        return d;
    }

    public void setD(Integer d) {
        this.d = d;
    }

    public Integer getC() {
        return c;
    }

    public void setC(Integer c) {
        this.c = c;
    }

    public Integer getB() {
        return b;
    }

    public void setB(Integer b) {
        this.b = b;
    }

    public Integer getA() {
        return a;
    }

    public void setA(Integer a) {
        this.a = a;
    }

    public Integer getResc() {
        return resc;
    }

    public void setResc(Integer resc) {
        this.resc = resc;
    }
}
