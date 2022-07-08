package com.example.demo;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

/**
 * Implementation of Spring HATEOAS RepresentationModelAssembler interface. 
 * Converts Employee objects to EntityModel<Employee> to simplify hypertext link creation.
 *  
 * 
 * @authore https://spring.io/guides/tutorials/rest/
 * @author James Tatum
 *
 */
@Component
class EmployeeModelAssembler implements RepresentationModelAssembler<Employee, EntityModel<Employee>> {

  @Override
  public EntityModel<Employee> toModel(Employee employee) {

    return EntityModel.of(employee, //
    	// ask Spring HATEoAS to build a link to EmployeeController's one() method and flag as a self link
        linkTo(methodOn(EmployeeController.class).one(employee.getId())).withSelfRel(),
        // ask Spring HATEOAS to build link to the aggregate root all() and call it "employees"
        linkTo(methodOn(EmployeeController.class).all()).withRel("employees"));
  }
}
