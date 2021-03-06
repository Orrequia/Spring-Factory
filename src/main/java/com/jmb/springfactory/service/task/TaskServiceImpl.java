package com.jmb.springfactory.service.task;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.jmb.springfactory.dao.GenericMySQLService;
import com.jmb.springfactory.dao.task.TaskMySQLService;
import com.jmb.springfactory.exceptions.NotFoundException;
import com.jmb.springfactory.exceptions.PersistenceLayerException;
import com.jmb.springfactory.exceptions.ServiceLayerException;
import com.jmb.springfactory.model.bo.BusinessObjectBase;
import com.jmb.springfactory.model.bo.QueryTaskObject;
import com.jmb.springfactory.model.dto.CommentDto;
import com.jmb.springfactory.model.dto.TaskDto;
import com.jmb.springfactory.model.dto.WorkLogDto;
import com.jmb.springfactory.model.entity.Task;
import com.jmb.springfactory.model.enumeration.TaskStatusEnum;
import com.jmb.springfactory.service.GenericServiceImpl;
import com.jmb.springfactory.service.ValidatorService;

import static com.jmb.springfactory.service.UtilsService.notExist;
import static com.jmb.springfactory.service.UtilsService.addIntoList;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import com.jmb.springfactory.service.productionorder.ProductionOrderService;

@Service
public class TaskServiceImpl extends GenericServiceImpl<Task, TaskDto, BusinessObjectBase, Integer> 
  implements TaskService {

  @Autowired
  private TaskMySQLService taskMySQLService;
  
  @Autowired
  private ProductionOrderService productionOrderService;
  
  @Autowired
  @Qualifier("taskValidatorService")
  private ValidatorService taskValidatorService;

  private static final String TASK_INITIAL_STATUS = TaskStatusEnum.OPENED.name();

  @Override
  public GenericMySQLService<Task, Integer> genericDao() {
    return taskMySQLService;
  }

  @Override
  public Class<? extends Task> getClazz() {
    return Task.class;
  }

  @Override
  public Class<? extends TaskDto> getDtoClazz() {
    return TaskDto.class;
  }

  @Override
  public Class<? extends BusinessObjectBase> getBoClazz() {
    return BusinessObjectBase.class;
  }
  
  @Override
  public void update(TaskDto taskDto, Integer idTask) throws ServiceLayerException {
    taskValidatorService.validateOnUpdate(taskDto);
    super.update(taskDto, idTask);
  }
  
  @Override
  public TaskDto save(Integer orderId, TaskDto taskDto) throws ServiceLayerException, NotFoundException, 
    PersistenceLayerException {
    
    addInitialInformation(orderId, taskDto);
    return super.save(taskDto);
  }
  
  @Override 
  public void delete(Integer id) {
    
    final Consumer<Task> updateAsDeleted = task -> {
       task.setStatus(TaskStatusEnum.DELETED); 
       try {
         taskMySQLService.save(task);
       } catch (Exception e) {
         serviceLog.error(String.format("A error has ocurred when update as DELETED the task [%s]", e.getMessage()));
      }
    };

    serviceLog.info(String.format("Search task with id %s", id));
    taskMySQLService.findOne(id).ifPresent(updateAsDeleted);
  }

  /**
   * Add essential information to create a task
   * @param orderId
   * @param taskDto
   * @throws NotFoundException
   * @throws PersistenceLayerException
   */
  private void addInitialInformation(Integer orderId, TaskDto taskDto)
      throws NotFoundException, PersistenceLayerException {

    serviceLog.info(String.format("Adding task into order %s", orderId));
    taskDto.setOrder(productionOrderService.findOne(orderId));
    
    if (notExist(taskDto.getOrderNumber())) {
      serviceLog.info("The order number is not defined");
      taskDto.setOrderNumber(getLastPositionOrder(orderId));
    }

    serviceLog.info(String.format("Set task initial status as %s", TASK_INITIAL_STATUS));
    taskDto.setStatus(TASK_INITIAL_STATUS);
  }

  /**
   * Get the last position order of all task of specific order
   * @param orderId
   * @return
   * @throws PersistenceLayerException
   */
  private Integer getLastPositionOrder(Integer orderId) throws PersistenceLayerException {

    final Integer numberOrders = taskMySQLService.countByOrderId(orderId).intValue();
    final Integer lastNumberOrder = numberOrders + 1;

    serviceLog.info(String.format("There are %s task for order %s, the order of the new one is %s", 
        orderId, numberOrders, lastNumberOrder));

    return lastNumberOrder;
  }

  @Override
  public List<TaskDto> findAll(QueryTaskObject queryParams) {
    serviceLog.info(String.format("Search task with the follow arguments: %s", queryParams.toString()));
    return taskMySQLService.findAll(queryParams).map(this::entityToDto).collect(Collectors.toList());
  }
  
  @SuppressWarnings("unchecked")
  @Override
  public void addComment(Integer idTask, CommentDto comment) throws NotFoundException, ServiceLayerException {
    
    final TaskDto task = findOne(idTask);
    task.setComments((List<CommentDto>)addIntoList(task.getComments(), comment));
    save(task);
  }

  @SuppressWarnings("unchecked")
  @Override
  public void addWorkLog(Integer idTask, WorkLogDto workLog) throws NotFoundException, ServiceLayerException {
    
    final TaskDto task = findOne(idTask);
    task.setWorkLogs((List<WorkLogDto>)addIntoList(task.getWorkLogs(), workLog));
    save(task);
  }
  
  @Override
  public Optional<TaskDto> findOneById(Integer id) {
    return taskMySQLService.findOne(id).map(this::entityToDto);
  }
  
}
