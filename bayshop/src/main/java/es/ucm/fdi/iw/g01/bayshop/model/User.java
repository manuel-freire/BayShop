package es.ucm.fdi.iw.g01.bayshop.model;

import java.util.ArrayList;
import java.util.Date;
import lombok.Data;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;

// @Entity
// @Data
public class User {
    // @Id
    // @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String email;
    private String password;
    private Product_images image;
    private String username;
    private int baypoints;
    private String rol;    

}
