package com.cursojava.curso.dao;

import com.cursojava.curso.models.Usuario;
import de.mkammerer.argon2.Argon2;
import de.mkammerer.argon2.Argon2Factory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;


import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;


@Repository  //va a tener la funcionalidad de acceder al repositorio de la base de datos
@Transactional // forma en que se trata las consultas SQL
public class UsuarioDaoImp implements UsuarioDao{

    @PersistenceContext
    EntityManager entityManager;

    @Override
    @Transactional
    public List getUsuarios() {
        String query = "FROM Usuario";
        return entityManager.createQuery(query).getResultList();
    }

    @Override
    public void eliminar(Long id) {
        Usuario usuario= entityManager.find(Usuario.class,id);
        entityManager.remove(usuario);
    }

    @Override
    public void registrar(Usuario usuario) {

        entityManager.merge(usuario);
    }


    public Usuario obtenerUsuarioPorCredenciales(Usuario usuario){
       // String email = "' OR 1 = 1 -- '"; sql injection.

        String query = "FROM Usuario WHERE email = :email";
        List<Usuario> lista= entityManager.createQuery(query)
                .setParameter("email", usuario.getEmail())
                .getResultList();

        if (lista.isEmpty()){
            return null;
        }
        String passwordHashed = lista.get(0).getPassword();

        Argon2 argon2 = Argon2Factory.create(Argon2Factory.Argon2Types.ARGON2id);
        if ( argon2.verify(passwordHashed, usuario.getPassword())){

            return lista.get(0);
        }
        return null;


        /*
        if (lista.isEmpty()){
            return false;
        }else
            return true;
        */

       // return !lista.isEmpty();
    }
}
