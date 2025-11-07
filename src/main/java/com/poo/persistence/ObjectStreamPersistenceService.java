package com.poo.persistence;

import java.io.*;


public class ObjectStreamPersistenceService {

    private String filePath;

    public ObjectStreamPersistenceService(String filePath) {
        this.setFilePath(filePath);
    }

    private void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getFilePath() {
        return filePath;
    }


    /**
     * Carga los datos desde el archivo binario.
     */
    public DatosPersistidos cargarDatos() throws IOException, ClassNotFoundException {
        File archivo = new File(filePath);

        if (!archivo.exists()) {
            System.out.println("No se encontró " + filePath + ". Creando nuevo almacén de datos.");
            return new DatosPersistidos();
        }

        try (FileInputStream fis = new FileInputStream(filePath);
             ObjectInputStream ois = new ObjectInputStream(fis)) {

            return (DatosPersistidos) ois.readObject();
        }
    }

    /**
     * Guarda el objeto contenedor en el archivo binario.
     */
    public void guardarDatos(DatosPersistidos datos) throws IOException {

        try (FileOutputStream fos = new FileOutputStream(filePath);
             ObjectOutputStream oos = new ObjectOutputStream(fos)) {

            oos.writeObject(datos);
        }
    }
}