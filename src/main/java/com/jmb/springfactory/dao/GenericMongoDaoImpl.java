package com.jmb.springfactory.dao;

import java.io.Serializable;
import java.util.Optional;
import java.util.stream.Stream;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.jmb.springfactory.exceptions.PersistenceLayerException;
import com.jmb.springfactory.model.entity.BaseEntity;

public abstract class GenericMongoDaoImpl<T extends BaseEntity, I extends Serializable> extends BaseDao implements 
  GenericMongoDao<T, I> {
  
  public abstract MongoRepository<T, I> getRepository();

  @Override
  public T save(T t) throws PersistenceLayerException {

    checkIfEntityExists(t);
    return this.getRepository().save(t);
  }

  @Override
  public void delete(I id) {
    this.getRepository().delete(id);
  }

  @Override
  public Stream<T> findAll() {
    return this.getRepository().findAll().stream();
  }

  @Override
  public Optional<T> findOne(I id) {
    return Optional.ofNullable(this.getRepository().findOne(id));
  }

  private void checkIfEntityExists(T t) throws PersistenceLayerException {
    Optional.ofNullable(t)
      .orElseThrow(PersistenceLayerException::new);
  }

}
