package com.moshe.glaz.scrabble.enteties;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

public class Users extends HashMap<String,User> {
    public Users(){}
    public Users(ArrayList<User> list){
        if(list==null){
            return;
        }

        for(User u:list){
            this.put(u.uid,u);
        }
    }

    public ArrayList<User> asList(){
        Collection<User> values = values();

        ArrayList<User> listOfValues
                = new ArrayList();

        for(User u: values){
            listOfValues.add(u);
        }

        return listOfValues;
    }
}
