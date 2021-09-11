package com.xica.gymticket.gymticket;

import android.net.Uri;

public class User {

    private String  email, usertype,dob,name,h,w,genderS,city1,phone1,address,dietitian,yoga,trainer,timings,heading,content;
    private Uri pic;

    public User(){

    }
    public Uri getPic() {
        return pic;
    }
    public void setPic(Uri pic) { this.pic = pic; }


    public User(String name,String email, String userType,String dob,String h,String w,String genderS,String city1,String phone1) {
        this.name=name;
        this.email = email;
        this.usertype = userType;
        this.dob=dob;
        this.h=h;
        this.w=w;
        this.genderS=genderS;
        this.city1=city1;
        this.phone1=phone1;
    }

    public User(String name,String email, String userType,String address,String city1,String phone1) {
        this.name=name;
        this.email = email;
        this.usertype = userType;
        this.address=address;
        this.city1=city1;
        this.phone1=phone1;
    }

    public User(String name,String email, String userType,String dietitian,String trainer,String yoga,String genderS,String city1,String phone1,String address) {
        this.name=name;
        this.email = email;
        this.usertype = userType;
        this.dietitian=dietitian;
        this.trainer=trainer;
        this.yoga=yoga;
        this.genderS=genderS;
        this.city1=city1;
        this.phone1=phone1;
        this.address=address;
    }


    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public String getCity1() {
        return city1;
    }

    public String getDob() {
        return dob;
    }

    public String getYoga() {
        return yoga;
    }

    public String getTrainer() {

        return trainer;
    }

    public String getDietitian() {

        return dietitian;
    }

    public String getAddress() {

        return address;
    }

    public String getEmail() {
        return email;
    }

    public String getGenderS() {
        return genderS;
    }

    public String getPhone1() {
        return phone1;
    }

    public String getW() {
        return w;
    }

    public String getH() {
        return h;
    }

    public String getUsertype() {
        return usertype;
    }

    public void setCity1(String city1) {
        this.city1 = city1;
    }

    public void setAddress(String address) {

        this.address = address;
    }

    public void setYoga(String yoga) {
        this.yoga = yoga;
    }

    public void setW(String w) {

        this.w = w;
    }

    public void setUsertype(String usertype) {

        this.usertype = usertype;
    }

    public void setTrainer(String trainer) {

        this.trainer = trainer;
    }

    public void setPhone1(String phone1) {

        this.phone1 = phone1;
    }

    public void setH(String h) {

        this.h = h;
    }

    public void setGenderS(String genderS) {

        this.genderS = genderS;
    }

    public void setEmail(String email) {

        this.email = email;
    }

    public void setDob(String dob) {

        this.dob = dob;
    }

    public void setDietitian(String dietitian) {

        this.dietitian = dietitian;
    }

    public void setHeading(String heading) {
        this.heading = heading;
    }

    public void setContent(String content) {

        this.content = content;
    }

    public String getHeading() {

        return heading;
    }

    public String getContent() {

        return content;
    }
    public String getTimings() {
        return timings;
    }
    public void setTimings(String timings) {
        this.timings = timings;
    }

}
