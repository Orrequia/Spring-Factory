package com.jmb.springfactory.unit.dao;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.List;
import java.util.stream.Stream;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.data.domain.Example;

import com.jmb.springfactory.model.entity.User;
import com.jmb.springfactory.model.factory.user.UserFactory;
import com.jmb.springfactory.model.factory.user.UserSamples;

@RunWith(MockitoJUnitRunner.class)
public class UserMongoServiceTest {

  private static final String SOME_NAME = "some name";

  private static final String SOME_NIF = "some nif";

  private static final List<User> usersFoundStream = UserFactory.createSampleDefaultListUser();

  @InjectMocks
  private UserMongoService userMongoService;
  
  @Mock
  private UserRepository userRepository;
  
  @Before
  public void beforeTest() {
    
    when(userRepository.findAll(any(Example.class))).thenReturn(usersFoundStream);
  }
  
  @Test
  public void whenSearchByNifContainThenInvokeFindAllMethodFromUserRepository() {
    
    userMongoService.findByNifContain(SOME_NIF);
    verify(userRepository, times(1)).findAll(SOME_NIF);
  }

  @Test
  public void whenSearchByNameContainThenInvokeFindAllMethodFromUserRepository() {
    
    userMongoService.findByNameContain(SOME_NAME);
    verify(userRepository, times(1)).findAll(SOME_NAME);
  }
  
  @Test
  public void whenSearchByNifAndSomeOneExistThenReturnListWithResults() {
    
    final String aNifOfExistsUser = UserSamples.NIF_USER_TEST_1;
    
    final Stream<User> usersFound = userMongoService.findByNifContain(aNifOfExistsUser);
    
    assertNotNull(usersFound);
    assertTrue(usersFound.anyMatch(user -> user.getNif().equals(aNifOfExistsUser)));
  }

  @Test
  public void whenSearchByNameAndSomeOneExistThenReturnListWithResults() {
    
    final String aNameOfExistsUser = UserSamples.NAME_USER_TEST_1;
    
    final Stream<User> usersFound = userMongoService.findByNameContain(aNameOfExistsUser);
    
    assertNotNull(usersFound);
    assertTrue(usersFound.anyMatch(user -> user.getName().equals(aNameOfExistsUser)));
  }
}
