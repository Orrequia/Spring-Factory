package com.jmb.springfactory.dao.productionorder;

import java.util.stream.Stream;

import com.jmb.springfactory.dao.GenericMySQLService;
import com.jmb.springfactory.exceptions.NotFoundException;
import com.jmb.springfactory.exceptions.PersistenceLayerException;
import com.jmb.springfactory.model.entity.ProductionOrder;

public interface ProductionOrderMySQL extends GenericMySQLService<ProductionOrder, Integer> {

    Stream<ProductionOrder> findAllByScheduleId(Integer scheduleId);

    ProductionOrder save(ProductionOrder order, Integer scheduleId) throws PersistenceLayerException, NotFoundException;

}
