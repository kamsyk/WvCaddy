package com.lindewiemann.wvcaddy;

public class Month {
    private int id;
    private String name;

    public Month(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    //to display object as a string in spinner
    @Override
    public String toString() {
        return name;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof Month){
            Month c = (Month)obj;
            if(c.getName().equals(name) && c.getId()==id ) return true;
        }

        return false;
    }
}
