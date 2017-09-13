package com.jmb.springfactory.integration;

import static org.junit.Assert.assertNotNull;

import javax.validation.ValidationException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import com.jmb.springfactory.SpringFactoryApplication;
import com.jmb.springfactory.SpringFactoryTestConfiguration;
import com.jmb.springfactory.model.dto.GroupDto;
import com.jmb.springfactory.model.factory.group.GroupDtoFactory;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {SpringFactoryApplication.class, SpringFactoryTestConfiguration.class})
public class GroupIntegrationTest {

  @Autowired
  private GroupController groupController;
  
  @Autowired
  private MongoTemplate mongoTemplate;
  
  @Test(expected = ValidationException.class)
  public void whenCreateGroupAndExistAnyOneWithTheSameNameThenThrowValidationException() {
    
    final GroupDto newGroupDto = GroupDtoFactory.createSampleDefaultGroupDto();
    
    mongoTemplate.save(newGroupDto);
    groupController.create(newGroupDto);
  }
  
  @Test(expected = ValidationException.class)
  public void whenCreateGroupThatHaveAnyEmptyFieldThenThrowValidationException() {
    
    final GroupDto newGroupDto = GroupDtoFactory.createGroup(null, null, null, null);
    
    groupController.create(newGroupDto);
  }
  
  @Test(expected = ValidationException.class)
  public void whenCreateGroupThatHaveIncorrectFomatHourFieldsThenThrowValidationException() {
    
    final GroupDto newGroupDto = GroupDtoFactory.createGroup(null, null, "asddas", "1231:123123");
    
    groupController.create(newGroupDto);
  }

  @Test
  public void whenCreateGroupTheGroupIdMustNotBeNull() {
    
    final GroupDto newGroupDto = GroupDtoFactory.createSampleDefaultGroupDto();
    newGroupDto.setId(null);
    
    mongoTemplate.save(newGroupDto);
    final GroupDto groupCreated = groupController.create(newGroupDto);
    
    assertNotNull(groupCreated);
    assertNotNull(groupCreated.getId());
  }
}