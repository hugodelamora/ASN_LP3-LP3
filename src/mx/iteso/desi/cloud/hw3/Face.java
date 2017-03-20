package mx.iteso.desi.cloud.hw3;

public class Face {
    String name;
    Float cofidence;

    public Face() {
        
    }
    
    public Face(String name, Float cofidence) {
        this.name = name;
        this.cofidence = cofidence;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Float getCofidence() {
        return cofidence;
    }

    public void setCofidence(Float cofidence) {
        this.cofidence = cofidence;
    }
    
    
}
