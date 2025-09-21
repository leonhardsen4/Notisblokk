package com.leonhardsen.notisblokk.dao;

import com.leonhardsen.notisblokk.utils.ConnectionFactory;
import javafx.collections.ObservableList;

/**
 * Classe DAO genérica abstrata.
 * @param <T> O tipo da entidade.
 */
public abstract class GenericDAO<T> extends ConnectionFactory {

    public abstract void save(T t);

    public abstract void update(T t);

    public abstract void delete(T t);

    public abstract ObservableList<T> getAll();

}