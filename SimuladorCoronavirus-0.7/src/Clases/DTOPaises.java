/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Clases;

import java.io.Serializable;

/**
 *
 * @author prado
 */
public class DTOPaises implements Serializable{
    
    public Pais pViejo;
    public Pais pNuevo;
    
    public DTOPaises(Pais p1, Pais p2)
    {
        this.pViejo = p1;
        this.pNuevo = p2;
    }
    
}
